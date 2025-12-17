package io.github.soulslight.model;

/**
 * Pattern: Decorator (Concrete Decorator)
 * Adds fire damage effect to an attack.
 */
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
    public float getDamage() {
        return super.getDamage() + 5.0f; // Bonus fire damage
    }
}

