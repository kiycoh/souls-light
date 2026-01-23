package io.github.soulslight.model.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.GridPoint2;
import io.github.soulslight.manager.ResourceManager;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public record NoiseMapStrategy(
    long seed, int width, int height, float frequency, int octaves, float wallThreshold)
    implements MapGenerationStrategy {

  private static final int TILE_SIZE = 32;
  // Increased iterations for better cave formation
  private static final int SMOOTHING_ITERATIONS = 5;
  // Border padding to ensure closed caves
  private static final int BORDER_SIZE = 2;

  @Override
  public TiledMap generate() {
    var map = new TiledMap();
    map.getProperties().put("width", width);
    map.getProperties().put("height", height);
    map.getProperties().put("tilewidth", TILE_SIZE);
    map.getProperties().put("tileheight", TILE_SIZE);

    var layer = new TiledMapTileLayer(width, height, TILE_SIZE, TILE_SIZE);

    ResourceManager rm = ResourceManager.getInstance();

    // --- FLOOR TILES ---
    TextureRegion[] floorRegions = rm.getFloorTextureRegions();
    StaticTiledMapTile[] floorTiles;

    if (floorRegions != null && floorRegions.length > 0) {
      floorTiles = new StaticTiledMapTile[floorRegions.length];
      for (int i = 0; i < floorRegions.length; i++) {
        StaticTiledMapTile t = new StaticTiledMapTile(floorRegions[i]);
        t.getProperties().put("type", "floor");
        floorTiles[i] = t;
      }
    } else {
      // fallback: if for some reason no variants are available, use the old single
      // floor tile
      StaticTiledMapTile singleFloor = new StaticTiledMapTile(rm.getFloorTextureRegion());
      singleFloor.getProperties().put("type", "floor");
      floorTiles = new StaticTiledMapTile[] { singleFloor };
    }

    // --- WALL TILES ---
    TextureRegion[] wallRegions = rm.getWallMaskRegions();
    StaticTiledMapTile[] wallMaskTiles = new StaticTiledMapTile[16];

    TextureRegion fallbackWallRegion = rm.getWallTextureRegion();

    for (int i = 0; i < 16; i++) {
      TextureRegion region = null;
      if (wallRegions != null && i < wallRegions.length) {
        region = wallRegions[i];
      }
      if (region == null) {
        region = fallbackWallRegion;
      }
      StaticTiledMapTile t = new StaticTiledMapTile(region);
      t.getProperties().put("type", "wall");
      wallMaskTiles[i] = t;
    }

    // generic wall used during generation
    StaticTiledMapTile genericWallTile = wallMaskTiles[15];

    StaticTiledMapTile innerNeTile = new StaticTiledMapTile(rm.getInnerCornerWallNE());
    innerNeTile.getProperties().put("type", "wall");

    StaticTiledMapTile innerNwTile = new StaticTiledMapTile(rm.getInnerCornerWallNW());
    innerNwTile.getProperties().put("type", "wall");

    StaticTiledMapTile innerSeTile = new StaticTiledMapTile(rm.getInnerCornerWallSE());
    innerSeTile.getProperties().put("type", "wall");

    StaticTiledMapTile innerSwTile = new StaticTiledMapTile(rm.getInnerCornerWallSW());
    innerSwTile.getProperties().put("type", "wall");

    Random rnd = new Random(seed);

    // === CAVE GENERATION: White Noise Initialization ===
    // Use wallThreshold as initial wall fill percentage (e.g., 0.45 = 45% walls)
    // This replaces the coherent noise approach for proper cave formation
    float initialFillPercent = wallThreshold > 0 && wallThreshold < 1 ? wallThreshold : 0.45f;

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        // Force walls at borders to ensure closed caves
        if (x < BORDER_SIZE || x >= width - BORDER_SIZE
            || y < BORDER_SIZE || y >= height - BORDER_SIZE) {
          setTile(layer, x, y, genericWallTile);
        } else {
          // Random fill: white noise seed for cellular automata
          if (rnd.nextFloat() < initialFillPercent) {
            setTile(layer, x, y, genericWallTile);
          } else {
            StaticTiledMapTile selectedTile = floorTiles[rnd.nextInt(floorTiles.length)];
            setTile(layer, x, y, selectedTile);
          }
        }
      }
    }

    // Apply cellular automata smoothing (4-5 rule)
    for (int i = 0; i < SMOOTHING_ITERATIONS; i++) {
      smoothMap(layer, genericWallTile, floorTiles, rnd);
    }

    List<List<GridPoint2>> regions = getRegions(layer);

    System.out.println("Cave Generation Log:");
    System.out.println("Found " + regions.size() + " disconnected regions.");
    for (int i = 0; i < regions.size(); i++) {
      System.out.println("Region " + i + " Size: " + regions.get(i).size() + " tiles.");
    }

    if (regions.size() > 1) {
      connectRegions(regions, layer, floorTiles, rnd);
    }

    applyWallAutotiling(layer, wallMaskTiles, innerNeTile, innerNwTile, innerSeTile, innerSwTile);

    map.getLayers().add(layer);
    return map;
  }

  private void smoothMap(
      TiledMapTileLayer layer,
      StaticTiledMapTile wallTile,
      StaticTiledMapTile[] floorTiles,
      Random rnd) {

    boolean[][] nextState = new boolean[width][height]; // true = wall, false = floor

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        int wallNeighbors = countWallNeighbors(layer, x, y);
        boolean isWall = isWall(layer, x, y);

        // 4-5 Rule
        // If a cell is a wall and has >= 4 wall neighbors, it stays a wall.
        // If a cell is a floor and has >= 5 wall neighbors, it becomes a wall.
        if (isWall) {
          nextState[x][y] = (wallNeighbors >= 4);
        } else {
          nextState[x][y] = (wallNeighbors >= 5);
        }
      }
    }

    // Apply next state
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if (nextState[x][y]) {
          setTile(layer, x, y, wallTile);
        } else {
          StaticTiledMapTile selectedTile = floorTiles[rnd.nextInt(floorTiles.length)];
          setTile(layer, x, y, selectedTile);
        }
      }
    }
  }

  private int countWallNeighbors(TiledMapTileLayer layer, int cx, int cy) {
    int count = 0;
    for (int x = cx - 1; x <= cx + 1; x++) {
      for (int y = cy - 1; y <= cy + 1; y++) {
        if (x == cx && y == cy)
          continue;

        // out of bounds counts as wall (forces closed caves)
        if (x < 0 || x >= width || y < 0 || y >= height) {
          count++;
        } else if (isWall(layer, x, y)) {
          count++;
        }
      }
    }
    return count;
  }

  private List<List<GridPoint2>> getRegions(TiledMapTileLayer layer) {
    List<List<GridPoint2>> regions = new ArrayList<>();
    boolean[][] visited = new boolean[width][height];

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if (!visited[x][y] && isFloor(layer, x, y)) {
          List<GridPoint2> region = new ArrayList<>();
          floodFill(x, y, layer, visited, region);
          if (!region.isEmpty()) {
            regions.add(region);
          }
        }
      }
    }
    return regions;
  }

  private void floodFill(
      int startX,
      int startY,
      TiledMapTileLayer layer,
      boolean[][] visited,
      List<GridPoint2> region) {

    Queue<GridPoint2> queue = new LinkedList<>();
    GridPoint2 start = new GridPoint2(startX, startY);
    queue.add(start);
    visited[startX][startY] = true;

    int[] dx = { 0, 0, 1, -1 };
    int[] dy = { 1, -1, 0, 0 };

    while (!queue.isEmpty()) {
      GridPoint2 current = queue.poll();
      region.add(current);

      for (int i = 0; i < 4; i++) {
        int nx = current.x + dx[i];
        int ny = current.y + dy[i];

        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
          if (!visited[nx][ny] && isFloor(layer, nx, ny)) {
            visited[nx][ny] = true;
            queue.add(new GridPoint2(nx, ny));
          }
        }
      }
    }
  }

  private void connectRegions(
      List<List<GridPoint2>> regions,
      TiledMapTileLayer layer,
      StaticTiledMapTile[] floorTiles,
      Random rnd) {

    regions.sort((r1, r2) -> Integer.compare(r2.size(), r1.size()));

    List<GridPoint2> mainRegion = regions.get(0);

    for (int i = 1; i < regions.size(); i++) {
      List<GridPoint2> otherRegion = regions.get(i);

      GridPoint2 bestMain = null;
      GridPoint2 bestOther = null;
      double minDistance = Double.MAX_VALUE;

      for (GridPoint2 pMain : mainRegion) {
        for (GridPoint2 pOther : otherRegion) {
          double dist = Math.pow(pMain.x - pOther.x, 2) + Math.pow(pMain.y - pOther.y, 2);
          if (dist < minDistance) {
            minDistance = dist;
            bestMain = pMain;
            bestOther = pOther;
          }
        }
      }

      if (bestMain != null && bestOther != null) {
        createCorridor(bestMain, bestOther, layer, floorTiles, rnd);
      }
    }
  }

  private void createCorridor(
      GridPoint2 start,
      GridPoint2 end,
      TiledMapTileLayer layer,
      StaticTiledMapTile[] floorTiles,
      Random rnd) {

    int x = start.x;
    int y = start.y;

    while (x != end.x) {
      StaticTiledMapTile selectedTile = floorTiles[rnd.nextInt(floorTiles.length)];
      setTile(layer, x, y, selectedTile);
      x += (end.x > x) ? 1 : -1;
    }

    while (y != end.y) {
      StaticTiledMapTile selectedTile = floorTiles[rnd.nextInt(floorTiles.length)];
      setTile(layer, x, y, selectedTile);
      y += (end.y > y) ? 1 : -1;
    }

    StaticTiledMapTile selectedTile = floorTiles[rnd.nextInt(floorTiles.length)];
    setTile(layer, end.x, end.y, selectedTile);
  }

  private void applyWallAutotiling(
      TiledMapTileLayer layer,
      StaticTiledMapTile[] wallMaskTiles,
      StaticTiledMapTile innerNeTile,
      StaticTiledMapTile innerNwTile,
      StaticTiledMapTile innerSeTile,
      StaticTiledMapTile innerSwTile) {

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if (!isWall(layer, x, y))
          continue;

        int mask = computeWallMask(layer, x, y);
        StaticTiledMapTile tile;

        if (mask == 15) {
          boolean neWall = isWall(layer, x + 1, y + 1);
          boolean nwWall = isWall(layer, x - 1, y + 1);
          boolean seWall = isWall(layer, x + 1, y - 1);
          boolean swWall = isWall(layer, x - 1, y - 1);

          boolean neHole = !neWall;
          boolean nwHole = !nwWall;
          boolean seHole = !seWall;
          boolean swHole = !swWall;

          int holes = (neHole ? 1 : 0) + (nwHole ? 1 : 0) + (seHole ? 1 : 0) + (swHole ? 1 : 0);

          if (holes == 1) {
            if (neHole) {
              tile = innerNeTile;
            } else if (nwHole) {
              tile = innerNwTile;
            } else if (seHole) {
              tile = innerSeTile;
            } else {
              tile = innerSwTile;
            }
          } else {
            tile = wallMaskTiles[15];
          }
        } else {
          if (mask < 0 || mask >= wallMaskTiles.length) {
            mask = 15;
          }
          tile = wallMaskTiles[mask];
          if (tile == null) {
            tile = wallMaskTiles[15];
          }
        }

        setTile(layer, x, y, tile);
      }
    }
  }

  private int computeWallMask(TiledMapTileLayer layer, int x, int y) {
    int mask = 0;

    if (isWall(layer, x, y + 1))
      mask |= 1; // up
    if (isWall(layer, x + 1, y))
      mask |= 2; // right
    if (isWall(layer, x, y - 1))
      mask |= 4; // down
    if (isWall(layer, x - 1, y))
      mask |= 8; // left

    return mask;
  }

  private void setTile(TiledMapTileLayer layer, int x, int y, StaticTiledMapTile tile) {
    if (layer.getCell(x, y) == null) {
      layer.setCell(x, y, new TiledMapTileLayer.Cell());
    }
    layer.getCell(x, y).setTile(tile);
  }

  private boolean isFloor(TiledMapTileLayer layer, int x, int y) {
    if (x < 0 || x >= width || y < 0 || y >= height)
      return false;
    var cell = layer.getCell(x, y);
    return cell != null
        && cell.getTile() != null
        && "floor".equals(cell.getTile().getProperties().get("type"));
  }

  private boolean isWall(TiledMapTileLayer layer, int x, int y) {
    if (x < 0 || x >= width || y < 0 || y >= height)
      return false;
    var cell = layer.getCell(x, y);
    return cell != null
        && cell.getTile() != null
        && "wall".equals(cell.getTile().getProperties().get("type"));
  }
}
