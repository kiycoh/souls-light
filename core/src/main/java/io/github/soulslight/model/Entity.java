package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;

public abstract class Entity {

  protected Vector2 position;
  protected float health;
  protected AttackStrategy attackStrategy;

  // COSTRUTTORE VUOTO (Utile per i Nemici/Prototype)
  public Entity() {
    this.position = new Vector2(0, 0);
    this.health = 100;
  }

  // COSTRUTTORE COMPLETO (Player)
  public Entity(Vector2 position, float health) {
    this.position = position;
    this.health = health;
  }

  public Vector2 getPosition() {
    return position;
  }

  public void setPosition(float x, float y) {
    this.position.set(x, y);
  }

  public float getX() {
    return this.position.x;
  }

  public float getY() {
    return this.position.y;
  }

  public void setHealth(float health) {
    this.health = health;
  }

  public float getHealth() {
    return health;
  }

  public AttackStrategy getAttackStrategy() {
    return attackStrategy;
  }

  public void takeDamage(float amount) {
    this.health -= amount;
    if (this.health < 0) this.health = 0;
  }

  public boolean isDead() {
    return this.health <= 0;
  }
}
