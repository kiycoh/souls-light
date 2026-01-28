package io.github.soulslight.model.enemies.ai;

import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.Player;
import java.util.List;

public final class ChaseState implements EnemyState {
  private static final float STOP_DISTANCE = 30f;

  @Override
  public void update(AbstractEnemy enemy, List<Player> players, float deltaTime) {
    if (enemy.getBody() == null) return;
    if (players.isEmpty()) return;

    Player target = enemy.getNearestTarget(players);
    if (target == null) return;

    float dist = enemy.getBody().getPosition().dst(target.getPosition());

    // Attack transition
    if (dist <= STOP_DISTANCE && enemy.canSeePlayer(target, enemy.getBody().getWorld())) {
      enemy.setAIState(new AttackState());
      return;
    }

    // Delegate to AbstractEnemy's smart movement which handles LOS and Pathfinding
    enemy.smartMoveTowards(target.getPosition(), deltaTime);
  }
}
