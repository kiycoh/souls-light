package io.github.soulslight.controller.commands;

import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.entities.Player;

public class SpecialAttackCommand implements Command {
  private final int playerIndex;

  public SpecialAttackCommand(int playerIndex) {
    this.playerIndex = playerIndex;
  }

  @Override
  public void execute(GameModel model) {
    if (model.getPlayers().size() > playerIndex) {
      Player p = model.getPlayers().get(playerIndex);
      if (p != null && !p.isDead()) {
        p.performSpecialAttack(model.getActiveEnemies());
      }
    }
  }
}
