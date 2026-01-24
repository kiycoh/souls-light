package io.github.soulslight.model.enemies.ai;

import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.Player;
import java.util.List;

/**
 * Pattern: State (Concrete State)
 * Room-based idle state: enemies stay completely still until the room activates
 * combat.
 * Unlike IdleState, this does NOT auto-detect players - activation is
 * controlled by Room.
 */
public final class RoomIdleState implements EnemyState {

    @Override
    public void enter(AbstractEnemy enemy) {
        // Stop all movement
        if (enemy.getBody() != null) {
            enemy.getBody().setLinearVelocity(0, 0);
        }
    }

    @Override
    public void update(AbstractEnemy enemy, List<Player> players, float deltaTime) {
        // Do nothing - wait for room activation
        // Explicitly stop any movement each frame to ensure enemies stay still
        if (enemy.getBody() != null) {
            enemy.getBody().setLinearVelocity(0, 0);
        }
    }

    @Override
    public void exit(AbstractEnemy enemy) {
        // Cleanup if needed
    }
}
