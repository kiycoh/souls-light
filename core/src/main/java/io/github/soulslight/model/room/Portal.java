package io.github.soulslight.model.room;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.soulslight.model.Constants;

/**
 * A portal entity that allows level completion when player interacts. Uses a Box2D sensor to detect
 * player proximity.
 */
public class Portal {

  private final Vector2 position;
  private Body sensorBody;
  private boolean playerInRange;
  private boolean activated;

  /**
   * Creates a portal at the specified position.
   *
   * @param x X position in world units
   * @param y Y position in world units
   */
  public Portal(float x, float y) {
    this.position = new Vector2(x, y);
    this.playerInRange = false;
    this.activated = false;
  }

  /**
   * Creates the portal's sensor body in the physics world.
   *
   * @param world The Box2D physics world
   */
  public void createBody(World world) {
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.StaticBody;
    bodyDef.position.set(position);

    sensorBody = world.createBody(bodyDef);

    CircleShape shape = new CircleShape();
    shape.setRadius(32f); // Interaction radius

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = shape;
    fixtureDef.isSensor = true;
    fixtureDef.filter.categoryBits = Constants.BIT_SENSOR;
    fixtureDef.filter.maskBits = Constants.BIT_PLAYER;

    sensorBody.createFixture(fixtureDef);
    sensorBody.setUserData(this);

    shape.dispose();
  }

  /**
   * Destroys the portal's body from the physics world.
   *
   * @param world The Box2D physics world
   */
  public void destroyBody(World world) {
    if (sensorBody != null) {
      world.destroyBody(sensorBody);
      sensorBody = null;
    }
  }

  /** Called when player enters the portal's sensor range. */
  public void onPlayerEnter() {
    playerInRange = true;
  }

  /** Called when player exits the portal's sensor range. */
  public void onPlayerExit() {
    playerInRange = false;
  }

  /**
   * Attempts to activate the portal. Only succeeds if player is in range.
   *
   * @return True if activation was successful
   */
  public boolean tryActivate() {
    if (playerInRange && !activated) {
      activated = true;
      return true;
    }
    return false;
  }

  // --- Getters ---

  public Vector2 getPosition() {
    return position.cpy();
  }

  public boolean isPlayerInRange() {
    return playerInRange;
  }

  public boolean isActivated() {
    return activated;
  }

  public Body getBody() {
    return sensorBody;
  }
}
