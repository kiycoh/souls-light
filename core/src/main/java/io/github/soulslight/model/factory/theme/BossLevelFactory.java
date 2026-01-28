package io.github.soulslight.model.factory.theme;

import io.github.soulslight.model.enemies.DungeonEnemyFactory;
import io.github.soulslight.model.enemies.EnemyFactory;

public class BossLevelFactory implements GameLevelFactory {
  @Override
  public String getMusicPath() {
    return "audio/boss_theme.mp3";
  }

  @Override
  public float getMusicVolume() {
    return 0.1f;
  }

  @Override
  public EnemyFactory getEnemyFactory() {
    return new DungeonEnemyFactory();
  }
}
