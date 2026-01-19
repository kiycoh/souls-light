package io.github.soulslight.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.soulslight.manager.ResourceManager;

/** Pattern: Factory (Simple Factory) Creates enemies using prototypes. */
public class EnemyFactory {

  private final Enemy meleePrototype;
  private final Enemy rangedPrototype;

  public EnemyFactory() {
    // Initialize melee prototype (e.g., Skeleton/Goblin style)
    meleePrototype = new Chaser(35, 80, new WarriorAttack(20.0f));
    meleePrototype.setTextureRegion(
        new TextureRegion(ResourceManager.getInstance().getEnemyTexture()));

    // Initialize ranged prototype (e.g., Archer/Mage style)
    rangedPrototype = new Ranger(20, 50, new ArcherAttack(10.0f));
    rangedPrototype.setTextureRegion(
        new TextureRegion(ResourceManager.getInstance().getEnemyTexture()));
  }

  public Enemy createMeleeEnemy() {
    return meleePrototype.clone();
  }

  public Enemy createRangedEnemy() {
    return rangedPrototype.clone();
  }
}
