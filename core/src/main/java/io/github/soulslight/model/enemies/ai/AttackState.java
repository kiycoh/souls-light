package io.github.soulslight.model.enemies.ai;

import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.Player;
import java.util.List;

public final class AttackState implements EnemyState {
  private static final float RETREAT_DURATION = 0.8f;
  private static final float STOP_DISTANCE = 30f;
  private float attackCooldown = 1.5f;
  private float retreatTimer = 0f;
  private boolean isRetreating = false;

  @Override
  public void update(AbstractEnemy enemy, List<Player> players, float deltaTime) {
      if (enemy.getBody() == null) return;
      if (players.isEmpty()) {
          enemy.setAIState(new PatrolState());
          return;
      }
      Player target = players.get(0);
      float dist = enemy.getBody().getPosition().dst(target.getPosition());

      if (isRetreating) {
          enemy.moveAway(target.getPosition());
          retreatTimer -= deltaTime;
          if (retreatTimer <= 0) {
              isRetreating = false;
              enemy.setAIState(new ChaseState());
          }
          return;
      }

      enemy.getBody().setLinearVelocity(0, 0);
      attackCooldown -= deltaTime;

      if (attackCooldown <= 0) {
          enemy.attack(players);
          isRetreating = true;
          retreatTimer = RETREAT_DURATION;
          attackCooldown = 1.5f; // Reset
      } else if (dist > STOP_DISTANCE + 15f) {
          enemy.setAIState(new ChaseState());
      }
  }
}
