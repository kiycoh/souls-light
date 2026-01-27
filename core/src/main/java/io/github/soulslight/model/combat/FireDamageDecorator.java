package io.github.soulslight.model.combat;

import io.github.soulslight.model.entities.Entity;
import java.util.List;

/** GoF Pattern: Decorator (ConcreteDecorator) Adds Fire effect to the attack. */
public class FireDamageDecorator extends AttackDecorator {

  private static final float BURN_DURATION = 3.0f;
  private static final float BURN_DAMAGE = 5.0f; // Total over duration

  public FireDamageDecorator(AttackStrategy wrapped) {
    super(wrapped);
  }

  @Override
  public void executeAttack(Entity attacker, List<Entity> targets) {
    // Perform normal attack (Damage + Knockback)
    super.executeAttack(attacker, targets);

    // Add Fire Effect to hits

    for (Entity target : targets) {
      if (isValidTarget(attacker, target)) {
        applyBurn(target);
      }
    }
  }

  private boolean isValidTarget(Entity attacker, Entity target) {
    if (attacker == target) return false;
    if (attacker instanceof io.github.soulslight.model.entities.Player
        && target instanceof io.github.soulslight.model.entities.Player)
      return false; // Friendly fire?

    float dist = attacker.getPosition().dst(target.getPosition());
    if (dist > getRange()) return false;

    return true;
  }

  private void applyBurn(Entity target) {
    // Logic to apply Desired Buff/Debuff.
    com.badlogic.gdx.Gdx.app.log(
        "FireDecorator", "Applied Burn to " + target.getClass().getSimpleName());
    target.takeDamage(1.0f); // Bonus fire damage instant
  }

  @Override
  public String getSoundID() {
    // Could mix sounds or return fire sound
    return "fire_swing";
  }
}
