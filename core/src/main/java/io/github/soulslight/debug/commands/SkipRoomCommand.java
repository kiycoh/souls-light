package io.github.soulslight.debug.commands;

import com.badlogic.gdx.Gdx;
import io.github.soulslight.debug.DebugCommand;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.room.Door;
import io.github.soulslight.model.room.Room;
import io.github.soulslight.model.room.RoomManager;

/**
 * Debug command that skips the current room by marking it as cleared, unlocking all doors, and
 * teleporting players to the door exit.
 */
public class SkipRoomCommand implements DebugCommand {

  private final GameModel model;
  private static final float DOOR_EXIT_OFFSET = 50f;

  public SkipRoomCommand(GameModel model) {
    this.model = model;
  }

  @Override
  public void execute() {
    if (model.getLevel() == null || model.getLevel().getRoomManager() == null) {
      Gdx.app.log("DebugMenu", "Skip Room: No room manager available.");
      return;
    }

    RoomManager rm = model.getLevel().getRoomManager();

    if (model.getPlayers().isEmpty()) {
      return;
    }

    Player player = model.getPlayers().get(0);
    Room current = rm.findRoomContaining(player.getPosition());

    if (current == null) {
      Gdx.app.log("DebugMenu", "Skip Room: Player not in a room.");
      return;
    }

    // Force clear the room
    current.forceCleared();
    rm.unlockAllDoors();

    // Teleport players to the first door's exit (outside the room)
    teleportPlayersToNextDoor(current);

    Gdx.app.log("DebugMenu", "Room skipped: " + current.getId());
  }

  /** Teleports all players to just outside the first available door. */
  private void teleportPlayersToNextDoor(Room room) {
    if (room.getDoors().isEmpty()) {
      return;
    }

    // Use the first door as the exit point
    Door exitDoor = room.getDoors().get(0);

    float targetX = exitDoor.getPosition().x;
    float targetY = exitDoor.getPosition().y;

    // Offset AWAY from the room (opposite of ActiveCombatState logic)
    switch (exitDoor.getDirection()) {
      case NORTH -> targetY += DOOR_EXIT_OFFSET; // Door is north, move player further north
      case SOUTH -> targetY -= DOOR_EXIT_OFFSET; // Door is south, move player further south
      case EAST -> targetX += DOOR_EXIT_OFFSET; // Door is east, move player further east
      case WEST -> targetX -= DOOR_EXIT_OFFSET; // Door is west, move player further west
    }

    // Teleport all players
    for (int i = 0; i < model.getPlayers().size(); i++) {
      Player p = model.getPlayers().get(i);
      if (p != null && p.getBody() != null) {
        float offsetX = i * 25f; // Slight offset between players
        p.setPosition(targetX + offsetX, targetY);
      }
    }
  }

  @Override
  public String getName() {
    return "Skip Room";
  }

  @Override
  public String getDescription() {
    return "Clears the current room and teleports to the exit.";
  }
}
