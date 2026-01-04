package io.github.soulslight.model;
import java.util.*;

/**
 * Pattern: Strategy (Strategy Interface) Defines the common interface for all supported attack
 * algorithms.
 */
public interface AttackStrategy {
    void attack();
    float getRange();
    float getDamage();
    float getAttackSpeed();
    String getSoundID();
    void executeAttack(Entity attacker, List<Entity> targets);
}
