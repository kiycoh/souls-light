package io.github.soulslight.model.enemies;

public class DungeonEnemyFactory implements EnemyFactory {

  @Override
  public AbstractEnemy createMelee() {
    return EnemyRegistry.getEnemy("Chaser");
  }

  @Override
  public AbstractEnemy createRanged() {
    return EnemyRegistry.getEnemy("Ranger");
  }

  @Override
  public AbstractEnemy createTank() {
    return EnemyRegistry.getEnemy("Shielder");
  }

  @Override
  public AbstractEnemy createBall() {
    return EnemyRegistry.getEnemy("SpikedBall");
  }

  @Override
  public AbstractEnemy createBoss() {
    return EnemyRegistry.getEnemy("Oblivion");
  }
}
