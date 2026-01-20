package io.github.soulslight.model.combat;

import com.badlogic.gdx.Gdx;
import io.github.soulslight.model.entities.Entity;
import java.util.List;

/** Pattern: Decorator (Concrete Decorator) Adds fire damage effect to an attack. */
public class FireDamageDecorator extends AttackDecorator {

  public FireDamageDecorator(AttackStrategy attack) {
    super(attack);
  }

  @Override
  public void attack() {
    super.attack();
    Gdx.app.log("FireDamageDecorator", "... with Fire Effect!");
  }

  @Override
  public void executeAttack(Entity attacker, List<Entity> targets) {
    // EXECUTES BASE ATTACK
    super.executeAttack(attacker, targets);

    Gdx.app.log("FireDamageDecorator", "... 🔥 BRUCIA! (+5 Danni Fuoco) 🔥");

    for (Entity target : targets) {
      if (attacker.getPosition().dst(target.getPosition()) <= super.getRange()) {
        // APPLIES EXTRA FIRE DAMAGE
        target.takeDamage(5.0f);
      }
    }
  }

  @Override
  public float getDamage() {
    return super.getDamage() + 5.0f; // Bonus fire damage
  }

  @Override
  public float getRange() {
    return super.getRange();
  }

  @Override
  public float getAttackSpeed() {
    return super.getAttackSpeed();
  }

  @Override
  public String getSoundID() {
    return super.getSoundID();
  }
}
