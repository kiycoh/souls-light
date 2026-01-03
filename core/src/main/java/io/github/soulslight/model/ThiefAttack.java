package io.github.soulslight.model;

/** Pattern: Strategy (Concrete Strategy) Implements a specific attack behavior (Thief). */
public class ThiefAttack extends AbstractAttack {
  @Override
  public void attack() {
    System.out.println("Attacco eseguito");
  }

  @Override
  public float getRange() {
    return 0.8f;
  }

  @Override
  public float getDamage() {
    return 8.0f;
  }

  @Override
  public float getAttackSpeed() {
    return 2.0f;
  }

  @Override
  public String getSoundID() {
    return "dagger_sound";
  }
}
