package io.github.soulslight.model.enemies.ai;

import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.Player;
import java.util.List;

public final class SearchState implements EnemyState {
  private float searchTimer = 2.0f;

  @Override
  public void enter(AbstractEnemy enemy) {
    this.searchTimer = enemy.getSearchTimer();
    if (this.searchTimer <= 0) this.searchTimer = 2.0f; // Default
  }

  @Override
  public void update(AbstractEnemy enemy, List<Player> players, float deltaTime) {
    if (enemy.getBody() == null) return;
    // Check if player reappeared
    for (Player p : players) {
      if (enemy.canSeePlayer(p, enemy.getBody().getWorld())) {
        enemy.setAIState(new ChaseState());
        return;
      }
    }

    searchTimer -= deltaTime;
    if (searchTimer > 0) {
      if (enemy.getLastKnownPlayerPos().dst(enemy.getBody().getPosition()) > 15f) {
        enemy.moveTowards(enemy.getLastKnownPlayerPos(), deltaTime);
      } else {
        enemy.getBody().setLinearVelocity(0, 0);
      }
    } else {
      enemy.setAIState(new PatrolState());
    }
  }
}
