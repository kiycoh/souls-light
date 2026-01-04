package io.github.soulslight.model;
import java.util.*;

/** Pattern: Strategy (Concrete Strategy) Implements a specific attack behavior (Warrior). */
public class WarriorAttack extends AbstractAttack {

    @Override
    public void attack() {
        System.out.println("Attacco eseguito");
    }

    @Override
    public float getRange() {
        return 1.0f;
    }

    @Override
    public float getDamage() {
        return 20.0f;
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
        // Logica : colpisci il primo che è a tiro
        for (Entity target : targets) {
            // Verifica distanza
            if (attacker.getPosition().dst(target.getPosition()) <= getRange()) {
                target.takeDamage(getDamage());
            }
        }
    }
}
