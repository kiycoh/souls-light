package io.github.soulslight.model.room;

import io.github.soulslight.model.entities.Player;

/**
 * Pattern: State (Concrete State) Combat state: doors locked, enemies active.
 * Monitors enemy count
 * for transition to ClearedState.
 */
public final class ActiveCombatState implements RoomState {

  /** Singleton instance for state flyweight optimization. */
  public static final ActiveCombatState INSTANCE = new ActiveCombatState();

  private ActiveCombatState() {
  }

  @Override
  public void onEnter(Room room) {
    // Lock ALL doors when combat begins (global lock via RoomManager)
    RoomManager manager = room.getRoomManager();
    if (manager != null) {
      manager.lockAllDoors();
      // Teleport players just inside the door to prevent being pushed out
      teleportPlayersInsideDoor(manager, room);
    }
    room.setDoorsLocked(true); // Track local state
    // Activate all enemies in the room (switch from RoomIdleState to ChaseState)
    room.activateEnemies();
  }

  /**
   * Teleports all players just inside the nearest door.
   * Prevents edge case where players are on door tiles when doors close.
   */
  private void teleportPlayersInsideDoor(RoomManager manager, Room room) {
    // Offset distance from door into the room - reduced to avoid "center" feel in
    // small tooms
    final float DOOR_OFFSET = 35f;

    for (Player player : manager.getPlayers()) {
      if (player == null || player.getBody() == null)
        continue;

      // Find the closest door to this player
      Door nearestDoor = findNearestDoor(room, player);
      if (nearestDoor == null) {
        System.out.println("Warning: Combat started but no door found for player teleport!");
        continue;
      }

      // Calculate position just inside the door (offset into the room)
      float targetX = nearestDoor.getPosition().x;
      float targetY = nearestDoor.getPosition().y;

      switch (nearestDoor.getDirection()) {
        case NORTH -> targetY -= DOOR_OFFSET; // Door is north, move player south into room
        case SOUTH -> targetY += DOOR_OFFSET; // Door is south, move player north into room
        case EAST -> targetX -= DOOR_OFFSET; // Door is east, move player west into room
        case WEST -> targetX += DOOR_OFFSET; // Door is west, move player east into room
      }

      System.out.println(
          "Teleporting player to: " + targetX + ", " + targetY + " (DoorDir: " + nearestDoor.getDirection() + ")");

      // SetTransform moves the physics body instantly
      player.getBody().setTransform(targetX, targetY, 0);
      // Sync entity position with physics body
      player.getPosition().set(targetX, targetY);
    }
  }

  /**
   * Finds the door closest to the player's current position.
   */
  private Door findNearestDoor(Room room, Player player) {
    Door nearest = null;
    float minDist = Float.MAX_VALUE;

    for (Door door : room.getDoors()) {
      float dist = player.getPosition().dst(door.getPosition());
      if (dist < minDist) {
        minDist = dist;
        nearest = door;
      }
    }
    return nearest;
  }

  @Override
  public void onExit(Room room) {
    // Cleanup handled by next state
  }

  @Override
  public void update(Room room, float deltaTime) {
    // Could add periodic clear checks here if needed
  }

  @Override
  public void onPlayerEntered(Room room) {
    // Already in combat - no effect
  }

  @Override
  public void checkClearCondition(Room room) {
    if (room.getRemainingEnemyCount() == 0) {
      room.transitionTo(ClearedState.INSTANCE);
    }
  }
}
