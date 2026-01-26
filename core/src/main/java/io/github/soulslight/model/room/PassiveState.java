package io.github.soulslight.model.room;

/**
 * Pattern: State (Concrete State) Initial state before player enters the room. Triggers lockdown
 * transition on player detection.
 */
public final class PassiveState implements RoomState {

  /** Singleton instance for state flyweight optimization. */
  public static final PassiveState INSTANCE = new PassiveState();

  private PassiveState() {}

  @Override
  public void onEnter(Room room) {
    // Doors start unlocked in passive state
    room.setDoorsLocked(false);
  }

  @Override
  public void onExit(Room room) {
    // No cleanup needed
  }

  @Override
  public void update(Room room, float deltaTime) {
    // Check if any player is FULLY inside the room to trigger combat
    if (room.getRoomManager() != null) {
      for (io.github.soulslight.model.entities.Player player : room.getRoomManager().getPlayers()) {
        if (room.isPlayerFullyInRoom(player)) {
          triggerCombatOrClear(room);
          return;
        }
      }
    }
  }

  @Override
  public void onPlayerEntered(Room room) {
    // No longer trigger on sensor touch. Wait for full containment in update().
  }

  private void triggerCombatOrClear(Room room) {
    // If room has no enemies, skip combat and mark as cleared
    if (room.getRemainingEnemyCount() == 0) {
      room.transitionTo(ClearedState.INSTANCE);
    } else {
      // Player entered: transition to combat
      room.transitionTo(ActiveCombatState.INSTANCE);
    }
  }

  @Override
  public void checkClearCondition(Room room) {
    // Cannot clear from passive state
  }
}
