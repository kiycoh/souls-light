package io.github.soulslight.model.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.soulslight.model.Constants;
import io.github.soulslight.model.combat.*;
import io.github.soulslight.model.enemies.AbstractEnemy;
import java.util.List;

public class Player extends Entity {

  private static final float BODY_RADIUS = 14.0f;
  private static final float NORMAL_DAMPING = 10.0f; // Attrito per non scivolare
  // private float speed = 300f; al momento la speed è data da gamecontroller, da
  // cambiare
  // evetualmente
  private boolean wasInKnockback = false;

  private final PlayerClass type;
  private float knockbackTimer = 0f;
  private float invincibilityTimer = 0f;
  private final float INVINCIBILITY_DURATION = 0.1f;
  private float attackCooldown = 0f;
  private static final float ATTACK_COOLDOWN_DURATION = 0.5f;

  private Vector2 currentKnockbackVelocity = new Vector2();

  public enum PlayerClass {
    WARRIOR {
      @Override
      public AttackStrategy getStrategy() {
        return new WarriorAttack(9000);
      }
    },
    MAGE {
      @Override
      public AttackStrategy getStrategy() {
        return new MageAttack(45);
      }
    },
    THIEF {
      @Override
      public AttackStrategy getStrategy() {
        return new ThiefAttack(20);
      }
    },
    ARCHER {
      @Override
      public AttackStrategy getStrategy() {
        return new ArcherAttack(25);
      }
    };

    public abstract AttackStrategy getStrategy();
  }

  public Player(PlayerClass type, World world, float startX, float startY) {
    super();

    if (type == null) {
      throw new IllegalArgumentException("Player Type cannot be null");
    }
    this.health = 500;
    this.maxHealth = 500;
    this.type = type;
    // this.speed= 100; al momento la speed è data da gamecontroller, da cambiare
    // evetualmente
    this.attackStrategy = type.getStrategy();
    this.position = new Vector2(startX, startY);
    createBody(world, startX, startY);
  }

  @Override
  public void update(float delta) {

    if (invincibilityTimer > 0)
      invincibilityTimer -= delta;
    if (knockbackTimer > 0)
      knockbackTimer -= delta;
    if (attackCooldown > 0)
      attackCooldown -= delta;

    if (knockbackTimer > 0) {
      wasInKnockback = true;

      if (body != null) {
        body.setLinearDamping(0f);

        body.setLinearVelocity(currentKnockbackVelocity);
      }

      super.update(delta);
      return;
    }

    // Uscita dal knockback
    if (wasInKnockback) {
      wasInKnockback = false;
      if (body != null) {
        body.setLinearVelocity(0, 0);
        body.setLinearDamping(NORMAL_DAMPING);
      }
    }

    super.update(delta);
  }

  public void applyKnockback(Vector2 direction, float speedForce, float duration) {
    if (body == null)
      return;

    this.currentKnockbackVelocity.set(direction).nor().scl(speedForce);

    this.knockbackTimer = duration;

    body.setLinearDamping(0f);
    body.setLinearVelocity(currentKnockbackVelocity);
  }

  private void createBody(World world, float x, float y) {
    BodyDef bdef = new BodyDef();
    bdef.position.set(x, y);
    bdef.type = BodyDef.BodyType.DynamicBody;
    bdef.fixedRotation = true;

    bdef.linearDamping = NORMAL_DAMPING;

    this.body = world.createBody(bdef);

    CircleShape shape = new CircleShape();
    shape.setRadius(BODY_RADIUS);

    FixtureDef fdef = new FixtureDef();
    fdef.shape = shape;
    fdef.density = 1.0f;
    fdef.friction = 0.0f;
    fdef.restitution = 0.0f; // No bouncing
    fdef.filter.categoryBits = Constants.BIT_PLAYER;
    fdef.filter.maskBits = Constants.BIT_WALL | Constants.BIT_ENEMY | Constants.BIT_DOOR | Constants.BIT_SENSOR;

    this.body.createFixture(fdef);
    this.body.setUserData(this);
    shape.dispose();
  }

  public void attack(List<AbstractEnemy> enemies) {
    if (attackCooldown > 0)
      return;
    attackCooldown = ATTACK_COOLDOWN_DURATION;
    for (AbstractEnemy enemy : enemies) {
      if (enemy == null || enemy.isDead())
        continue;
      if (this.getPosition().dst(enemy.getPosition()) <= attackStrategy.getRange()) {
        enemy.takeDamage(attackStrategy.getDamage());
      }
    }
  }

  public void move(float x, float y) {
    if (body != null)
      body.setLinearVelocity(x, y);
  }

  @Override
  public void takeDamage(float amount) {
    if (invincibilityTimer > 0)
      return;
    super.takeDamage(amount);
    if (!isDead())
      invincibilityTimer = INVINCIBILITY_DURATION;
  }

  public void doAnAttack() {
    if (attackStrategy != null)
      attackStrategy.attack();
  }

  public boolean isInvincible() {
    return invincibilityTimer > 0;
  }

  public PlayerClass getType() {
    return type;
  }
}
