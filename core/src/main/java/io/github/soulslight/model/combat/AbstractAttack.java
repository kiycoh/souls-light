package io.github.soulslight.model.combat;

import io.github.soulslight.model.entities.Entity;
import java.util.List;

/**
 * Pattern: Strategy (Abstract Strategy) / Template Method Provides a base class for attack
 * strategies with default attack execution.
 */
public abstract class AbstractAttack implements AttackStrategy {

  @Override
  public void executeAttack(Entity attacker, List<Entity> targets) {
    for (Entity target : targets) {
      if (attacker.getPosition().dst(target.getPosition()) <= getRange()) {
        target.takeDamage(getDamage());
        onHit(target, attacker);
      }
    }
  }

  /**
   * Template Method hook. Called when an attack successfully hits a target. Subclasses can override
   * to add special effects (knockback, teleport, etc.).
   *
   * @param target The entity that was hit
   * @param attacker The entity that performed the attack
   */
  protected void onHit(Entity target, Entity attacker) {
    // Default: no special effect
  }

  @Override
  public abstract void attack();

  @Override
  public abstract float getRange();

  @Override
  public abstract float getDamage();

  @Override
  public abstract float getAttackSpeed();

  @Override
  public abstract String getSoundID();
}
