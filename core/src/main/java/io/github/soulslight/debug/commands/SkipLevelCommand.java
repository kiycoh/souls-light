package io.github.soulslight.debug.commands;

import com.badlogic.gdx.Gdx;
import io.github.soulslight.debug.DebugCommand;
import io.github.soulslight.model.GameModel;

/** Debug command that advances to the next level. */
public class SkipLevelCommand implements DebugCommand {

  private final GameModel model;

  public SkipLevelCommand(GameModel model) {
    this.model = model;
  }

  @Override
  public void execute() {
    model.setLevelCompleted(true);
    Gdx.app.log("DebugMenu", "Skipping to next level...");
  }

  @Override
  public String getName() {
    return "Skip Level";
  }

  @Override
  public String getDescription() {
    return "Advances to the next level.";
  }
}
