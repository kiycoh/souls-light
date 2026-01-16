package io.github.soulslight.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import io.github.soulslight.model.Level;
import io.github.soulslight.model.MapGenerator;
import io.github.soulslight.model.Player;

public class GameManager {
  public static final float PPM = 100f;

  private static GameManager instance;
  private Level currentLevel;
  private Player player;
    public static boolean DEBUG_MODE = false;
  private GameManager() {}

  public static synchronized GameManager getInstance() {
    if (instance == null) {
      instance = new GameManager();
    }
    return instance;
  }

  public Level getCurrentLevel() {
    return currentLevel;
  }

  public void setCurrentLevel(Level currentLevel) {
    this.currentLevel = currentLevel;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public void cleanUp() {
    this.currentLevel = null;
    this.player = null;
  }

  public void loadLevel() {
    try {
      // Generate procedural map
      TiledMap map = MapGenerator.generateProceduralMap(21L); // Map seed

      // Dispose old level if exists to prevent leaks
      if (currentLevel != null) {
        currentLevel.dispose();
      }

      // Assign map to a new Level object to ensure it's managed and disposed correctly
      currentLevel = new Level();
      currentLevel.setMap(map);

      // Initialize player at starting position
      if (player != null) {
        player.setPosition(5, 5); // start position
      }

      Gdx.app.log("GameManager", "Level loaded successfully.");

    } catch (Exception e) {
      Gdx.app.error("GameManager", "Failed to load level", e);
    }
  }
}
