package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class ShielderAttack extends AbstractAttack {

    private final float KNOCKBACK_FORCE = 500.0f; // Forza della spinta

    @Override
    public float getRange() {
        return 40.0f; // Range corto
    }

    @Override
    public float getDamage() {
        return 0.0f; // Nessun danno, solo spinta
    }

    @Override
    public float getAttackSpeed() {
        return 1.0f; // Può spingere ogni secondo
    }

    @Override
    public String getSoundID() {
        return "shield_bash";
    }

    @Override
    public void executeAttack(Entity attacker, List<Entity> targets) {
        for (Entity target : targets) {
            // Controlla distanza
            if (attacker.getPosition().dst(target.getPosition()) <= getRange()) {

                // 1. Calcola direzione spinta: (Target - Attacker)
                // Il vettore deve puntare VERSO il target (via dall'attacker)
                Vector2 knockbackDir = target.getPosition().cpy().sub(attacker.getPosition());

                // 2. Applica Knockback (se il target è un Player)
                if (target instanceof Player) {
                    ((Player) target).applyKnockback(knockbackDir, KNOCKBACK_FORCE);
                    System.out.println("SHIELD BASH! Il player è stato respinto.");
                }
            }
        }
    }

    @Override
    public void attack() { }
}
