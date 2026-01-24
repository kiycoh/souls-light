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
    return 45.0f;
  }

  @Override
  public float getDamage() {
    return this.damage;
  }

  @Override
  public float getAttackSpeed() {
    return 1.0f;
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
}
