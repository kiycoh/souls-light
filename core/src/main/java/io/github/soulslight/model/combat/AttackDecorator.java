package io.github.soulslight.model.combat;

import io.github.soulslight.model.entities.Entity;
import java.util.List;

/**
 * GoF Pattern: Decorator (Decorator) Wrapper for AttackStrategy that allows dynamic behavior
 * extension (Buffs). Follows the ResponsibleWorker/Employee style where it delegates to the wrapped
 * component.
 */
public abstract class AttackDecorator implements AttackStrategy {

  protected final AttackStrategy wrapped;

  public AttackDecorator(AttackStrategy wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public void attack() {
    wrapped.attack();
  }

  @Override
  public float getRange() {
    return wrapped.getRange();
  }

  @Override
  public float getDamage() {
    return wrapped.getDamage();
  }

  @Override
  public float getAttackSpeed() {
    return wrapped.getAttackSpeed();
  }

  @Override
  public String getSoundID() {
    return wrapped.getSoundID();
  }

  @Override
  public void executeAttack(Entity attacker, List<Entity> targets) {
    wrapped.executeAttack(attacker, targets);
  }

  @Override
  public boolean hasDecorator(Class<?> type) {
    if (type.isInstance(this)) {
      return true;
    }
    return wrapped.hasDecorator(type);
  }
}
