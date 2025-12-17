package io.github.soulslight.model;

/**
 * Pattern: Strategy (Strategy Interface)
 * Defines the common interface for all supported attack algorithms.
 */
public interface AttackStrategy {
    void attack();
    float getRange();
    float getDamage();
    float getAttackSpeed();
    String getSoundID();

}
