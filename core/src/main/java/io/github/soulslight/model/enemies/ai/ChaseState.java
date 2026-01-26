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

    if (enemy.canSeePlayer(target, enemy.getBody().getWorld())) {
      float distance = enemy.getBody().getPosition().dst(target.getPosition());
      if (distance <= STOP_DISTANCE) {
        enemy.setAIState(new AttackState());
      } else {
        enemy.moveTowards(target.getPosition(), deltaTime);
      }
    } else {
      enemy.setAIState(new SearchState());
    }
  }
}
