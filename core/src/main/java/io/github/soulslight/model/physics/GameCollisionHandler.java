package io.github.soulslight.model.physics;

import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.model.enemies.SpikedBall;
import io.github.soulslight.model.entities.ItemEntity;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.room.Portal;
import io.github.soulslight.model.room.RoomSensor;
import io.github.soulslight.utils.LogHelper;

/**
 * GoF Pattern: Adapter (Adaptee Implementation) Contains the actual business logic for handling
 * collisions between game objects. This class is decoupled from Box2D physics engine details
 * (Figures/Contacts).
 */
public class GameCollisionHandler implements CollisionHandler {

  @Override
  public void handleBeginContact(Object userA, Object userB) {
    // Logging (generic)
    logContact(userA, userB);

    // Feature Logic: RoomSensor
    checkRoomSensor(userA, userB);
    checkRoomSensor(userB, userA);

    // Feature Logic: Portal
    checkPortal(userA, userB, true);
    checkPortal(userB, userA, true);

    // Feature Logic: Item Pickup
    checkItem(userA, userB);
    checkItem(userB, userA);

    // Feature Logic: SpikedBall (Wall hit handled differently?)
    checkSpikedBall(userA, userB);
    checkSpikedBall(userB, userA);
  }

  @Override
  public void handleEndContact(Object userA, Object userB) {
    // Portal exit
    checkPortal(userA, userB, false);
    checkPortal(userB, userA, false);
  }

  private void logContact(Object a, Object b) {
    String nameA = getName(a);
    String nameB = getName(b);
    LogHelper.logThrottled("Physics", "Collision Start: " + nameA + " <-> " + nameB, 1.0f);
  }

  private String getName(Object o) {
    if (o instanceof Player) return "Player";
    if (o == null) return "Wall/Static"; // Assumption for null UserData
    return o.getClass().getSimpleName();
  }

  private void checkRoomSensor(Object entity, Object sensor) {
    if (entity instanceof Player && sensor instanceof RoomSensor) {
      ((RoomSensor) sensor).onPlayerContact();
    }
  }

  private void checkPortal(Object entity, Object sensor, boolean entering) {
    if (entity instanceof Player && sensor instanceof Portal) {
      if (entering) {
        ((Portal) sensor).onPlayerEnter();
      } else {
        ((Portal) sensor).onPlayerExit();
      }
    }
  }

  private void checkItem(Object playerObj, Object itemObj) {
    if (playerObj instanceof Player && itemObj instanceof ItemEntity) {
      Player player = (Player) playerObj;
      ItemEntity itemEntity = (ItemEntity) itemObj;
      if (player.pickUpItem(itemEntity.getItem())) {
        itemEntity.kill();
      }
    }
  }

  private void checkSpikedBall(Object enemyObj, Object wallObj) {
    if (enemyObj instanceof SpikedBall) {
      // Wall detection. In default Box2D setup, walls often have null UserData.
      // We rely on the Adapter passing a "Wall" object or we check for null if we
      // assume only walls are null.
      if (wallObj == null || wallObj.toString().equals("Wall")) {

        // Strategy: SpikedBall needs real physics info.
      }
    }
  }

  public void handleSpikedBallHit(Object spikedBall, Vector2 normal) {
    if (spikedBall instanceof SpikedBall spikedBall1) {
      spikedBall1.onWallHit(normal);
    }
  }
}
