package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Player extends Entity {

  // --- PHYSICS CONSTANTS ---
  // Define minimal body radius
  private static final float BODY_RADIUS = 0.4f;
  private static final float LINEAR_DAMPING = 10.0f; // High damping for tight "Souls-like" movement

  private final PlayerClass type;
  private AttackStrategy attackStrategy;

  // Box2D Body (represents the player in the physics world)
  private final Body body;

  public enum PlayerClass {
    WARRIOR {
      @Override
      public AttackStrategy getStrategy() {
        return new WarriorAttack();
      }
    },
    MAGE {
      @Override
      public AttackStrategy getStrategy() {
        return new MageAttack();
      }
    },
    THIEF {
      @Override
      public AttackStrategy getStrategy() {
        return new ThiefAttack();
      }
    },
    ARCHER {
      @Override
      public AttackStrategy getStrategy() {
        return new ArcherAttack();
      }
    };

    // Abstract method that every constant defined above MUST implement
    public abstract AttackStrategy getStrategy();
  }

  // Inject World via Constructor
  public Player(PlayerClass type, World world, float startX, float startY) {
    if (type == null) {
      throw new IllegalArgumentException("Player Type cannot be null");
    }
    if (world == null) {
      throw new IllegalArgumentException("Physics World cannot be null");
    }

    this.type = type;
    this.attackStrategy = type.getStrategy();

    // Initialize Physics Body immediately
    this.body = createBody(world, startX, startY);
  }

  public void setPosition(float x, float y) {
    if (body != null) {
      body.setTransform(x, y, body.getAngle());
    }
  }

  // ---------------------------------------------------------
  // PHYSICS INITIALIZATION (One-Time Allocation)
  // ---------------------------------------------------------
  private Body createBody(World world, float x, float y) {
    // Define Body
    BodyDef bdef = new BodyDef();
    bdef.position.set(x, y);
    bdef.type = BodyDef.BodyType.DynamicBody; // Player moves
    bdef.fixedRotation = true; // Prevent player from rolling like a ball
    bdef.linearDamping = LINEAR_DAMPING; // Stop quickly when input ceases

    Body pBody = world.createBody(bdef);

    // Define Shape (Circle for smooth sliding)
    CircleShape shape = new CircleShape();
    shape.setRadius(BODY_RADIUS);

    // Define Fixture
    FixtureDef fdef = new FixtureDef();
    fdef.shape = shape;
    fdef.density = 1.0f;
    fdef.friction = 0.0f; // No friction with walls to prevent sticking
    fdef.restitution = 0.0f; // No bouncing

    // Create Fixture & Dispose Shape (Crucial for memory!)
    pBody.createFixture(fdef);
    shape.dispose();

    return pBody;
  }

  // ---------------------------------------------------------
  // HOT PATH METHODS (Zero-Allocation)
  // ---------------------------------------------------------

  public void setAttackStrategy(AttackStrategy attackStrategy) {
    this.attackStrategy = attackStrategy;
  }

  public void doAnAttack() {
    if (attackStrategy != null) {
      attackStrategy.attack();
    }
  }

  /**
   * Applies velocity to the physics body.
   *
   * @param targetVelocityX The desired speed on X (not displacement!)
   * @param targetVelocityY The desired speed on Y (not displacement!)
   */
  public void move(float targetVelocityX, float targetVelocityY) {
    // Directly set velocity to body.
    // Note: In a real simulation, applyForce is more "physical",
    // but setLinearVelocity gives snappy controls required for this genre.
    body.setLinearVelocity(targetVelocityX, targetVelocityY);
  }

  /** Stop the player immediately (e.g., when no keys are pressed). */
  public void stop() {
    body.setLinearVelocity(0, 0);
  }

  public Vector2 getPosition() {
    return body.getPosition(); // Returns reference to internal Vector2 (Do not modify externally!)
  }

  public Body getBody() {
    return body;
  }

  public AttackStrategy getAttackStrategy() {
    return attackStrategy;
  }

  public void applyKnockback(Vector2 direction, float force) {
    if (this.body != null) {
      // Normalize the direction for safety
      direction.nor();

      // 'force' determines how strong the push is (e.g. 10.0f - 50.0f)
      this.body.applyLinearImpulse(direction.scl(force), this.body.getWorldCenter(), true);
    } else {
      this.position.mulAdd(direction, force);
    }
  }
}
