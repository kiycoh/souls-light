package io.github.soulslight.debug.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.debug.DebugCommand;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.room.Portal;
import io.github.soulslight.model.room.PortalRoom;

/** Debug command that teleports all players to the portal position. */
public class TeleportToPortalCommand implements DebugCommand {

  private final GameModel model;

  public TeleportToPortalCommand(GameModel model) {
    this.model = model;
  }

  @Override
  public void execute() {
    if (model.getPlayers().isEmpty() || model.getLevel() == null) {
      Gdx.app.log("DebugMenu", "Cannot teleport: no players or level.");
      return;
    }

    Portal portal = findPortal();

    if (portal == null) {
      Gdx.app.log("DebugMenu", "No portal found in current level.");
      return;
    }

    Vector2 portalPos = portal.getPosition();
    float offsetX = -50f; // Offset to avoid overlapping the portal

    for (int i = 0; i < model.getPlayers().size(); i++) {
      Player player = model.getPlayers().get(i);
      player.setPosition(portalPos.x + offsetX - (i * 30), portalPos.y);
    }

    Gdx.app.log("DebugMenu", "Teleported players to portal.");
  }

  private Portal findPortal() {
    // Try dungeon-style PortalRoom first
    if (model.getLevel().getRoomManager() != null) {
      PortalRoom pr = model.getLevel().getRoomManager().getPortalRoom();
      if (pr != null && pr.getPortal() != null) {
        return pr.getPortal();
      }
    }

    // Fall back to cave-style direct portal
    return model.getLevel().getCavePortal();
  }

  @Override
  public String getName() {
    return "Teleport to Portal";
  }

  @Override
  public String getDescription() {
    return "Moves all players near the level portal.";
  }
}
