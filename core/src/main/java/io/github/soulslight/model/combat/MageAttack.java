package io.github.soulslight.model.combat;

import com.badlogic.gdx.Gdx;

/** Pattern: Strategy (Concrete Strategy) Implements a specific attack behavior (Mage). */
public class MageAttack extends AbstractAttack {

  private final float damage;

  public MageAttack(float damage) {
    this.damage = damage;
  }

  @Override
  protected java.util.List<io.github.soulslight.model.entities.Entity> selectTargets(
      io.github.soulslight.model.entities.Entity attacker,
      java.util.List<io.github.soulslight.model.entities.Entity> candidates) {
    return findNearestTarget(attacker, candidates);
  }

  @Override
  protected void performAttack(
      io.github.soulslight.model.entities.Entity attacker,
      io.github.soulslight.model.entities.Entity target) {

    ((io.github.soulslight.model.entities.Player) attacker)
        .notifyProjectileRequest(
            attacker.getPosition(),
            target, // Pass the Entity target for homing
            "homing_fireball",
            getDamage());
  }

  @Override
  public void attack() {
    Gdx.app.log("MageAttack", "Shooting Tracking Projectile");
  }

  @Override
  public float getRange() {
    return 300.0f;
  }

  @Override
  public float getDamage() {
    return this.damage;
  }

  @Override
  public float getAttackSpeed() {
    return 0.8f; // Slower than instant hit
  }

  @Override
  public String getSoundID() {
    return "fireball_sound";
  }
}
