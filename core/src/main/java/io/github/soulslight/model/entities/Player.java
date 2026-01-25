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

  private Vector2 currentKnockbackVelocity = new Vector2();

  // Debug mode invincibility
  private boolean debugInvincible = false;

  // Special Ability
  private SpecialAbilityStrategy specialAbility;
  private float specialCooldownTimer = 0f;

  // Revive Mechanic
  private float reviveAttemptTimer = 0f;

  public enum PlayerClass {
    WARRIOR {
      @Override
      public AttackStrategy getStrategy() {
        return new WarriorAttack(75);
      }

      @Override
      public SpecialAbilityStrategy getSpecial() {
        return new ShieldBashAbility();
      }

      @Override
      public int getBaseHP() {
        return 600;
      }

      @Override
      public int getBaseWill() {
        return 30;
      }

      @Override
      public String getSpecialAbility() {
        return "Shield Bash";
      }
    },
    MAGE {
      @Override
      public AttackStrategy getStrategy() {
        return new MageAttack(45);
      }

      @Override
      public SpecialAbilityStrategy getSpecial() {
        return new EarthquakeAbility();
      }

      @Override
      public int getBaseHP() {
        return 350;
      }

      @Override
      public int getBaseWill() {
        return 100;
      }

      @Override
      public String getSpecialAbility() {
        return "Arcane Blast";
      }
    },
    THIEF {
      @Override
      public AttackStrategy getStrategy() {
        return new ThiefAttack(20);
      }

      @Override
      public SpecialAbilityStrategy getSpecial() {
        return new ShadowStepAbility();
      }

      @Override
      public int getBaseHP() {
        return 400;
      }

      @Override
      public int getBaseWill() {
        return 60;
      }

      @Override
      public String getSpecialAbility() {
        return "Shadow Step";
      }
    },
    ARCHER {
      @Override
      public AttackStrategy getStrategy() {
        return new ArcherAttack(25);
      }

      @Override
      public SpecialAbilityStrategy getSpecial() {
        return new RainOfArrowsAbility();
      }

      @Override
      public int getBaseHP() {
        return 450;
      }

      @Override
      public int getBaseWill() {
        return 50;
      }

      @Override
      public String getSpecialAbility() {
        return "Rain of Arrows";
      }
    };

    public abstract AttackStrategy getStrategy();

    public abstract SpecialAbilityStrategy getSpecial();

    public abstract int getBaseHP();

    public abstract int getBaseWill();

    public abstract String getSpecialAbility();
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
    this.specialAbility = type.getSpecial();

    this.position = new Vector2(startX, startY);
    createBody(world, startX, startY);
  }

  @Override
  public void update(float delta) {
    if (invincibilityTimer > 0) invincibilityTimer -= delta;
    if (knockbackTimer > 0) knockbackTimer -= delta;
    if (attackCooldown > 0) attackCooldown -= delta;
    if (specialCooldownTimer > 0) specialCooldownTimer -= delta;

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
    if (body == null) return;

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
    fdef.filter.maskBits =
        Constants.BIT_WALL | Constants.BIT_ENEMY | Constants.BIT_DOOR | Constants.BIT_SENSOR;

    this.body.createFixture(fdef);
    this.body.setUserData(this);
    shape.dispose();
  }

  private List<ProjectileListener> projectileListeners = new java.util.ArrayList<>();

  public void addProjectileListener(ProjectileListener listener) {
    if (!projectileListeners.contains(listener)) {
      projectileListeners.add(listener);
    }
  }

  public void notifyProjectileRequest(Vector2 origin, Vector2 target, String type, float damage) {
    for (ProjectileListener listener : projectileListeners) {
      listener.onProjectileRequest(origin, target, type, damage);
    }
  }

  public void notifyProjectileRequest(
      Vector2 origin,
      io.github.soulslight.model.entities.Entity target,
      String type,
      float damage) {
    for (ProjectileListener listener : projectileListeners) {
      listener.onProjectileRequest(origin, target, type, damage);
    }
  }

  public void attack(List<AbstractEnemy> enemies) {
    if (attackCooldown > 0) return;
    // Higher attackSpeed = lower cooldown (attacks per second)
    attackCooldown = 1.0f / attackStrategy.getAttackSpeed();

    // Cast strict typed list to raw Entity list for Strategy interface
    // compatibility
    // In a cleaner refactor, AbstractEnemy and Player would share a 'Combatant' or
    // 'LivingEntity' base
    // but here we just cast.
    List<Entity> targets = new java.util.ArrayList<>(enemies);
    attackStrategy.executeAttack(this, targets);
  }

  public void performSpecialAttack(List<AbstractEnemy> enemies) {
    if (specialCooldownTimer > 0) {
      com.badlogic.gdx.Gdx.app.log("Player", "Ability Cooldown: " + specialCooldownTimer);
      return;
    }

    if (specialAbility != null) {
      specialAbility.execute(this, enemies);
      specialCooldownTimer = specialAbility.getCooldown();
    }
  }

  // Facing direction for attacks (independent of knockback)
  private Vector2 facingDirection = new Vector2(0, -1);

  public void move(float x, float y) {
    if (body != null) body.setLinearVelocity(x, y);
    if (Math.abs(x) > 0.1f || Math.abs(y) > 0.1f) {
      facingDirection.set(x, y).nor();
    }
  }

  public Vector2 getFacingDirection() {
    return facingDirection.cpy();
  }

  @Override
  public void takeDamage(float amount) {
    if (debugInvincible || invincibilityTimer > 0) return;
    super.takeDamage(amount);
    if (!isDead()) invincibilityTimer = INVINCIBILITY_DURATION;
  }

  public void doAnAttack() {
    if (attackStrategy != null) attackStrategy.attack();
  }

  public boolean isInvincible() {
    return invincibilityTimer > 0;
  }

  public PlayerClass getType() {
    return type;
  }

  /** Debug: Toggles the debug invincibility mode. */
  public void toggleDebugInvincibility() {
    this.debugInvincible = !this.debugInvincible;
  }

  /**
   * Debug: Checks if debug invincibility is active.
   *
   * @return True if debug invincible
   */
  public boolean isDebugInvincible() {
    return debugInvincible;
  }

  /**
   * Debug: Sets the player position directly (teleportation).
   *
   * @param x New X position
   * @param y New Y position
   */
  public void setPosition(float x, float y) {
    if (body != null) {
      body.setTransform(x, y, body.getAngle());
    }
    this.position.set(x, y);
  }

  /** Restores health to maximum and removes dead state. */
  public void restoreMaxHealth() {
    this.health = this.maxHealth;
    this.isDead = false;
  }

  /** Revives the player with full health and 3 seconds of invincibility. */
  public void revive() {
    restoreMaxHealth();
    this.invincibilityTimer = 3.0f;
  }

  public float getReviveAttemptTimer() {
    return reviveAttemptTimer;
  }

  public void setReviveAttemptTimer(float reviveAttemptTimer) {
    this.reviveAttemptTimer = reviveAttemptTimer;
  }
}
