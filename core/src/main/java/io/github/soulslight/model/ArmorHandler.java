package io.github.soulslight.model;

/**
 * Pattern: Chain of Responsibility (Concrete Handler)
 * Reduces damage based on armor value.
 */
public class ArmorHandler extends DamageHandler {
    private float armorValue;

    public ArmorHandler(float armorValue) {
        this.armorValue = armorValue;
    }

    @Override
    protected float process(float amount) {
        float reduced = Math.max(0, amount - armorValue);
        System.out.println("Armor reduced damage by " + armorValue + ". Remaining: " + reduced);
        return reduced;
    }
}

