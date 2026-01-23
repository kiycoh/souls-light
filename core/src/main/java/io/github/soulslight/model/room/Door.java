package io.github.soulslight.model.room;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.soulslight.model.Constants;

/**
 * Represents a door that can be locked/unlocked. When locked, creates a static physics body to
 * block passage. When unlocked, removes the blocking body.
 */
public class Door {

  /** Cardinal direction for door placement. */
  public enum Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST
  }

  private final Vector2 position;
  private final Direction direction;
  private final float width;
  private final float height;
  private Body physicsBody;
  private boolean locked;
  private World world;

  /**
   * Creates a new door at the specified position.
   *
   * @param x X position in world units
   * @param y Y position in world units
   * @param direction Cardinal direction of the door
   * @param width Door width in world units
   * @param height Door height in world units
   */
  public Door(float x, float y, Direction direction, float width, float height) {
    this.position = new Vector2(x, y);
    this.direction = direction;
    this.width = width;
    this.height = height;
    this.locked = false;
    this.physicsBody = null;
    this.world = null;
  }

  /**
   * Initializes the door with a physics world reference.
   *
   * @param world The Box2D world
   */
  public void initialize(World world) {
    this.world = world;
  }

  /** Locks the door by creating a static blocking body. */
  public void lock() {
    if (locked || world == null) return;

    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.StaticBody;
    bodyDef.position.set(position.x, position.y);

    physicsBody = world.createBody(bodyDef);

    PolygonShape shape = new PolygonShape();
    shape.setAsBox(width / 2f, height / 2f);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = shape;
    fixtureDef.filter.categoryBits = Constants.BIT_DOOR;
    fixtureDef.filter.maskBits = Constants.BIT_PLAYER | Constants.BIT_ENEMY;

    physicsBody.createFixture(fixtureDef);
    physicsBody.setUserData(this);

    shape.dispose();
    locked = true;
  }

  /** Unlocks the door by destroying the blocking body. */
  public void unlock() {
    if (!locked || world == null) return;

    if (physicsBody != null) {
      world.destroyBody(physicsBody);
      physicsBody = null;
    }
    locked = false;
  }

  // --- Getters ---

  public Vector2 getPosition() {
    return position.cpy();
  }

  public Direction getDirection() {
    return direction;
  }

  public boolean isLocked() {
    return locked;
  }

  public float getWidth() {
    return width;
  }

  public float getHeight() {
    return height;
  }
}
