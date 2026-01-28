package io.github.soulslight.model.pathfinding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import io.github.soulslight.manager.PathfindingManager;
import io.github.soulslight.utils.GdxTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxTestExtension.class)
class DungeonGraphTest {

  @Test
  void testGraphCreationFromLayer() {
    // Create a 5x5 layer
    TiledMapTileLayer layer = new TiledMapTileLayer(5, 5, 32, 32);

    // Fill with floor
    StaticTiledMapTile floorTile =
        new StaticTiledMapTile((com.badlogic.gdx.graphics.g2d.TextureRegion) null);
    floorTile.getProperties().put("type", "floor");

    StaticTiledMapTile wallTile =
        new StaticTiledMapTile((com.badlogic.gdx.graphics.g2d.TextureRegion) null);
    wallTile.getProperties().put("type", "wall");

    // Center wall at 2,2
    for (int x = 0; x < 5; x++) {
      for (int y = 0; y < 5; y++) {
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        if (x == 2 && y == 2) {
          cell.setTile(wallTile);
        } else {
          cell.setTile(floorTile);
        }
        layer.setCell(x, y, cell);
      }
    }

    DungeonGraph graph = new DungeonGraph(layer);

    assertEquals(25, graph.getNodeCount());

    DungeonNode center = graph.getNode(2, 2);
    assertTrue(center.isWall);

    DungeonNode left = graph.getNode(1, 2);
    assertFalse(left.isWall);

    // Check connections
    PathfindingManager pfm = new PathfindingManager(layer);

    // Start at 1,2 (Left of wall). End at 3,2 (Right of wall).
    // Should go around.

    // World coordinates: tile center is x*32+16
    float startX = 1 * 32 + 16;
    float startY = 2 * 32 + 16;

    float endX = 3 * 32 + 16;
    float endY = 2 * 32 + 16;

    DefaultGraphPath<DungeonNode> path = pfm.findPath(startX, startY, endX, endY);

    assertNotNull(path);
    assertTrue(path.getCount() > 0);

    // Path should avoid 2,2
    for (DungeonNode node : path) {
      assertFalse(node.x == 2 && node.y == 2, "Path should not pass through wall at 2,2");
    }
  }
}
