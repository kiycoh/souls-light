package io.github.soulslight.model.enemies.ai;

import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.Player;
import java.util.List;

public final class IdleState implements EnemyState {
  @Override
  public void update(AbstractEnemy enemy, List<Player> players, float deltaTime) {
    if (enemy.getBody() != null) {
      enemy.getBody().setLinearVelocity(0, 0);
    }
    // Simple transition logic: if sees player, chase
    for (Player p : players) {
        com.badlogic.gdx.physics.box2d.World world = (enemy.getBody() != null) ? enemy.getBody().getWorld() : null;
        if (enemy.canSeePlayer(p, world)) {
            enemy.setAIState(new ChaseState());
            return;
        }
    }
  }
}
