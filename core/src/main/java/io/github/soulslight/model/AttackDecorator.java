package io.github.soulslight.model;
import java.util.*;
/** Pattern: Decorator Abstract decorator for AttackStrategy to add dynamic behaviors. */
public abstract class AttackDecorator implements AttackStrategy {
  protected AttackStrategy wrappedAttack;

  public AttackDecorator(AttackStrategy attack) {
    this.wrappedAttack = attack;
  }

  @Override
  public void attack() {
    wrappedAttack.attack();
  }

  @Override
  public void executeAttack(Entity attacker, List<Entity> targets) {
        this.wrappedAttack.executeAttack(attacker, targets);
    }

  @Override
  public float getRange() {
    return wrappedAttack.getRange();
  }

  @Override
  public float getDamage() {
    return wrappedAttack.getDamage();
  }

  @Override
  public float getAttackSpeed() {
    return wrappedAttack.getAttackSpeed();
  }

  @Override
  public String getSoundID() {
    return wrappedAttack.getSoundID();
  }
}
