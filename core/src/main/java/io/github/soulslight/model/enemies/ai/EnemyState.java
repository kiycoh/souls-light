package io.github.soulslight.model.enemies.ai;

import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.Player;
import java.util.List;

public sealed interface EnemyState permits PatrolState, ChaseState, SearchState, AttackState, IdleState {
  void update(AbstractEnemy enemy, List<Player> players, float deltaTime);
  
  default void enter(AbstractEnemy enemy) {}
  default void exit(AbstractEnemy enemy) {}
}
