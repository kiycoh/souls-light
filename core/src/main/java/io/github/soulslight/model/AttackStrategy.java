package io.github.soulslight.model;

public interface AttackStrategy {
    void attack();
    float getRange();
    float getDamage();
    float getAttackSpeed();
    String getSoundID();

}
