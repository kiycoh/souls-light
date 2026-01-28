package io.github.soulslight.model.factory.theme;

import io.github.soulslight.model.enemies.DungeonEnemyFactory;
import io.github.soulslight.model.enemies.EnemyFactory;

public class CaveLevelFactory implements GameLevelFactory {
  @Override
  public String getMusicPath() {
    return "audio/cave_theme.mp3";
  }

  @Override
  public float getMusicVolume() {
    return 0.2f;
  }

  @Override
  public EnemyFactory getEnemyFactory() {
    // Assuming Cave uses same enemies for now, or we could make CaveEnemyFactory
    return new DungeonEnemyFactory();
  }
}
