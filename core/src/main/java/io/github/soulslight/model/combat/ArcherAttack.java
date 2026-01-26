package io.github.soulslight.model.combat;

import com.badlogic.gdx.Gdx;

/** Pattern: Strategy (Concrete Strategy) Implements a specific attack behavior (Archer). */
public class ArcherAttack extends AbstractAttack {

  private final float damage;

  public ArcherAttack(float damage) {
    this.damage = damage;
  }

  @Override
  protected java.util.List<io.github.soulslight.model.entities.Entity> selectTargets(
      io.github.soulslight.model.entities.Entity attacker,
      java.util.List<io.github.soulslight.model.entities.Entity> candidates) {
    // Also uses single nearest target, like Mage
    return findNearestTarget(attacker, candidates);
  }

  @Override
  protected void performAttack(
      io.github.soulslight.model.entities.Entity attacker,
      io.github.soulslight.model.entities.Entity target) {

    ((io.github.soulslight.model.entities.Player) attacker)
        .notifyProjectileRequest(
            attacker.getPosition(),
            target.getPosition(), // Target Position (non-homing)
            "fast_arrow",
            getDamage());
  }

  @Override
  public void attack() {
    Gdx.app.log("ArcherAttack", "Shooting Fast Arrow");
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
    return "bow_sound";
  }
}
