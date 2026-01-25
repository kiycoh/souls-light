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
    if (targets == null || targets.isEmpty()) return;

    List<Entity> finalTargets = selectTargets(attacker, targets);
    for (Entity target : finalTargets) {
      performAttack(attacker, target);
    }
  }

  /**
   * Hook method to select which entities from the candidate list should receive the attack. Default
   * implementation: All entities within range/shape.
   */
  protected List<Entity> selectTargets(Entity attacker, List<Entity> candidates) {
    List<Entity> validTargets = new java.util.ArrayList<>();
    for (Entity target : candidates) {
      if (isInRange(attacker, target)) {
        validTargets.add(target);
      }
    }
    return validTargets;
  }

  /**
   * Hook method to define the specific action taken against a target. Default implementation: Deal
   * damage and trigger onHit effects.
   */
  protected void performAttack(Entity attacker, Entity target) {
    target.takeDamage(getDamage());
    onHit(target, attacker);
  }

  /**
   * Helper method for single-target strategies. Returns a list containing the single nearest valid
   * target, or empty if none.
   */
  protected List<Entity> findNearestTarget(Entity attacker, List<Entity> candidates) {
    Entity nearest = null;
    float minDst = Float.MAX_VALUE;

    for (Entity e : candidates) {
      if (e.isDead()) continue;
      float dst = attacker.getPosition().dst(e.getPosition());
      if (dst < minDst) {
        minDst = dst;
        nearest = e;
      }
    }

    if (nearest != null && isInRange(attacker, nearest)) {
      return java.util.Collections.singletonList(nearest);
    }
    return java.util.Collections.emptyList();
  }

  /**
   * Checks if the target is in valid attack range/shape. Default is simple distance check.
   * Subclasses can override for shapes (Cone, Rect).
   */
  protected boolean isInRange(Entity attacker, Entity target) {
    return attacker.getPosition().dst(target.getPosition()) <= getRange();
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
