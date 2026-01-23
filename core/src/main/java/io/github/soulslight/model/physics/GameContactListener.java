package io.github.soulslight.model.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.soulslight.model.Constants;
import io.github.soulslight.model.enemies.SpikedBall;
import io.github.soulslight.model.room.RoomSensor;
import io.github.soulslight.utils.LogHelper;

public class GameContactListener implements ContactListener {

  @Override
  public void beginContact(Contact contact) {
    Fixture fa = contact.getFixtureA();
    Fixture fb = contact.getFixtureB();

    // HEAD Logic: Logging
    String aName = getFixtureName(fa);
    String bName = getFixtureName(fb);
    LogHelper.logThrottled("Physics", "Collision Start: " + aName + " <-> " + bName, 1.0f);

    // Feature Logic: RoomSensor detection
    checkRoomSensorContact(fa, fb);
    checkRoomSensorContact(fb, fa);

    // Feature Logic: SpikedBall
    Vector2 normal = contact.getWorldManifold().getNormal();
    checkSpikedBallWallHit(fa, fb, normal, false);
    checkSpikedBallWallHit(fb, fa, normal, true);
  }

  private String getFixtureName(Fixture fixture) {
    short category = fixture.getFilterData().categoryBits;

    if ((category & Constants.BIT_PLAYER) != 0)
      return "Player";
    if ((category & Constants.BIT_ENEMY) != 0)
      return "Enemy";
    if ((category & Constants.BIT_WALL) != 0)
      return "Wall";
    if ((category & Constants.BIT_SENSOR) != 0)
      return "RoomSensor";
    if ((category & Constants.BIT_DOOR) != 0)
      return "Door";

    return "Unknown";
  }

  /**
   * Checks if the contact is between a Player and a RoomSensor.
   * If so, triggers the room's onPlayerEntered callback.
   */
  private void checkRoomSensorContact(Fixture potentialPlayer, Fixture potentialSensor) {
    short playerCategory = potentialPlayer.getFilterData().categoryBits;
    short sensorCategory = potentialSensor.getFilterData().categoryBits;

    if ((playerCategory & Constants.BIT_PLAYER) != 0
        && (sensorCategory & Constants.BIT_SENSOR) != 0) {
      Object userData = potentialSensor.getBody().getUserData();
      if (userData instanceof RoomSensor sensor) {
        sensor.onPlayerContact();
      }
    }
  }

  private void checkSpikedBallWallHit(
      Fixture potentialEnemy, Fixture potentialWall, Vector2 normal, boolean invertNormal) {
    Object userData = potentialEnemy.getBody().getUserData();
    if (userData instanceof SpikedBall
        && potentialWall.getBody().getType() == BodyDef.BodyType.StaticBody) {
      Vector2 collisionNormal = invertNormal ? normal.cpy().scl(-1) : normal.cpy();
      ((SpikedBall) userData).onWallHit(collisionNormal);
    }
  }

  @Override
  public void endContact(Contact contact) {
  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {
  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {
  }
}
