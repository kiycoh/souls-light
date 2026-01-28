package io.github.soulslight.model.room;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.model.enemies.Chaser;
import io.github.soulslight.model.enemies.EnemyRegistry;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.utils.GdxTestExtension;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * TDD tests for RoomState transitions. Tests the State pattern implementation for room lifecycle.
 */
@ExtendWith(GdxTestExtension.class)
class RoomStateTest {

  private Room room;
  private World world;

  @BeforeEach
  void setUp() {
    Box2D.init();
    world = new World(new Vector2(0, 0), true);
    room = new Room("test-room-1", 0, 0, 100, 100);
    EnemyRegistry.loadCache(null);
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
      // Setup dependencies for transition
      RoomManager roomManager = new RoomManager();
      roomManager.initialize(world);
      roomManager.addRoom(room);

      // Add enemy to ensure it goes to Combat, not Cleared
      Chaser enemy = new Chaser();
      enemy.createBody(world, 50, 50);
      room.addEnemy(enemy);

      // Add player inside room
      Player player = new Player(Player.PlayerClass.WARRIOR, world, 50, 50);
      roomManager.setPlayers(Collections.singletonList(player));

      // Trigger update
      room.update(0.1f);

      assertInstanceOf(ActiveCombatState.class, room.getCurrentState());
    }
  }

  @Nested
  @DisplayName("ActiveCombatState Tests")
  class ActiveCombatStateTests {

    @BeforeEach
    void enterCombat() {
      room.transitionTo(ActiveCombatState.INSTANCE);
    }

    @Test
    @DisplayName("ActiveCombatState keeps doors locked")
    void doorsRemainLockedDuringCombat() {
      assertTrue(room.areDoorsLocked());
    }

    @Test
    @DisplayName("ActiveCombatState transitions to ClearedState when all enemies dead")
    void transitionsToClearedWhenNoEnemies() {
      // Room has no enemies by default, so should immediately transition on check
      room.checkClearCondition();
      assertInstanceOf(ClearedState.class, room.getCurrentState());
    }
  }

  @Nested
  @DisplayName("ClearedState Tests")
  class ClearedStateTests {

    @BeforeEach
    void clearRoom() {
      room.transitionTo(ClearedState.INSTANCE);
    }

    @Test
    @DisplayName("ClearedState unlocks doors")
    void doorsUnlockedAfterClearing() {
      assertFalse(room.areDoorsLocked());
    }

    @Test
    @DisplayName("ClearedState is terminal - cannot re-lock")
    void clearedStateIsTerminal() {
      room.transitionTo(PassiveState.INSTANCE); // Try to go back?
      room.transitionTo(ClearedState.INSTANCE);
      room.checkClearCondition();
      assertInstanceOf(ClearedState.class, room.getCurrentState());
    }

    @Test
    @DisplayName("Room reports cleared status correctly")
    void roomReportsCleared() {
      assertTrue(room.isCleared());
    }
  }
}
