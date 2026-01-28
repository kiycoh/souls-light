package io.github.soulslight.model.enemies;

import static org.junit.jupiter.api.Assertions.*;

import io.github.soulslight.model.enemies.ai.EnemyState;
import io.github.soulslight.model.enemies.ai.IdleState;
import io.github.soulslight.model.entities.Player;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class EnemyAITest {

  @Test
  void testStateTransition() {
    TestEnemy enemy = new TestEnemy();
    EnemyState initialState = new IdleState();

    enemy.setAIState(initialState);

    assertEquals(initialState, enemy.getCurrentState());

    // Update should not crash even with null body (handled by state logic)
    enemy.updateBehavior(new ArrayList<>(), 0.1f);

    // State should remain IdleState
    assertInstanceOf(IdleState.class, enemy.getCurrentState());
  }

  static class TestEnemy extends AbstractEnemy {
    public TestEnemy() {
      super();
    }

    @Override
    public void updateBehavior(List<Player> players, float deltaTime) {
      if (getCurrentState() != null) {
        getCurrentState().update(this, players, deltaTime);
      }
    }

    @Override
    public AbstractEnemy clone() {
      return new TestEnemy();
    }
  }
}
