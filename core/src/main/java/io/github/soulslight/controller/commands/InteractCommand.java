package io.github.soulslight.controller.commands;

import com.badlogic.gdx.Gdx;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.room.PortalRoom;

public class InteractCommand implements Command {
  @Override
  public void execute(GameModel model) {
    if (model.getLevel() != null) {
      boolean activated = false;

      // Try dungeon-style PortalRoom first
      if (model.getLevel().getRoomManager() != null) {
        PortalRoom portalRoom = model.getLevel().getRoomManager().getPortalRoom();
        if (portalRoom != null && portalRoom.tryActivatePortal()) {
          activated = true;
        }
      }

      // Try cave-style direct portal
      if (!activated
          && model.getLevel().getCavePortal() != null
          && model.getLevel().getCavePortal().tryActivate()) {
        activated = true;
      }

      if (activated) {
        Gdx.app.log("InteractCommand", "Portal activated! Advancing to next level.");
        model.setLevelCompleted(true);
      }
    }
  }
}
