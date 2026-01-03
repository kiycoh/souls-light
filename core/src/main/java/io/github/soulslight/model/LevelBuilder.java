package io.github.soulslight.model;

import com.badlogic.gdx.maps.tiled.TiledMap;

/** Pattern: Builder Constructs complex Level objects step by step. */
public class LevelBuilder {
  private Level level;

  public LevelBuilder() {
    this.level = new Level();
  }

  public LevelBuilder buildMap(TiledMap map) {
    level.setMap(map);
    return this;
  }

  public LevelBuilder spawnEnemies(EnemyFactory factory, int meleeCount, int rangedCount) {
    for (int i = 0; i < meleeCount; i++) {
      Enemy enemy = factory.createMeleeEnemy();
      // Random position logic could go here
      enemy.setPosition(i * 50, 100);
      level.addEnemy(enemy);
    }
    for (int i = 0; i < rangedCount; i++) {
      Enemy enemy = factory.createRangedEnemy();
      enemy.setPosition(i * 50, 200);
      level.addEnemy(enemy);
    }
    return this;
  }

  public LevelBuilder setEnvironment(String musicTrack, float lightLevel) {
    level.setMusicTrack(musicTrack);
    level.setAmbientLight(lightLevel);
    return this;
  }

  public Level build() {
    return level;
  }
}
