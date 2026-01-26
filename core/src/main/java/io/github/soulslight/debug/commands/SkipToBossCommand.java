package io.github.soulslight.debug.commands;

import com.badlogic.gdx.Gdx;
import io.github.soulslight.debug.DebugCommand;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.model.GameModel;

/** Debug command that jumps directly to the final boss stage (Level 5). */
public class SkipToBossCommand implements DebugCommand {

  private final GameModel model;

  public SkipToBossCommand(GameModel model) {
    this.model = model;
  }

  @Override
  public void execute() {
    // Set level index to 4 so advanceToNextLevel() goes to 5
    GameManager.getInstance().setCurrentLevelIndex(4);
    model.setLevelCompleted(true);
    Gdx.app.log("DebugMenu", "Skipping to Oblivion's Lair (Level 5)...");
  }

  @Override
  public String getName() {
    return "Skip to Boss";
  }

  @Override
  public String getDescription() {
    return "Jumps directly to Oblivion's Lair (Level 5).";
  }
}
