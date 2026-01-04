package io.github.soulslight.model;

/** Pattern: Abstract Factory Interface for creating families of enemies. */
public interface EnemyFactory {
  Enemy createMeleeEnemy();

  Enemy createRangedEnemy();
}
