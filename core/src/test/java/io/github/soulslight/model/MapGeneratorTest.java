package io.github.soulslight.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import io.github.soulslight.utils.GdxTestExtension;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxTestExtension.class)
class MapGeneratorTest {

  @Test
  void testDungeonStrategyGeneration() {
    // This assumes we have a DungeonMapStrategy
    // We want 50x50 map
    long seed = 12345L;
    MapGenerationStrategy strategy = new DungeonMapStrategy(seed, 50, 50);
    
    TiledMap map = MapGenerator.generate(strategy);
    
    assertNotNull(map, "Map should not be null");
    TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
    assertNotNull(layer, "Map should have a layer");
    
    // Check if we have at least one floor tile (rooms generated)
    boolean hasFloor = false;
    for(int x = 0; x < 50; x++) {
        for(int y = 0; y < 50; y++) {
            if(layer.getCell(x, y) != null && 
               "floor".equals(layer.getCell(x, y).getTile().getProperties().get("type"))) {
                hasFloor = true;
                break;
            }
        }
    }
    assertTrue(hasFloor, "Dungeon should contain floor tiles");
  }

  @Test
  void testNoiseStrategyGeneration() {
      long seed = 12345L;
      // New signature: seed, width, height, frequency, octaves, wallThreshold
      MapGenerationStrategy strategy = new NoiseMapStrategy(seed, 50, 50, 0.1f, 3, -0.1f);
      
      TiledMap map = MapGenerator.generate(strategy);
      
      assertNotNull(map);
      TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
      assertNotNull(layer);
      // Noise strategy fills everywhere, so check 0,0
      assertNotNull(layer.getCell(0, 0));
  }

  @Test
  void testDungeonCorridorWidth() {
      long seed = 12345L;
      // Use a custom strategy size if needed, but 50x50 is standard
      MapGenerationStrategy strategy = new DungeonMapStrategy(seed, 50, 50);
      TiledMap map = MapGenerator.generate(strategy);
      TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
      
      // We expect at least one 3x3 block of floors (indicating wide rooms/corridors)
      boolean hasWideArea = false;
      for (int x = 1; x < 49; x++) {
          for (int y = 1; y < 49; y++) {
              if (isFloor(layer, x, y) && isFloor(layer, x+1, y) && isFloor(layer, x+2, y) &&
                  isFloor(layer, x, y+1) && isFloor(layer, x+1, y+1) && isFloor(layer, x+2, y+1) &&
                  isFloor(layer, x, y+2) && isFloor(layer, x+1, y+2) && isFloor(layer, x+2, y+2)) {
                  hasWideArea = true;
                  break;
              }
          }
          if (hasWideArea) break;
      }
      assertTrue(hasWideArea, "Dungeon should contain at least 3x3 open areas (wide corridors/rooms)");
  }
  
  @Test
  void testNoiseMapConnectivity() {
      long seed = 12345L;
      // Use parameters that are likely to cause disconnections without the fix
      MapGenerationStrategy strategy = new NoiseMapStrategy(seed, 50, 50, 0.2f, 3, -0.1f);
      TiledMap map = MapGenerator.generate(strategy);
      TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);

      // 1. Count total floor tiles
      int totalFloorTiles = 0;
      GridPoint2 startNode = null;
      for (int x = 0; x < 50; x++) {
          for (int y = 0; y < 50; y++) {
              if (isFloor(layer, x, y)) {
                  totalFloorTiles++;
                  if (startNode == null) startNode = new GridPoint2(x, y);
              }
          }
      }

      if (totalFloorTiles == 0) return; // Empty map is technically connected? or fail.

      // 2. Flood fill from startNode
      int connectedTiles = 0;
      Set<GridPoint2> visited = new HashSet<>();
      Queue<GridPoint2> queue = new LinkedList<>();
      
      queue.add(startNode);
      visited.add(startNode);
      connectedTiles++;

      int[] dx = {0, 0, 1, -1};
      int[] dy = {1, -1, 0, 0};

      while (!queue.isEmpty()) {
          GridPoint2 current = queue.poll();
          
          for (int i = 0; i < 4; i++) {
              int nx = current.x + dx[i];
              int ny = current.y + dy[i];
              GridPoint2 next = new GridPoint2(nx, ny);
              
              if (nx >= 0 && nx < 50 && ny >= 0 && ny < 50 && 
                  isFloor(layer, nx, ny) && !visited.contains(next)) {
                  visited.add(next);
                  queue.add(next);
                  connectedTiles++;
              }
          }
      }

      // 3. Assert connectivity
      assertEquals(totalFloorTiles, connectedTiles, "All floor tiles should be connected in a single region.");
  }

  private boolean isFloor(TiledMapTileLayer layer, int x, int y) {
      return layer.getCell(x, y) != null && "floor".equals(layer.getCell(x, y).getTile().getProperties().get("type"));
  }
}
