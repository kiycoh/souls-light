package io.github.soulslight.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.map.Level;
import io.github.soulslight.model.map.MapGenerator;

public class GameManager {
  public static final float PPM = 100f;

  private static GameManager instance;
  private Level currentLevel;
  private java.util.List<Player> players;
  public static boolean DEBUG_MODE = false;

  private GameManager() {
    this.players = new java.util.ArrayList<>();
  }

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

  public java.util.List<Player> getPlayers() {
    return players;
  }

  public void addPlayer(Player player) {
    if (this.players == null) {
        this.players = new java.util.ArrayList<>();
    }
    this.players.add(player);
  }
  
  public void clearPlayers() {
     if (this.players != null) {
        this.players.clear();
     }
  }

  public void cleanUp() {
    this.currentLevel = null;
    clearPlayers();
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
      if (players != null) {
          for(Player p : players) {
             p.setPosition(5, 5); // start position
          }
      }

      Gdx.app.log("GameManager", "Level loaded successfully.");

    } catch (Exception e) {
      Gdx.app.error("GameManager", "Failed to load level", e);
    }
  }
}
