package io.github.soulslight.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.soulslight.manager.ResourceManager;

/** Pattern: Abstract Factory (Concrete Factory) Creates enemies for the Dungeon environment. */
public class DungeonEnemyFactory implements EnemyFactory {

  private final Enemy skeletonPrototype;
  private final Enemy darkMagePrototype;

  public DungeonEnemyFactory() {
    skeletonPrototype = new Chaser(40, 80, new WarriorAttack());
    skeletonPrototype.setTextureRegion(
        new TextureRegion(ResourceManager.getInstance().getEnemyTexture()));

    darkMagePrototype = new Ranger(25, 50, new MageAttack());
    darkMagePrototype.setTextureRegion(
        new TextureRegion(ResourceManager.getInstance().getEnemyTexture()));
  }

  @Override
  public Enemy createMeleeEnemy() {
    return skeletonPrototype.clone();
  }

  @Override
  public Enemy createRangedEnemy() {
    return darkMagePrototype.clone();
  }
}
