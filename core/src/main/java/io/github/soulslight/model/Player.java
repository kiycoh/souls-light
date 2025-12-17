package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;

/**
 * Pattern: Strategy (Context)
 * The Player class uses an AttackStrategy to perform attacks.
 * Represents a player entity with movement and combat capabilities.
 */

public class Player {

    private AttackStrategy attackStrategy;
    private Vector2 position;
    private float hp = 100f;
    private DamageHandler damageHandler;

    /**
     * Creates a player with default position (0,0) and no attack strategy.
     */
    public Player() {
        this.position = new Vector2(0, 0);
        // Default chain: Armor -> HP (implicit)
        this.damageHandler = new ArmorHandler(5.0f); // Base armor
    }

    /**
     * Creates a player with a specific attack strategy.
     * @param attackStrategy The combat strategy to use.
     */
    public Player(AttackStrategy attackStrategy) {
        this.attackStrategy = attackStrategy;
        this.position = new Vector2(0, 0);
        this.damageHandler = new ArmorHandler(5.0f);
    }

    /**
     * @return The current attack strategy.
     */
    public AttackStrategy getAttackStrategy() {
        return attackStrategy;
    }

    /**
     * Sets a new attack strategy for the player.
     * @param attackStrategy The new combat strategy to use.
     */
    public void setAttackStrategy(AttackStrategy attackStrategy) {
        this.attackStrategy = attackStrategy;
    }

    /**
     * Gets the damage potential of the current strategy.
     * @return Damage value or 0 if no strategy is set.
     */
    public float getStrategyDamage() {
        if (attackStrategy == null) {
            return 0;
        }
        return attackStrategy.getDamage();
    }

    /**
     * Performs an attack using the current strategy.
     * @return The damage dealt.
     * @throws IllegalStateException if no strategy is set.
     */
    public float doAnAttack() {
        if (attackStrategy == null) {
            throw new IllegalStateException("Attack strategy not set");
        }
        return attackStrategy.getDamage();
    }

    /**
     * @return The current position vector.
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Moves the player by the specified amount.
     * @param x X-axis displacement.
     * @param y Y-axis displacement.
     */
    public void move(float x, float y) {
        this.position.add(x, y);
    }

    /**
     * Reduces the player's HP by the damage taken, considering armor.
     * @param damage The damage amount.
     */
    public void takeDamage(float damage) {
        float finalDamage = damageHandler.handleDamage(damage);
        this.hp -= finalDamage;
        System.out.println("Player took " + finalDamage + " damage. HP: " + hp);
    }

    /**
     * Pattern: Factory Method
     * Defines available player classes with their default strategies.
     */
    public enum PlayerClass {
        WARRIOR {
            @Override
            public AttackStrategy getAttackStrategy() {
                return new WarriorAttack();
            }
        },
        MAGE {
            @Override
            public AttackStrategy getAttackStrategy() {
                return new MageAttack();
            }
        };
        public abstract AttackStrategy getAttackStrategy();
    }
}
