package io.github.soulslight.model.factory.theme;

import io.github.soulslight.model.enemies.EnemyFactory;

/**
 * GoF Pattern: Abstract Factory Defines the interface for creating families of related objects
 * (Theme assets).
 */
public interface GameLevelFactory {
  String getMusicPath();

  float getMusicVolume();

  EnemyFactory getEnemyFactory();
}
