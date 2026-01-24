package io.github.soulslight.debug.commands;

import com.badlogic.gdx.Gdx;
import io.github.soulslight.debug.DebugCommand;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.entities.Player;

/** Debug command that restores all players to full health. */
public class HealToFullCommand implements DebugCommand {

  private final GameModel model;

  public HealToFullCommand(GameModel model) {
    this.model = model;
  }

  @Override
  public void execute() {
    for (Player player : model.getPlayers()) {
      player.setHealth(player.getMaxHealth());
    }
    Gdx.app.log("DebugMenu", "All players healed to full health.");
  }

  @Override
  public String getName() {
    return "Heal to Full";
  }

  @Override
  public String getDescription() {
    return "Restores all players to full health.";
  }
}
