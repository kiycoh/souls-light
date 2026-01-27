package io.github.soulslight.model.factory.theme;

import io.github.soulslight.model.enemies.DungeonEnemyFactory;
import io.github.soulslight.model.enemies.EnemyFactory;

public class DungeonLevelFactory implements GameLevelFactory {
  @Override
  public String getMusicPath() {
    return "audio/dungeon_theme.mp3";
  }

  @Override
  public float getMusicVolume() {
    return 0.3f;
  }

  @Override
  public EnemyFactory getEnemyFactory() {
    return new DungeonEnemyFactory();
  }
}
