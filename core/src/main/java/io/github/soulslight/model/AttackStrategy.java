package io.github.soulslight.model;
import java.util.*;

public interface AttackStrategy {
    void attack();
    float getRange();
    float getDamage();
    float getAttackSpeed();
    String getSoundID();
    void executeAttack(Entity attacker, List<Entity> targets);
}
