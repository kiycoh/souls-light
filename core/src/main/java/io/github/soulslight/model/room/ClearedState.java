package io.github.soulslight.model.room;

/**
 * Pattern: State (Concrete State) Terminal state: room cleared, doors unlocked. Optional loot spawn
 * trigger could be added here.
 */
public final class ClearedState implements RoomState {

  /** Singleton instance for state flyweight optimization. */
  public static final ClearedState INSTANCE = new ClearedState();

  private ClearedState() {}

  @Override
  public void onEnter(Room room) {
    // Unlock ALL doors when room is cleared (global unlock via RoomManager)
    RoomManager manager = room.getRoomManager();
    if (manager != null) {
      manager.unlockAllDoors();
    }
    room.setDoorsLocked(false); // Track local state
    room.markAsCleared();
  }

  @Override
  public void onExit(Room room) {
    // Terminal state - should not exit
  }

  @Override
  public void update(Room room, float deltaTime) {
    // Cleared rooms are static
  }

  @Override
  public void onPlayerEntered(Room room) {
    // Already cleared - no effect
  }

  @Override
  public void checkClearCondition(Room room) {
    // Already cleared - no effect
  }
}
