package io.github.soulslight.model.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import io.github.soulslight.manager.ResourceManager;
import io.github.soulslight.model.room.DoorPosition;
import io.github.soulslight.model.room.RoomData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public record DungeonMapStrategy(long seed, int width, int height)
    implements MapGenerationStrategy {

  private static final int TILE_SIZE = 32;
  private static final int GRID_ROWS = 3;
  private static final int GRID_COLS = 3;

  // Padding inside a grid cell to avoid rooms touching edges too closely
  private static final int CELL_PADDING = 2;

  /** Key for storing room data in map properties. */
  public static final String ROOM_DATA_KEY = "roomData";

  @Override
  public TiledMap generate() {
    var map = new TiledMap();
    map.getProperties().put("width", width);
    map.getProperties().put("height", height);
    map.getProperties().put("tilewidth", TILE_SIZE);
    map.getProperties().put("tileheight", TILE_SIZE);

    var layer = new TiledMapTileLayer(width, height, TILE_SIZE, TILE_SIZE);
    layer.setName("Ground");

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
      // fallback
      StaticTiledMapTile singleFloor = new StaticTiledMapTile(rm.getFloorTextureRegion());
      singleFloor.getProperties().put("type", "floor");
      floorTiles = new StaticTiledMapTile[] {singleFloor};
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

    // Generic wall used during initial fill
    StaticTiledMapTile genericWallTile = wallMaskTiles[15];

    // Inner Corner Tiles
    StaticTiledMapTile innerNeTile = new StaticTiledMapTile(rm.getInnerCornerWallNE());
    innerNeTile.getProperties().put("type", "wall");

    StaticTiledMapTile innerNwTile = new StaticTiledMapTile(rm.getInnerCornerWallNW());
    innerNwTile.getProperties().put("type", "wall");

    StaticTiledMapTile innerSeTile = new StaticTiledMapTile(rm.getInnerCornerWallSE());
    innerSeTile.getProperties().put("type", "wall");

    StaticTiledMapTile innerSwTile = new StaticTiledMapTile(rm.getInnerCornerWallSW());
    innerSwTile.getProperties().put("type", "wall");

    // 1. Fill with generic walls
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        setTile(layer, x, y, genericWallTile);
      }
    }

    var rng = new Random(seed);

    // 2. Generate Grid Path (Guaranteed ~6 rooms)
    int cellW = width / GRID_COLS;
    int cellH = height / GRID_ROWS;
    List<GridPoint> path = generateGridPath(rng, 6);

    // 3. Place Rooms in Path Cells
    List<Room> rooms = new ArrayList<>();
    for (GridPoint p : path) {
      int minX = p.x * cellW + CELL_PADDING;
      int maxX = (p.x + 1) * cellW - CELL_PADDING;
      int minY = p.y * cellH + CELL_PADDING;
      int maxY = (p.y + 1) * cellH - CELL_PADDING;

      int maxRoomW = Math.max(6, maxX - minX);
      int maxRoomH = Math.max(6, maxY - minY);

      // Safety check to ensure bounds are valid
      if (maxRoomW < 6) maxRoomW = 6;
      if (maxRoomH < 6) maxRoomH = 6;

      int w = rng.nextInt(6, Math.min(15, maxRoomW));
      int h = rng.nextInt(6, Math.min(15, maxRoomH));

      int x = rng.nextInt(Math.min(minX, maxX - w + 1), Math.max(minX + 1, maxX - w + 1));
      int y = rng.nextInt(Math.min(minY, maxY - h + 1), Math.max(minY + 1, maxY - h + 1));

      Room room = new Room(x, y, w, h);
      rooms.add(room);
      createRoom(room, layer, floorTiles, rng);
    }

    // 4. Connect Rooms with Organic Tunnels
    Map<Integer, List<DoorPosition>> roomDoors = new HashMap<>();
    for (int i = 0; i < rooms.size(); i++) {
      roomDoors.put(i, new ArrayList<>());
    }

    for (int i = 0; i < rooms.size() - 1; i++) {
      Room r1 = rooms.get(i);
      Room r2 = rooms.get(i + 1);
      createOrganicTunnel(r1, r2, layer, floorTiles, rng);
    }

    // 4b. Detect Doors via perimeter scan
    detectDoors(rooms, layer, roomDoors);

    // 5. Apply Wall Auto-tiling
    applyWallAutotiling(layer, wallMaskTiles, innerNeTile, innerNwTile, innerSeTile, innerSwTile);

    // 6. Store room data
    List<RoomData> roomDataList = new ArrayList<>();
    for (int i = 0; i < rooms.size(); i++) {
      Room r = rooms.get(i);
      float worldX = r.x * TILE_SIZE;
      float worldY = r.y * TILE_SIZE;
      float worldW = r.w * TILE_SIZE;
      float worldH = r.h * TILE_SIZE;

      boolean isPortal = (i == rooms.size() - 1);
      String roomId = "room-" + i;
      List<DoorPosition> doors = roomDoors.getOrDefault(i, Collections.emptyList());

      if (isPortal) {
        roomDataList.add(RoomData.portal(roomId, worldX, worldY, worldW, worldH, doors));
      } else {
        roomDataList.add(RoomData.standard(roomId, worldX, worldY, worldW, worldH, doors));
      }
    }
    map.getProperties().put(ROOM_DATA_KEY, roomDataList);

    map.getLayers().add(layer);
    return map;
  }

  // --- Helper Methods ---

  @SuppressWarnings("unchecked")
  public static List<RoomData> extractRoomData(TiledMap map) {
    Object data = map.getProperties().get(ROOM_DATA_KEY);
    if (data instanceof List<?>) {
      return (List<RoomData>) data;
    }
    return Collections.emptyList();
  }

  /**
   * Generates a path of distinct GridPoints. Retries until a path of desired minLength is found.
   */
  private List<GridPoint> generateGridPath(Random rng, int targetLength) {
    List<GridPoint> bestPath = new ArrayList<>();

    // Attempt up to 20 times to find a path of sufficient length
    for (int attempt = 0; attempt < 20; attempt++) {
      List<GridPoint> path = new ArrayList<>();
      boolean[][] visited = new boolean[GRID_COLS][GRID_ROWS];
      int cx = rng.nextInt(GRID_COLS);
      int cy = rng.nextInt(GRID_ROWS);
      path.add(new GridPoint(cx, cy));
      visited[cx][cy] = true;

      boolean stuck = false;
      while (!stuck) {
        List<GridPoint> neighbors = new ArrayList<>();
        int[] dx = {0, 0, -1, 1};
        int[] dy = {1, -1, 0, 0};

        for (int i = 0; i < 4; i++) {
          int nx = cx + dx[i];
          int ny = cy + dy[i];
          if (nx >= 0 && nx < GRID_COLS && ny >= 0 && ny < GRID_ROWS && !visited[nx][ny]) {
            neighbors.add(new GridPoint(nx, ny));
          }
        }

        if (neighbors.isEmpty()) {
          stuck = true;
        } else {
          GridPoint next = neighbors.get(rng.nextInt(neighbors.size()));
          cx = next.x;
          cy = next.y;
          path.add(next);
          visited[cx][cy] = true;
        }
      }

      if (path.size() >= targetLength || path.size() > bestPath.size()) {
        bestPath = path;
        if (bestPath.size() >= targetLength) break;
      }
    }
    return bestPath;
  }

  private void createRoom(
      Room room, TiledMapTileLayer layer, StaticTiledMapTile[] floorTiles, Random rng) {
    for (int x = room.x; x < room.x + room.w; x++) {
      for (int y = room.y; y < room.y + room.h; y++) {
        StaticTiledMapTile tile = floorTiles[rng.nextInt(floorTiles.length)];
        setTile(layer, x, y, tile);
      }
    }
  }

  /**
   * Creates an "Organic Tunnel" (Guided Drunkard's Walk) between two rooms. Also detects where the
   * tunnel pierces the room walls to place Doors.
   */
  /** Creates an "Organic Tunnel" (Guided Drunkard's Walk) between two rooms. */
  private void createOrganicTunnel(
      Room r1, Room r2, TiledMapTileLayer layer, StaticTiledMapTile[] floorTiles, Random rng) {

    // Start center of R1
    int cx = r1.x + r1.w / 2;
    int cy = r1.y + r1.h / 2;

    // Target center of R2
    int tx = r2.x + r2.w / 2;
    int ty = r2.y + r2.h / 2;

    // Maximum iterations to prevent infinite loops
    int maxSteps = width * height * 2;
    int steps = 0;

    while (steps < maxSteps) {
      if (cx == tx && cy == ty) break;

      // 1. Determine Move
      // Bias towards target (70% chance)
      // Random wobble (30% chance)
      int dx = 0;
      int dy = 0;

      if (rng.nextFloat() < 0.7f) {
        // Move towards target
        dx = Integer.signum(tx - cx);
        dy = Integer.signum(ty - cy);

        // If diagonal, pick one axis randomly to avoid zigzag bias
        if (dx != 0 && dy != 0) {
          if (rng.nextBoolean()) dx = 0;
          else dy = 0;
        }
      } else {
        // Random wobble (4 directions)
        int dir = rng.nextInt(4);
        switch (dir) {
          case 0 -> dx = 1;
          case 1 -> dx = -1;
          case 2 -> dy = 1;
          case 3 -> dy = -1;
        }
      }

      // Apply move
      int nextX = cx + dx;
      int nextY = cy + dy;

      // Bounds check
      if (nextX > 1 && nextX < width - 1 && nextY > 1 && nextY < height - 1) {
        cx = nextX;
        cy = nextY;

        // Dig at current position
        carveRough(layer, cx, cy, floorTiles, rng);
      }
      steps++;
    }
  }

  private void detectDoors(
      List<Room> rooms, TiledMapTileLayer layer, Map<Integer, List<DoorPosition>> roomDoors) {
    for (int i = 0; i < rooms.size(); i++) {
      Room r = rooms.get(i);
      List<DoorPosition> doors = roomDoors.get(i);

      // Scan North Wall (y = r.y + r.h)
      scanEdge(layer, r.x, r.y + r.h, 1, 0, r.w, DoorPosition.Direction.NORTH, doors);
      // Scan South Wall (y = r.y - 1)
      scanEdge(layer, r.x, r.y - 1, 1, 0, r.w, DoorPosition.Direction.SOUTH, doors);
      // Scan East Wall (x = r.x + r.w)
      scanEdge(layer, r.x + r.w, r.y, 0, 1, r.h, DoorPosition.Direction.EAST, doors);
      // Scan West Wall (x = r.x - 1)
      scanEdge(layer, r.x - 1, r.y, 0, 1, r.h, DoorPosition.Direction.WEST, doors);
    }
  }

  private void scanEdge(
      TiledMapTileLayer layer,
      int startX,
      int startY,
      int dx,
      int dy,
      int length,
      DoorPosition.Direction dir,
      List<DoorPosition> doors) {

    int runStart = -1;
    int runLen = 0;

    for (int k = 0; k < length; k++) {
      int tx = startX + k * dx;
      int ty = startY + k * dy;

      if (isFloor(layer, tx, ty)) {
        if (runStart == -1) runStart = k;
        runLen++;
      } else {
        if (runStart != -1) {
          addDoor(doors, startX, startY, dx, dy, runStart, runLen, dir);
          runStart = -1;
          runLen = 0;
        }
      }
    }
    // Final check
    if (runStart != -1) {
      addDoor(doors, startX, startY, dx, dy, runStart, runLen, dir);
    }
  }

  private void addDoor(
      List<DoorPosition> doors,
      int startX,
      int startY,
      int dx,
      int dy,
      int runStart,
      int runLen,
      DoorPosition.Direction dir) {

    // Calculate center of the run in tile coords, then pixels
    // The run starts at index 'runStart' relative to startX/Y
    int firstTileX = startX + runStart * dx;
    int firstTileY = startY + runStart * dy;

    // Center of the run in tiles: firstTile + (runLen - 1) / 2.0
    // But we want pixel center.
    // Pixel of tile T is T * SIZE + SIZE/2.
    // Start Pixel = firstTile * SIZE
    // End Pixel = (firstTile + runLen) * SIZE
    // Center Pixel = (Start + End) / 2

    float startPixelX = firstTileX * TILE_SIZE;
    float startPixelY = firstTileY * TILE_SIZE;

    float endPixelX = (firstTileX + (dx == 1 ? runLen : 1)) * TILE_SIZE;
    float endPixelY = (firstTileY + (dy == 1 ? runLen : 1)) * TILE_SIZE;

    float centerX = (startPixelX + endPixelX) / 2f;
    float centerY = (startPixelY + endPixelY) / 2f;

    float doorLength = runLen * TILE_SIZE;

    doors.add(DoorPosition.of(centerX, centerY, dir, doorLength));
  }

  private boolean isFloor(TiledMapTileLayer layer, int x, int y) {
    if (x < 0 || x >= width || y < 0 || y >= height) return false;
    var cell = layer.getCell(x, y);
    return cell != null
        && cell.getTile() != null
        && "floor".equals(cell.getTile().getProperties().get("type"));
  }

  private void carveRough(
      TiledMapTileLayer layer, int x, int y, StaticTiledMapTile[] floorTiles, Random rng) {
    // Carve a 3x3 area to ensure ~3 tile width for main path
    for (int dx = -1; dx <= 1; dx++) {
      for (int dy = -1; dy <= 1; dy++) {
        // High chance to carve all 3x3, but skips corners sometimes for "rounded" look
        if (Math.abs(dx) + Math.abs(dy) == 2 && rng.nextFloat() > 0.7f) continue;

        int nx = x + dx;
        int ny = y + dy;

        if (nx > 0 && nx < width - 1 && ny > 0 && ny < height - 1) {
          setTile(layer, nx, ny, floorTiles[rng.nextInt(floorTiles.length)]);
        }
      }
    }
  }

  // --- Auto-tiling Logic ---

  private void applyWallAutotiling(
      TiledMapTileLayer layer,
      StaticTiledMapTile[] wallMaskTiles,
      StaticTiledMapTile innerNeTile,
      StaticTiledMapTile innerNwTile,
      StaticTiledMapTile innerSeTile,
      StaticTiledMapTile innerSwTile) {

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if (!isWall(layer, x, y)) continue;

        int mask = computeWallMask(layer, x, y);
        StaticTiledMapTile tile;

        if (mask == 15) {
          boolean neHole = !isWall(layer, x + 1, y + 1);
          boolean nwHole = !isWall(layer, x - 1, y + 1);
          boolean seHole = !isWall(layer, x + 1, y - 1);
          boolean swHole = !isWall(layer, x - 1, y - 1);
          int holes = (neHole ? 1 : 0) + (nwHole ? 1 : 0) + (seHole ? 1 : 0) + (swHole ? 1 : 0);

          if (holes == 1) {
            if (neHole) tile = innerNeTile;
            else if (nwHole) tile = innerNwTile;
            else if (seHole) tile = innerSeTile;
            else tile = innerSwTile;
          } else {
            tile = wallMaskTiles[15];
          }
        } else {
          if (mask < 0 || mask >= wallMaskTiles.length) mask = 15;
          tile = wallMaskTiles[mask];
          if (tile == null) tile = wallMaskTiles[15];
        }

        setTile(layer, x, y, tile);
      }
    }
  }

  private int computeWallMask(TiledMapTileLayer layer, int x, int y) {
    int mask = 0;
    if (isWall(layer, x, y + 1)) mask |= 1; // up
    if (isWall(layer, x + 1, y)) mask |= 2; // right
    if (isWall(layer, x, y - 1)) mask |= 4; // down
    if (isWall(layer, x - 1, y)) mask |= 8; // left
    return mask;
  }

  private void setTile(TiledMapTileLayer layer, int x, int y, StaticTiledMapTile tile) {
    if (layer.getCell(x, y) == null) {
      layer.setCell(x, y, new TiledMapTileLayer.Cell());
    }
    layer.getCell(x, y).setTile(tile);
  }

  private boolean isWall(TiledMapTileLayer layer, int x, int y) {
    if (x < 0 || x >= width || y < 0 || y >= height) return true;
    var cell = layer.getCell(x, y);
    return cell != null
        && cell.getTile() != null
        && "wall".equals(cell.getTile().getProperties().get("type"));
  }

  private record Room(int x, int y, int w, int h) {
    boolean contains(int px, int py) {
      return px >= x && px < x + w && py >= y && py < y + h;
    }
  }

  private record GridPoint(int x, int y) {}
}
