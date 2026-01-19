package io.github.soulslight.model;

import com.badlogic.gdx.maps.tiled.TiledMap;

public interface MapGenerationStrategy {
  TiledMap generate();
}
