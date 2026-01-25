package io.github.soulslight.model.room;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import io.github.soulslight.model.Constants;

/**
 * A Box2D sensor fixture that covers a room's interior. Detects when Player enters the room to
 * trigger state transitions. The sensor is non-solid (isSensor = true) so entities pass through.
 */
public class RoomSensor {

  private final Room room;
  private Body sensorBody;

  /**
   * Creates a room sensor for the specified room.
   *
   * @param room The room this sensor belongs to
   */
  public RoomSensor(Room room) {
    this.room = room;
  }

  /**
   * Creates the physics sensor body in the world.
   *
   * @param world The Box2D physics world
   */
  public void createSensor(World world) {
    Rectangle bounds = room.getBounds();

    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.StaticBody;
    bodyDef.position.set(bounds.x + bounds.width / 2f, bounds.y + bounds.height / 2f);

    sensorBody = world.createBody(bodyDef);

    float halfW = Math.max(0.1f, bounds.width / 2f);
    float halfH = Math.max(0.1f, bounds.height / 2f);

    PolygonShape shape = new PolygonShape();
    shape.setAsBox(halfW, halfH);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = shape;
    fixtureDef.isSensor = true; // Non-solid, only detects overlaps
    fixtureDef.filter.categoryBits = Constants.BIT_SENSOR;
    fixtureDef.filter.maskBits = Constants.BIT_PLAYER;

    sensorBody.createFixture(fixtureDef);
    sensorBody.setUserData(this);

    shape.dispose();
  }

  /**
   * Destroys the sensor body from the physics world.
   *
   * @param world The Box2D physics world
   */
  public void destroySensor(World world) {
    if (sensorBody != null) {
      world.destroyBody(sensorBody);
      sensorBody = null;
    }
  }

  /** Called when a player enters this sensor. Delegates to the room's onPlayerEntered method. */
  public void onPlayerContact() {
    room.onPlayerEntered();
  }

  /**
   * Gets the room associated with this sensor.
   *
   * @return The room
   */
  public Room getRoom() {
    return room;
  }

  /**
   * Gets the physics body of this sensor.
   *
   * @return The sensor body, or null if not created
   */
  public Body getBody() {
    return sensorBody;
  }
}
