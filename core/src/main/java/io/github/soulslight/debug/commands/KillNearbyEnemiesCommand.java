package io.github.soulslight.debug.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.debug.DebugCommand;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.Player;

/** Debug command that kills all enemies within approximately 9 tiles of the player's position. */
public class KillNearbyEnemiesCommand implements DebugCommand {

  private final GameModel model;
  private static final float KILL_RADIUS = 9 * 32f; // 9 tiles * 32px per tile

  public KillNearbyEnemiesCommand(GameModel model) {
    this.model = model;
  }

  @Override
  public void execute() {
    if (model.getPlayers().isEmpty()) {
      return;
    }

    Player player = model.getPlayers().get(0);
    Vector2 playerPos = player.getPosition();
    int killCount = 0;

    for (AbstractEnemy enemy : model.getActiveEnemies()) {
      if (enemy.isDead()) continue;

      if (enemy.getPosition().dst(playerPos) <= KILL_RADIUS) {
        enemy.takeDamage(Float.MAX_VALUE); // Instant kill
        killCount++;
      }
    }

    Gdx.app.log(
        "DebugMenu",
        "Killed " + killCount + " enemies within " + (int) (KILL_RADIUS / 32) + " tiles.");
  }

  @Override
  public String getName() {
    return "Kill Nearby Enemies";
  }

  @Override
  public String getDescription() {
    return "Kills all enemies within 9 tiles of the player.";
  }
}
