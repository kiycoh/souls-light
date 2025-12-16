package io.github.soulslight.model;

abstract class AbstarctAttack implements AttackStrategy{
    public abstract void attack();
    public abstract float getRange();
    public abstract float getDamage();
    public abstract float getAttackSpeed();
    public abstract String getSoundID();
}
