package io.github.soulslight.model;

/** Pattern: Strategy (Abstract Strategy) Provides a base class for attack strategies. */


public abstract class AbstractAttack implements AttackStrategy {
  @Override
  public abstract void attack();

  @Override
  public abstract float getRange();

  @Override
  public abstract float getDamage();

  @Override
  public abstract float getAttackSpeed();

  @Override
  public abstract String getSoundID();

}
