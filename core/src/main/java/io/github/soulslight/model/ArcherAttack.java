package io.github.soulslight.model;
import java.util.List;
/** Pattern: Strategy (Concrete Strategy) Implements a specific attack behavior (Archer). */
public class ArcherAttack extends AbstractAttack {
  @Override
  public void attack() {
    System.out.println("Attacco eseguito");
  }

    @Override
    public float getRange(){
        return 100.0f;
    }
    @Override
    public  float getDamage(){
        return 7.0f;
    }

    @Override
    public  float getAttackSpeed(){
        return 1.5f;
    }
    @Override
    public  String getSoundID(){
        return "bow_sound";
    }

    @Override
    public void executeAttack(Entity attacker, List<Entity> targets) {
        // Logica semplice: colpisci il primo che è a tiro
        for (Entity target : targets) {
            // Verifica distanza
            if (attacker.getPosition().dst(target.getPosition()) <= getRange()) {
                target.takeDamage(getDamage());
            }
        }
    }
}
