package io.github.soulslight.model;

import com.badlogic.gdx.Gdx;
import java.util.List;

/** Pattern: Strategy (Concrete Strategy) Implements a specific attack behavior (Warrior). */
public class WarriorAttack extends AbstractAttack {

    private final float damage;
    public WarriorAttack(float damage) {
        this.damage = damage;
    }
  @Override
  public void attack() {
    Gdx.app.log("WarriorAttack", "Attack executed");
  }

  @Override
  public float getRange() {
    return 45.0f;
  }

  @Override
  public float getDamage() {
    return this.damage;
  }

  @Override
  public float getAttackSpeed() {
    return 1.0f;
  }

  @Override
  public String getSoundID() {
    return "sword_swing";
  }

  @Override
  public void executeAttack(Entity attacker, List<Entity> targets) {
    // Logica: colpisci il primo che è a tiro
    for (Entity target : targets) {
      // Verifica distanza
      if (attacker.getPosition().dst(target.getPosition()) <= getRange()) {
        target.takeDamage(getDamage());
      }
    }
  }
}
