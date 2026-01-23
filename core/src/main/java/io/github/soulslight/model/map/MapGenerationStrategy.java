package io.github.soulslight.model.map;

import com.badlogic.gdx.maps.tiled.TiledMap;

/**
 * Pattern: Strategy (Abstract Strategy)
 * Sealed interface for map generation algorithms.
 */
public sealed interface MapGenerationStrategy
    permits DungeonMapStrategy, NoiseMapStrategy, BossArenaStrategy {
  TiledMap generate();
}
