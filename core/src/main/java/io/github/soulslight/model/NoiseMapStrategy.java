package io.github.soulslight.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.GridPoint2;

import io.github.soulslight.manager.ResourceManager;
import make.some.noise.Noise;

public record NoiseMapStrategy(long seed, int width, int height, float frequency, int octaves, float wallThreshold) implements MapGenerationStrategy {

  private static final int TILE_SIZE = 32;
  private static final int SMOOTHING_ITERATIONS = 3;

  @Override
  public TiledMap generate() {
    var map = new TiledMap();
    var layer = new TiledMapTileLayer(width, height, TILE_SIZE, TILE_SIZE);

    var noise = new Noise();
    noise.setSeed((int) seed);
    noise.setNoiseType(Noise.VALUE);
    noise.setFrequency(frequency);
    noise.setFractalOctaves(octaves);

    var wallTile = new StaticTiledMapTile(ResourceManager.getInstance().getWallTextureRegion());
    var floorTile = new StaticTiledMapTile(ResourceManager.getInstance().getFloorTextureRegion());

    wallTile.getProperties().put("type", "wall");
    floorTile.getProperties().put("type", "floor");

    // 1. Initial Generation
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        float n = noise.getConfiguredNoise(x, y);
        // Using configurable threshold. Values > threshold are WALLS.
        StaticTiledMapTile selectedTile = (n > wallThreshold) ? wallTile : floorTile;
        setTile(layer, x, y, selectedTile);
      }
    }
    
    // 2. Cellular Automata Smoothing
    for (int i = 0; i < SMOOTHING_ITERATIONS; i++) {
        smoothMap(layer, wallTile, floorTile);
    }

    // 3. Identify Regions (Rooms)
    List<List<GridPoint2>> regions = getRegions(layer);
    
    System.out.println("Map Generation Log:");
    System.out.println("Found " + regions.size() + " disconnected regions.");
    for (int i = 0; i < regions.size(); i++) {
        System.out.println("Region " + i + " Size: " + regions.get(i).size() + " tiles.");
    }

    // 4. Connect Regions
    if (regions.size() > 1) {
        connectRegions(regions, layer, floorTile);
    }

    map.getLayers().add(layer);
    return map;
  }
  
  private void smoothMap(TiledMapTileLayer layer, StaticTiledMapTile wallTile, StaticTiledMapTile floorTile) {
      // Create a buffer to store next state
      boolean[][] nextState = new boolean[width][height]; // true = wall, false = floor
      
      for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
              int wallNeighbors = countWallNeighbors(layer, x, y);
              
              // Standard "4-5 Rule" for cave generation:
              // If a cell is a wall and has >= 4 wall neighbors, it stays a wall.
              // If a cell is a floor and has >= 5 wall neighbors, it becomes a wall.
              boolean isWall = isWall(layer, x, y);
              
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
              setTile(layer, x, y, nextState[x][y] ? wallTile : floorTile);
          }
      }
  }
  
  private int countWallNeighbors(TiledMapTileLayer layer, int cx, int cy) {
      int count = 0;
      for (int x = cx - 1; x <= cx + 1; x++) {
          for (int y = cy - 1; y <= cy + 1; y++) {
              if (x == cx && y == cy) continue;
              
              // Out of bounds counts as wall (forces closed caves)
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
  
  private void floodFill(int startX, int startY, TiledMapTileLayer layer, boolean[][] visited, List<GridPoint2> region) {
      Queue<GridPoint2> queue = new LinkedList<>();
      GridPoint2 start = new GridPoint2(startX, startY);
      queue.add(start);
      visited[startX][startY] = true;
      
      int[] dx = {0, 0, 1, -1};
      int[] dy = {1, -1, 0, 0};
      
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
  
  private void connectRegions(List<List<GridPoint2>> regions, TiledMapTileLayer layer, StaticTiledMapTile floorTile) {
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
              createCorridor(bestMain, bestOther, layer, floorTile);
          }
      }
  }
  
  private void createCorridor(GridPoint2 start, GridPoint2 end, TiledMapTileLayer layer, StaticTiledMapTile floorTile) {
      int x = start.x;
      int y = start.y;
      
      while (x != end.x) {
          setTile(layer, x, y, floorTile);
          x += (end.x > x) ? 1 : -1;
      }
      
      while (y != end.y) {
          setTile(layer, x, y, floorTile);
          y += (end.y > y) ? 1 : -1;
      }
      setTile(layer, end.x, end.y, floorTile);
  }
  
  private void setTile(TiledMapTileLayer layer, int x, int y, StaticTiledMapTile tile) {
      if (layer.getCell(x, y) == null) {
          layer.setCell(x, y, new TiledMapTileLayer.Cell());
      }
      layer.getCell(x, y).setTile(tile);
  }
  
  private boolean isFloor(TiledMapTileLayer layer, int x, int y) {
      return layer.getCell(x, y) != null && "floor".equals(layer.getCell(x, y).getTile().getProperties().get("type"));
  }
  
  private boolean isWall(TiledMapTileLayer layer, int x, int y) {
      return layer.getCell(x, y) != null && "wall".equals(layer.getCell(x, y).getTile().getProperties().get("type"));
  }
}