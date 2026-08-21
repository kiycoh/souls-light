package io.github.soulslight.model.enemies;

/** GoF Pattern: Factory Method (Concrete Factory) per i nemici del dungeon. */
public class DungeonEnemyFactory implements EnemyFactory {

  @Override
  public AbstractEnemy createMelee() {
    return EnemyRegistry.getEnemy(EnemyKind.CHASER);
  }

  @Override
  public AbstractEnemy createRanged() {
    return EnemyRegistry.getEnemy(EnemyKind.RANGER);
  }

  @Override
  public AbstractEnemy createTank() {
    return EnemyRegistry.getEnemy(EnemyKind.SHIELDER);
  }

  @Override
  public AbstractEnemy createBall() {
    return EnemyRegistry.getEnemy(EnemyKind.SPIKED_BALL);
  }

  @Override
  public AbstractEnemy createBoss() {
    return EnemyRegistry.getEnemy(EnemyKind.OBLIVION);
  }
}
