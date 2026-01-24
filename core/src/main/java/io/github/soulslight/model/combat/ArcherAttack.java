package io.github.soulslight.model.combat;

import com.badlogic.gdx.Gdx;

/** Pattern: Strategy (Concrete Strategy) Implements a specific attack behavior (Archer). */
public class ArcherAttack extends AbstractAttack {

  private final float damage;

  public ArcherAttack(float damage) {
    this.damage = damage;
  }

  @Override
  public void attack() {
    Gdx.app.log("ArcherAttack", "Attack executed");
  }

  @Override
  public float getRange() {
    return 100.0f;
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
