package io.github.soulslight.model.combat;

import io.github.soulslight.model.entities.Entity;
import java.util.List;

public class ContactDamageAttack extends AbstractAttack {

  @Override
  public float getRange() {
    return 40.0f;
  }

  @Override
  public float getDamage() {
    return 20.0f;
  }

  @Override
  public float getAttackSpeed() {
    return 0.0f;
  }

  @Override
  public String getSoundID() {
    return "impact_sound"; // Impact sound
  }

  @Override
  public void executeAttack(Entity attacker, List<Entity> targets) {
    for (Entity target : targets) {
      if (attacker.getPosition().dst(target.getPosition()) <= getRange()) {
        target.takeDamage(getDamage());
      }
    }
  }

  @Override
  public void attack() {
    // Empty method
  }
}
