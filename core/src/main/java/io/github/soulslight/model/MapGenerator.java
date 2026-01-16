package io.github.soulslight.model;

import com.badlogic.gdx.maps.tiled.TiledMap;

public class MapGenerator {

  public static final int TILE_SIZE = 32;
  public static final int MAP_WIDTH = 50;
  public static final int MAP_HEIGHT = 50;

  public static TiledMap generate(MapGenerationStrategy strategy) {
    return strategy.generate();
  }
}
