package io.github.soulslight.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.soulslight.manager.ResourceManager;

/** Pattern: Abstract Factory (Concrete Factory) Creates enemies for the Forest environment. */
public class ForestEnemyFactory implements EnemyFactory {

  private final Enemy goblinPrototype;
  private final Enemy elfArcherPrototype;

  public ForestEnemyFactory() {
    // Initialize prototypes
    goblinPrototype = new Chaser(30, 80, new WarriorAttack());
    goblinPrototype.setTextureRegion(
        new TextureRegion(ResourceManager.getInstance().getEnemyTexture()));

    elfArcherPrototype = new Ranger(20, 50, new ArcherAttack());
    elfArcherPrototype.setTextureRegion(
        new TextureRegion(ResourceManager.getInstance().getEnemyTexture()));
  }

  @Override
  public Enemy createMeleeEnemy() {
    return goblinPrototype.clone();
  }

  @Override
  public Enemy createRangedEnemy() {
    return elfArcherPrototype.clone();
  }
}
