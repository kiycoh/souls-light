package io.github.soulslight.model.combat;

import com.badlogic.gdx.Gdx;

/**
 * Pattern: Strategy (Concrete Strategy) Implements a specific attack behavior
 * (Thief).
 */
public class ThiefAttack extends AbstractAttack {
  @Override
  public void attack() {
    Gdx.app.log("ThiefAttack", "Attack executed");
  }

  private final float damage;

  public ThiefAttack(float damage) {
    this.damage = damage;
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
    return 0.8f;
  }

  @Override
  public String getSoundID() {
    return "dagger_sound";
  }
}
