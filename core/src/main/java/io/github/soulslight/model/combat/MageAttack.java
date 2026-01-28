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
  protected void performNoTargetAttack(io.github.soulslight.model.entities.Entity attacker) {
    if (attacker instanceof io.github.soulslight.model.entities.Player p) {
      com.badlogic.gdx.math.Vector2 targetPos =
          p.getPosition().cpy().add(p.getFacingDirection().scl(getRange()));
      p.notifyProjectileRequest(
          p.getPosition(), targetPos, "fireball", getDamage()); // Linear fallback
    }
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
    return 1.5f;
  }

  @Override
  public String getSoundID() {
    return "fireball_sound";
  }
}
