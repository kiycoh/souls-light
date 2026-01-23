package io.github.soulslight.model.room;

/**
 * Pattern: State (Concrete State)
 * Combat state: doors locked, enemies active.
 * Monitors enemy count for transition to ClearedState.
 */
public final class ActiveCombatState implements RoomState {

    /** Singleton instance for state flyweight optimization. */
    public static final ActiveCombatState INSTANCE = new ActiveCombatState();

    private ActiveCombatState() {
    }

    @Override
    public void onEnter(Room room) {
        // Lock all doors when combat begins
        room.setDoorsLocked(true);
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
