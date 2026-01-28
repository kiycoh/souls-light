package io.github.soulslight.model.room;

import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.entities.SerenityNPC;
import java.util.List;

/** Strategy for a Serenity Room. Spawns a friendly NPC that heals the player. */
public class SerenityEvent implements RoomEvent {

  private SerenityNPC npc;
  private boolean initialized = false;

  @Override
  public void onEnter(EventRoom room) {
    if (!initialized) {
      // Spawn NPC in the center of the room
      Vector2 center = new Vector2();
      room.getBounds().getCenter(center);
      npc = new SerenityNPC(center.x, center.y);

      // We need the world to init physics.
      if (room.getRoomManager() != null && room.getRoomManager().getWorld() != null) {
        npc.initPhysics(room.getRoomManager().getWorld());
      }

      initialized = true;
    }

    System.out.println("Entered Serenity Room");
    if (npc != null) {
      npc.reset();
    }
  }

  @Override
  public void update(EventRoom room, float delta) {
    if (npc == null || npc.hasInteracted()) return;

    // Check distance to players to trigger interaction
    if (room.getRoomManager() != null) {
      List<Player> players = room.getRoomManager().getPlayers();
      for (Player player : players) {
        if (player.getPosition().dst(npc.getPosition()) < 2.0f) { // Interaction range
          npc.interact(player);
        }
      }
    }
  }

  @Override
  public void onExit(EventRoom room) {
    // Cleanup if needed
  }

  // Getter for rendering
  public SerenityNPC getNpc() {
    return npc;
  }
}
