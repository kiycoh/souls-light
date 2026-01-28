package io.github.soulslight.model.combat;

import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.Player;
import java.util.List;

/** Pattern: Strategy Defines the behavior for a player's special ability. */
public interface SpecialAbilityStrategy {
  void execute(Player player, List<AbstractEnemy> enemies);

  float getCooldown();

  String getName();
}
