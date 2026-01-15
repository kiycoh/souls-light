package io.github.soulslight.model;

import com.badlogic.gdx.maps.tiled.TiledMap;

public sealed interface MapGenerationStrategy permits DungeonMapStrategy, NoiseMapStrategy {
  TiledMap generate();
}
