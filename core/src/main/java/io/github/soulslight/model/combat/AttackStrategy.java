package io.github.soulslight.model.combat;

import io.github.soulslight.model.entities.Entity;
import java.util.List;

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
