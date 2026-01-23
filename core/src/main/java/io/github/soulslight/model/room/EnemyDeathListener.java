package io.github.soulslight.model.room;

import io.github.soulslight.model.enemies.AbstractEnemy;

/**
 * Pattern: Observer Listener interface for enemy death events. Allows rooms to react to enemy
 * deaths without tight coupling.
 */
public interface EnemyDeathListener {

  /**
   * Called when an enemy dies.
   *
   * @param enemy The enemy that died
   */
  void onEnemyDied(AbstractEnemy enemy);
}
