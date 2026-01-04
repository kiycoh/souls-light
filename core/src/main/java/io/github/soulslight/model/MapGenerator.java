// ---------------------------------------------------------
// COMPONENT: io.github.soulslight.model.MapGenerator
// OPTIMIZATION: Zero-Allocation Noise / Flyweight Tiles
// ---------------------------------------------------------

package io.github.soulslight.model;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import io.github.soulslight.manager.ResourceManager;
import make.some.noise.Noise;

public class MapGenerator {

  public static final int TILE_SIZE = 32;
  public static final int MAP_WIDTH = 50;
  public static final int MAP_HEIGHT = 50;

  // Noise Settings
  private static final float NOISE_FREQUENCY = 0.1f;
  private static final int NOISE_OCTAVES = 3;
  private static final float WALL_THRESHOLD = 0.2f;

  public static TiledMap generateProceduralMap(long seed) {
    // 1. Setup Data Structures (Stack-allocated references)
    var map = new TiledMap();
    var layer = new TiledMapTileLayer(MAP_WIDTH, MAP_HEIGHT, TILE_SIZE, TILE_SIZE);

    // 2. Configure Noise (Zero-Allocation after init)
    // Using 'var' for local type inference (Java 21 style)
    var noise = new Noise();
    noise.setSeed((int) seed);
    noise.setNoiseType(Noise.VALUE_FRACTAL);
    noise.setFrequency(NOISE_FREQUENCY);
    noise.setFractalOctaves(NOISE_OCTAVES);

    // 3. Flyweight Pattern: Create Tile instances ONCE, reuse them.
    // Use specific regions from ResourceManager
    var wallTile = new StaticTiledMapTile(ResourceManager.getInstance().getWallTextureRegion());
    var floorTile = new StaticTiledMapTile(ResourceManager.getInstance().getFloorTextureRegion());

    // Differentiate visually if possible (e.g., using properties or blend colors if the API
    // supported it easily without allocs)
    wallTile.getProperties().put("type", "wall");
    floorTile.getProperties().put("type", "floor");

    // 4. Generation Loop
    for (int x = 0; x < MAP_WIDTH; x++) {
      for (int y = 0; y < MAP_HEIGHT; y++) {
        // Get noise value [-1.0, 1.0]
        float n = noise.getConfiguredNoise(x, y);

        // Determine tile type using Switch Expression (Java 21) logic
        // (Here we use simple if/else for the flyweight selection)
        StaticTiledMapTile selectedTile = (n > WALL_THRESHOLD) ? wallTile : floorTile;

        // Create Cell (Necessary allocation for TiledMap structure)
        var cell = new TiledMapTileLayer.Cell();
        cell.setTile(selectedTile);

        // Set Cell in Layer
        layer.setCell(x, y, cell);
      }
    }

    map.getLayers().add(layer);
    return map;
  }
}
