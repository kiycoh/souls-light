package io.github.soulslight.model.room;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * TDD tests for RoomState transitions. Tests the State pattern implementation for room lifecycle.
 */
class RoomStateTest {

  private Room room;

  @BeforeEach
  void setUp() {
    room = new Room("test-room-1", 0, 0, 100, 100);
  }

  @Nested
  @DisplayName("PassiveState Tests")
  class PassiveStateTests {

    @Test
    @DisplayName("Room starts in PassiveState")
    void roomStartsInPassiveState() {
      assertInstanceOf(PassiveState.class, room.getCurrentState());
    }

    @Test
    @DisplayName("PassiveState transitions to ActiveCombatState on player entry")
    void transitionsToActiveOnPlayerEntry() {
      room.onPlayerEntered();
      assertInstanceOf(ActiveCombatState.class, room.getCurrentState());
    }
  }

  @Nested
  @DisplayName("ActiveCombatState Tests")
  class ActiveCombatStateTests {

    @BeforeEach
    void enterCombat() {
      room.onPlayerEntered();
    }

    @Test
    @DisplayName("ActiveCombatState keeps doors locked")
    void doorsRemainLockedDuringCombat() {
      assertTrue(room.areDoorsLocked());
    }

    @Test
    @DisplayName("ActiveCombatState transitions to ClearedState when all enemies dead")
    void transitionsToClearedWhenNoEnemies() {
      // Room has no enemies, so should immediately transition
      room.checkClearCondition();
      assertInstanceOf(ClearedState.class, room.getCurrentState());
    }
  }

  @Nested
  @DisplayName("ClearedState Tests")
  class ClearedStateTests {

    @BeforeEach
    void clearRoom() {
      room.onPlayerEntered();
      room.checkClearCondition();
    }

    @Test
    @DisplayName("ClearedState unlocks doors")
    void doorsUnlockedAfterClearing() {
      assertFalse(room.areDoorsLocked());
    }

    @Test
    @DisplayName("ClearedState is terminal - cannot re-lock")
    void clearedStateIsTerminal() {
      room.onPlayerEntered(); // Should have no effect
      assertInstanceOf(ClearedState.class, room.getCurrentState());
      assertFalse(room.areDoorsLocked());
    }

    @Test
    @DisplayName("Room reports cleared status correctly")
    void roomReportsCleared() {
      assertTrue(room.isCleared());
    }
  }
}
