package io.github.soulslight.debug.commands;

import com.badlogic.gdx.Gdx;
import io.github.soulslight.debug.DebugCommand;
import io.github.soulslight.manager.GameManager;

/**
 * Debug command that toggles the Box2D debug renderer (hitboxes). Toggles the SHOW_HITBOXES flag in
 * GameManager.
 */
public class ToggleHitboxesCommand implements DebugCommand {

  @Override
  public void execute() {
    GameManager.SHOW_HITBOXES = !GameManager.SHOW_HITBOXES;
    Gdx.app.log("DebugMenu", "Hitboxes: " + (GameManager.SHOW_HITBOXES ? "VISIBLE" : "HIDDEN"));
  }

  @Override
  public String getName() {
    return "Toggle Hitboxes";
  }

  @Override
  public String getDescription() {
    return "Shows/hides Box2D physics debug shapes.";
  }
}
