package io.github.soulslight.model;

import java.util.*;

/** Pattern: Decorator (Concrete Decorator) Adds fire damage effect to an attack. */
public class FireDamageDecorator extends AttackDecorator {

  public FireDamageDecorator(AttackStrategy attack) {
    super(attack);
  }

  @Override
  public void attack() {
    super.attack();
    System.out.println("... with Fire Effect!");
  }

    @Override
    public void executeAttack(Entity attacker, List<Entity> targets) {
        // ESEGUE L'ATTACCO BASE
        super.executeAttack(attacker, targets);

        System.out.println("... 🔥 BRUCIA! (+5 Danni Fuoco) 🔥");

        for (Entity target : targets) {
            if (attacker.getPosition().dst(target.getPosition()) <= super.getRange()) {
                //APPLICA IL DANNO EXTRA DA FUOCO
                target.takeDamage(5.0f);
            }
        }
    }

  @Override
  public float getDamage() {
    return super.getDamage() + 5.0f; // Bonus fire damage
  }

  @Override
    public float getRange(){
      return super.getRange();
  }

  @Override
    public float getAttackSpeed() {
      return super.getAttackSpeed();
  }

  @Override
    public String getSoundID(){
      return super.getSoundID();
  }
}
