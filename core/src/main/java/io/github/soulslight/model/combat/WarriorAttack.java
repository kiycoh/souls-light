package io.github.soulslight.model.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.Entity;

/** Pattern: Strategy (Concrete Strategy) Implements a specific attack behavior (Warrior). */
public class WarriorAttack extends AbstractAttack {

  private final float damage;

  public WarriorAttack(float damage) {
    this.damage = damage;
  }

  @Override
  public void attack() {
    Gdx.app.log("WarriorAttack", "Attack executed");
  }

  @Override
  public float getRange() {
    return 100.0f; // Increased range
  }

  @Override
  protected boolean isInRange(Entity attacker, Entity target) {
    // Distance Check
    if (attacker.getPosition().dst(target.getPosition()) > getRange()) return false;

    // Direction Check (Cone)
    // Direction Check (Cone)
    Vector2 facing = null;

    if (attacker instanceof io.github.soulslight.model.entities.Player p) {
      facing = p.getFacingDirection();
    } else if (attacker.getBody() != null) {
      facing = attacker.getBody().getLinearVelocity().cpy();
      if (facing.len2() < 0.01f) {
        return true; // No velocity (and not Player), assume hit 360 or no hit? original was true
      }
      facing.nor();
    }

    if (facing != null) {
      Vector2 toTarget = target.getPosition().cpy().sub(attacker.getPosition()).nor();
      float dot = facing.dot(toTarget);

      // dot > 0.5 (~60 degrees total arc), dot > 0.707 (45 degrees / 90 total)
      return dot > 0.5f; // 0.5 is pretty wide
    }
    return true;
  }

  @Override
  public float getDamage() {
    return this.damage;
  }

  @Override
  public float getAttackSpeed() {
    return 1.3f;
  }

  @Override
  public String getSoundID() {
    return "sword_swing";
  }

  // Knockback configuration
  private static final float KNOCKBACK_FORCE = 300f;
  private static final float KNOCKBACK_DURATION = 0.2f;

  @Override
  protected void onHit(Entity target, Entity attacker) {
    if (target instanceof AbstractEnemy enemy) {
      // Calculate Knockback Direction (Away from attacker)
      Vector2 knockbackDir = target.getPosition().cpy().sub(attacker.getPosition());
      enemy.applyKnockback(knockbackDir, KNOCKBACK_FORCE, KNOCKBACK_DURATION);
    }
  }

  @Override
  protected void performNoTargetAttack(Entity attacker) {
    // Just play sound/log, animation logic is handled by Player cooldown
    Gdx.app.log("WarriorAttack", "Whiffed attack (Air Swing)");
    io.github.soulslight.manager.AudioManager.getInstance().playSound(getSoundID());
  }
}
