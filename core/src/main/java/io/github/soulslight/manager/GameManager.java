package io.github.soulslight.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.map.Level;
import io.github.soulslight.model.map.LevelFactory;
import io.github.soulslight.model.map.MapGenerationStrategy;

/**
 * Pattern: Singleton Central coordinator for game state, campaign progression, and player
 * management.
 */
public class GameManager {
  public static final float PPM = 100f;

  private static GameManager instance;
  private Level currentLevel;
  private java.util.List<Player> players;
  public static boolean DEBUG_MODE = false;

  // Campaign state
  private GameMode gameMode = GameMode.STORY;
  private int currentLevelIndex = 1;
  private long campaignSeed;

  // Selected player class (default: WARRIOR)
  private Player.PlayerClass selectedPlayerClass = Player.PlayerClass.WARRIOR;

  private GameManager() {
    this.players = new java.util.ArrayList<>();
    this.campaignSeed = System.currentTimeMillis();
  }

  public static synchronized GameManager getInstance() {
    if (instance == null) {
      instance = new GameManager();
    }
    return instance;
  }

  // --- Campaign Management ---

  /**
   * Starts a new campaign with the specified game mode.
   *
   * @param mode The game mode (STORY or CUSTOM)
   */
  public void startCampaign(GameMode mode) {
    this.gameMode = mode;
    this.currentLevelIndex = 1;
    this.campaignSeed = System.currentTimeMillis();
    Gdx.app.log("GameManager", "Starting " + mode + " campaign with seed: " + campaignSeed);
  }

  /**
   * Advances to the next level in Story Mode.
   *
   * @return True if there is a next level, false if campaign is complete
   */
  public boolean advanceToNextLevel() {
    if (gameMode == GameMode.STORY) {
      if (currentLevelIndex < LevelFactory.getStoryModeLevelCount()) {
        currentLevelIndex++;
        Gdx.app.log(
            "GameManager",
            "Advancing to level "
                + currentLevelIndex
                + ": "
                + LevelFactory.getLevelName(currentLevelIndex));
        return true;
      } else {
        Gdx.app.log("GameManager", "Campaign complete!");
        return false;
      }
    }
    return true; // Custom mode loops indefinitely
  }

  /**
   * Gets the map generation strategy for the current level.
   *
   * @return The appropriate MapGenerationStrategy
   */
  public MapGenerationStrategy getCurrentLevelStrategy() {
    // Use level-specific seed for deterministic generation
    long levelSeed = campaignSeed + currentLevelIndex;
    return LevelFactory.createStrategy(currentLevelIndex, gameMode, levelSeed);
  }

  /**
   * Gets the enemy configuration for the current level.
   *
   * @return Enemy spawn configuration
   */
  public LevelFactory.EnemyConfig getCurrentEnemyConfig() {
    return LevelFactory.getEnemyConfig(currentLevelIndex, gameMode);
  }

  // --- Level Management ---

  public Level getCurrentLevel() {
    return currentLevel;
  }

  public void setCurrentLevel(Level currentLevel) {
    this.currentLevel = currentLevel;
  }

  // --- Player Management ---

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

  // --- Getters for Campaign State ---

  public GameMode getGameMode() {
    return gameMode;
  }

  public int getCurrentLevelIndex() {
    return currentLevelIndex;
  }

  public String getCurrentLevelName() {
    return LevelFactory.getLevelName(currentLevelIndex);
  }

  public boolean isFinalLevel() {
    return gameMode == GameMode.STORY && currentLevelIndex >= LevelFactory.getStoryModeLevelCount();
  }

  // --- Player Class Selection ---

  public void setSelectedPlayerClass(Player.PlayerClass playerClass) {
    this.selectedPlayerClass = playerClass;
  }

  public Player.PlayerClass getSelectedPlayerClass() {
    return selectedPlayerClass;
  }

  // --- Cleanup ---

  public void cleanUp() {
    this.currentLevel = null;
    clearPlayers();
  }

  /** Loads the current level using the appropriate strategy. */
  public void loadLevel() {
    try {
      // Get strategy for current level
      MapGenerationStrategy strategy = getCurrentLevelStrategy();
      TiledMap map = strategy.generate();

      // Dispose old level if exists to prevent leaks
      if (currentLevel != null) {
        currentLevel.dispose();
      }

      // Assign map to a new Level object
      currentLevel = new Level();
      currentLevel.setMap(map);

      // Initialize player at starting position
      if (players != null) {
        for (Player p : players) {
          p.setPosition(5, 5); // start position
        }
      }

      Gdx.app.log(
          "GameManager", "Level " + currentLevelIndex + " loaded: " + getCurrentLevelName());

    } catch (Exception e) {
      Gdx.app.error("GameManager", "Failed to load level", e);
    }
  }
}
