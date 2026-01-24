package io.github.soulslight.model.room;

/**
 * Pattern: State Sealed interface for room lifecycle states. Manages transitions between Passive,
 * ActiveCombat, and Cleared states.
 */
public sealed interface RoomState permits PassiveState, ActiveCombatState, ClearedState {

  /**
   * Called when entering this state.
   *
   * @param room The room context
   */
  void onEnter(Room room);

  /**
   * Called when exiting this state.
   *
   * @param room The room context
   */
  void onExit(Room room);

  /**
   * Called each frame while in this state.
   *
   * @param room The room context
   * @param deltaTime Time since last update
   */
  void update(Room room, float deltaTime);

  /**
   * Called when player enters the room sensor.
   *
   * @param room The room context
   */
  void onPlayerEntered(Room room);

  /**
   * Called to check if the room clear condition is met.
   *
   * @param room The room context
   */
  void checkClearCondition(Room room);
}
