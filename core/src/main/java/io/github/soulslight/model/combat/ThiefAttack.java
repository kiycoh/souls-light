package io.github.soulslight.model.combat;

import com.badlogic.gdx.Gdx;
import io.github.soulslight.model.entities.Entity;
import java.util.List;

/** Pattern: Strategy (Concrete Strategy) Implements a specific attack behavior (Thief). */
public class ThiefAttack extends AbstractAttack {
  @Override
  public void attack() {
    Gdx.app.log("ThiefAttack", "Attack executed");
  }

  private final float damage;

  public ThiefAttack(float damage) {
    this.damage = damage;
  }

  @Override
  public float getRange() {
    return 0.8f;
  }

  @Override
  public float getDamage() {
    return this.damage;
  }

  @Override
  public float getAttackSpeed() {
    return 2.0f;
  }

  @Override
  public String getSoundID() {
    return "dagger_sound";
  }

  @Override
  public void executeAttack(Entity attacker, List<Entity> targets) {
    // Logica:  colpisci il primo che è a tiro
    for (Entity target : targets) {
      // Verifica distanza
      if (attacker.getPosition().dst(target.getPosition()) <= getRange()) {
        target.takeDamage(getDamage());
      }
    }
  }
}
