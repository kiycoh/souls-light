package io.github.soulslight.model.map;

import com.badlogic.gdx.maps.tiled.TiledMap;

public class MapGenerator {

  public static final int TILE_SIZE = 32;
  public static final int MAP_WIDTH = 50;
  public static final int MAP_HEIGHT = 50;

  // Cave generation config: wallThreshold is now the initial wall fill percentage
  // 0.45 = 45% walls creates good cave structures with cellular automata
  private static final float NOISE_FREQUENCY = 0.1f; // Unused now, kept for API compatibility
  private static final int NOISE_OCTAVES = 3; // Unused now, kept for API compatibility
  private static final float WALL_THRESHOLD = 0.45f;

  /** Generic entry point: chooses a strategy and generates a map. */
  public static TiledMap generate(MapGenerationStrategy strategy) {
    return strategy.generate();
  }

  /**
   * Procedural map creation method (Using NoiseMapStrategy with default
   * parameters).
   */
  public static TiledMap generateProceduralMap(long seed) {
    MapGenerationStrategy strategy = new NoiseMapStrategy(
        seed, MAP_WIDTH, MAP_HEIGHT, NOISE_FREQUENCY, NOISE_OCTAVES, WALL_THRESHOLD);
    return generate(strategy);
  }
}
