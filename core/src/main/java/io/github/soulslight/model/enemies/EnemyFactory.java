package io.github.soulslight.model.enemies;

public interface EnemyFactory {
  // Chaser
  AbstractEnemy createMelee();

  // Ranger
  AbstractEnemy createRanged();

  // Shielder
  AbstractEnemy createTank();

  // SpikedBall
  AbstractEnemy createBall();

  // Oblivion
  AbstractEnemy createBoss();
}
