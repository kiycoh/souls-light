package io.github.soulslight.model.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body; // necessario per la fisica
import io.github.soulslight.model.combat.AttackStrategy;

public abstract class Entity {

  protected Vector2 position;

  protected Body body; // per il corpo fisico
  protected TextureRegion textureRegion; // per la grafica

  protected float health;
  protected float maxHealth; // per la barra vita
  protected boolean isDead = false; // flag per determinare quando un nemico è morto

  protected AttackStrategy attackStrategy;

  public Entity() {
    this.position = new Vector2(0, 0);
    this.health = 100;
    this.maxHealth = 100;
  }

  public Entity(Vector2 position, float health) {
    this.position = position;
    this.health = health;
    this.maxHealth = health;
  }

  // metodo per sincronizzare grafica e fisica
  public void update(float delta) {
    if (body != null) {
      this.position.set(body.getPosition());
    }
  }

  // Metodo per infliggere il danno
  public void takeDamage(float amount) {
    if (isDead) return;

    this.health -= amount;
    if (this.health <= 0) {
      this.health = 0;
      this.isDead = true;
    }
  }

  public boolean isDead() {
    return this.health <= 0;
  }

  public void setPosition(float x, float y) {
    this.position.set(x, y);
    if (body != null) body.setTransform(x, y, body.getAngle());
  }

  public void setHealth(float health) {
    this.health = health;
    this.maxHealth = health;
  }

  public void setAttackStrategy(AttackStrategy strategy) {
    this.attackStrategy = strategy;
  }

  public void setTextureRegion(TextureRegion tr) {
    this.textureRegion = tr;
  }

  public Body getBody() {
    return body;
  }

  public Vector2 getPosition() {
    return position;
  }

  public float getX() {
    return this.position.x;
  }

  public float getY() {
    return this.position.y;
  }

  public float getHealth() {
    return health;
  }

  public float getMaxHealth() {
    return maxHealth;
  }

  public AttackStrategy getAttackStrategy() {
    return attackStrategy;
  }
}
