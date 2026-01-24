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
    // Passive rooms don't update - waiting for player
  }

  @Override
  public void onPlayerEntered(Room room) {
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
