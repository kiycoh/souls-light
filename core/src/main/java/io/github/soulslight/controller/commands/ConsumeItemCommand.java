package io.github.soulslight.controller.commands;

import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.entities.Player;

public class ConsumeItemCommand implements Command {
  private final int playerIndex;
  private final int slotIndex;

  public ConsumeItemCommand(int playerIndex, int slotIndex) {
    this.playerIndex = playerIndex;
    this.slotIndex = slotIndex;
  }

  @Override
  public void execute(GameModel model) {
    if (model.getPlayers().size() > playerIndex) {
      Player p = model.getPlayers().get(playerIndex);
      if (p != null && !p.isDead()) {
        p.consumeItem(slotIndex);
      }
    }
  }
}
