package io.github.soulslight.model;

/**
 * Pattern: Strategy (Concrete Strategy)
 * Implements a specific attack behavior (Archer).
 */
public class ArcherAttack extends AbstractAttack {
    @Override
    public void attack() {
        System.out.println("Attacco eseguito");
    }

    @Override
    public float getRange() {
        return 10.0f;
    }

    @Override
    public float getDamage() {
        return 7.0f;
    }

    @Override
    public float getAttackSpeed() {
        return 1.5f;
    }

    @Override
    public String getSoundID() {
        return "bow_sound";
    }
}
