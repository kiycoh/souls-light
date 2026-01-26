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

  /**
   * Checks if this strategy or any of its wrappers is of the specified type.
   *
   * @param type The class type to check for
   * @return True if the strategy contains the decorator/type, false otherwise
   */
  default boolean hasDecorator(Class<?> type) {
    return false;
  }
}
