package io.github.soulslight.model;

/** Pattern: Strategy (Abstract Strategy) Provides a base class for attack strategies. */
public abstract class AbstractAttack implements AttackStrategy {
  public abstract void attack();

  public abstract float getRange();

  public abstract float getDamage();

  public abstract float getAttackSpeed();

  public abstract String getSoundID();

}
