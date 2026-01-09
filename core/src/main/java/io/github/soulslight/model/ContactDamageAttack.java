package io.github.soulslight.model;

import java.util.List;

public class ContactDamageAttack extends AbstractAttack {

    @Override
    public float getRange() {
        return 30.0f;
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
        return "impact_sound"; // Suono di impatto
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
        // Metodo vuoto
    }
}
