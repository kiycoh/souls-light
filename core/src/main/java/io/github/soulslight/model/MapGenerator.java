package io.github.soulslight.model;

import com.badlogic.gdx.maps.tiled.TiledMap;

public class MapGenerator {

  public static final int TILE_SIZE = 32;
  public static final int MAP_WIDTH = 50;
  public static final int MAP_HEIGHT = 50;

  // noise base default config
  private static final float NOISE_FREQUENCY = 0.1f;
  private static final int NOISE_OCTAVES = 3;
  private static final float WALL_THRESHOLD = 0.2f;

  /**
   * Generic entry point: chooses a strategy and generates a map.
   */
  public static TiledMap generate(MapGenerationStrategy strategy) {
    return strategy.generate();
  }

  /**
   * Procedural map creation method
   * (Using NoiseMapStrategy with default parameters).
   */
  public static TiledMap generateProceduralMap(long seed) {
    MapGenerationStrategy strategy =
        new NoiseMapStrategy(seed, MAP_WIDTH, MAP_HEIGHT, NOISE_FREQUENCY, NOISE_OCTAVES, WALL_THRESHOLD);
    return generate(strategy);
  }
}
