package io.github.soulslight.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class ShielderAttack extends AbstractAttack {

  private static final float KNOCKBACK_FORCE = 500.0f; // Push force

  @Override
  public float getRange() {
    return 40.0f; // Short range
  }

  @Override
  public float getDamage() {
    return 0.0f; // No damage, push only
  }

  @Override
  public float getAttackSpeed() {
    return 1.0f; // Can push every second
  }

  @Override
  public String getSoundID() {
    return "shield_bash";
  }

  @Override
  public void executeAttack(Entity attacker, List<Entity> targets) {
    for (Entity target : targets) {
      // Checks distance
      if (attacker.getPosition().dst(target.getPosition()) <= getRange()) {

        // Calculate push direction: (Target - Attacker)
        // The vector must point TOWARDS the target (away from the attacker)
        Vector2 knockbackDir = target.getPosition().cpy().sub(attacker.getPosition());

        // Apply Knockback (if target is a Player)
        if (target instanceof Player) {
          ((Player) target).applyKnockback(knockbackDir, KNOCKBACK_FORCE);
          Gdx.app.log("ShielderAttack", "SHIELD BASH! Il player è stato respinto.");
        }
      }
    }
  }

  @Override
  public void attack() {}
}
