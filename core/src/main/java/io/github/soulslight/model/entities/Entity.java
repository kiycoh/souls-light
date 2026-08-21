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
  /**
   * Unica fonte di verità sullo stato di morte: vera quando la vita ha toccato lo zero.
   * isDead() la legge, e le sottoclassi con regole aggiuntive (Oblivion) la compongono
   * invece di duplicarla.
   */
  protected boolean healthDepleted = false;

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
    if (healthDepleted) return;

    this.health -= amount;
    if (this.health <= 0) {
      this.health = 0;
      this.healthDepleted = true;
    }
  }

  public boolean isDead() {
    return this.healthDepleted;
  }

  public void setPosition(float x, float y) {
    this.position.set(x, y);
    if (body != null) body.setTransform(x, y, body.getAngle());
  }

  /** Imposta la vita corrente. Non tocca il massimo: per quello serve setMaxHealth. */
  public void setHealth(float health) {
    this.health = Math.max(0f, health);
    this.healthDepleted = this.health <= 0;
  }

  /** Imposta il massimo e riporta la vita corrente al nuovo massimo. */
  public void setMaxHealth(float maxHealth) {
    this.maxHealth = maxHealth;
    setHealth(maxHealth);
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
