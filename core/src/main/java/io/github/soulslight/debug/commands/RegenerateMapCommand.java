package io.github.soulslight.debug.commands;

import com.badlogic.gdx.Gdx;
import io.github.soulslight.debug.DebugCommand;
import io.github.soulslight.model.GameModel;

/**
 * Debug command that regenerates the current map with a new random seed. This effectively creates a
 * new level layout while staying on the same level number.
 */
public class RegenerateMapCommand implements DebugCommand {

  private final GameModel model;

  public RegenerateMapCommand(GameModel model) {
    this.model = model;
  }

  @Override
  public void execute() {
    // Trigger a level reload by completing the current level
    // and letting the transition system handle it
    // For a true regeneration without level advancement,
    // additional implementation would be needed in GameModel

    Gdx.app.log("DebugMenu", "Map regeneration requested. Use Skip Level for now.");
    // Note: Full implementation would require adding a regenerateMap() method to
    // GameModel
    // that disposes the current level and creates a new one with a fresh seed
    // without incrementing the level counter
  }

  @Override
  public String getName() {
    return "Regenerate Map";
  }

  @Override
  public String getDescription() {
    return "Re-generates the current map with a new seed.";
  }
}
