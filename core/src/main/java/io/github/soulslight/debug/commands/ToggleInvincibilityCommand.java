package io.github.soulslight.debug.commands;

import com.badlogic.gdx.Gdx;
import io.github.soulslight.debug.DebugCommand;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.entities.Player;

/** Debug command that toggles player invincibility. */
public class ToggleInvincibilityCommand implements DebugCommand {

  private final GameModel model;

  public ToggleInvincibilityCommand(GameModel model) {
    this.model = model;
  }

  @Override
  public void execute() {
    boolean newState = false;
    for (Player player : model.getPlayers()) {
      player.toggleDebugInvincibility();
      newState = player.isDebugInvincible();
    }
    Gdx.app.log("DebugMenu", "Invincibility: " + (newState ? "ON" : "OFF"));
  }

  @Override
  public String getName() {
    return "Toggle Invincibility";
  }

  @Override
  public String getDescription() {
    return "Makes the player immune to damage.";
  }
}
