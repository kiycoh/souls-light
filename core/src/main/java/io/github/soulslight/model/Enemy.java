package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;

/** Pattern: Prototype (Concrete Prototype) Represents a generic enemy entity. */
public class Enemy implements Prototype<Enemy> {
  private Vector2 position;
  private float health;
  private AttackStrategy attackStrategy;
  private String type; // e.g., "Goblin", "Skeleton"

  public Enemy(String type, float health, AttackStrategy attackStrategy) {
    this.type = type;
    this.health = health;
    this.attackStrategy = attackStrategy;
    this.position = new Vector2(0, 0);
  }

  public void setPosition(float x, float y) {
    this.position.set(x, y);
  }

  public Vector2 getPosition() {
    return position;
  }

  public void update(float delta) {
    // Basic AI logic
  }

  @Override
  public Enemy clone() {
    try {
      Enemy cloned = (Enemy) super.clone();
      cloned.position = new Vector2(this.position); // Deep copy of position
      // AttackStrategy is shared (Flyweight-ish) or could be cloned if mutable
      return cloned;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError(); // Should not happen
    }
  }
}
