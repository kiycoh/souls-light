package io.github.soulslight.model.combat;

import com.badlogic.gdx.Gdx;
import io.github.soulslight.model.entities.Entity;
import java.util.List;

/** Pattern: Strategy (Concrete Strategy) Implements a specific attack behavior (Mage). */
public class MageAttack extends AbstractAttack {

  private final float damage;

  public MageAttack(float damage) {
    this.damage = damage;
  }

  @Override
  public void attack() {
    Gdx.app.log("MageAttack", "Attack executed");
  }

  @Override
  public float getRange() {
    return 300.0f;
  }

  @Override
  public float getDamage() {
    return this.damage;
  }

  @Override
  public float getAttackSpeed() {
    return 0.5f;
  }

  @Override
  public String getSoundID() {
    return "stick_sound";
  }

  @Override
  public void executeAttack(Entity attacker, List<Entity> targets) {
    // Logica colpisci il primo che è a tiro
    for (Entity target : targets) {
      // Verifica distanza
      if (attacker.getPosition().dst(target.getPosition()) <= getRange()) {
        target.takeDamage(getDamage());
      }
    }
  }
}
