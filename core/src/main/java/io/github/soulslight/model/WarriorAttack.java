package io.github.soulslight.model;
import java.util.*;

public class WarriorAttack extends AbstarctAttack {

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
