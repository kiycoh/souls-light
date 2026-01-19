package io.github.soulslight.model;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import java.util.List;

/**
 * Pattern: Facade GameModel acts as a Facade, providing a simplified interface to the complex
 * subsystems like Physics (Box2D) and Game Logic (Entities, Map).
 */
public class GameModel implements Disposable {

  // --- CONSTANTS ---
  public static final float MAX_WILL = 100f;

  // --- Box2D PHYSICS ---
  private final World physicsWorld;

  // --- GAME STATE ---
  private float currentWill;
  private boolean isPaused;

  // --- ENTITIES ---
  private Player player;
  private Level level;

  public GameModel() {
    // Physics initialization
    this.physicsWorld = new World(new Vector2(0, 0), true);

    // Game state initialization
    this.currentWill = MAX_WILL / 2;
    this.isPaused = false;

    // Entity initialization
    // Pattern: Factory Method (Usage)
    // Initialize player at a safer default position (e.g. 100, 100 pixels converted to meters)
    this.player =
        new Player(
            Player.PlayerClass.WARRIOR,
            this.physicsWorld,
            100 / Constants.PPM,
            100 / Constants.PPM);

    // Pattern: Builder & Abstract Factory (Usage)
    // Using LevelBuilder to construct the level with enemies from a factory
    this.level =
        new LevelBuilder()
            .buildMap(
                MapGenerator.generate(
                    new NoiseMapStrategy(
                        12345L, MapGenerator.MAP_WIDTH, MapGenerator.MAP_HEIGHT, 0.15f, 3, -0.1f)))
            .spawnEnemies(new EnemyFactory(), 2, 1)
            .setEnvironment("shine7.mp3", 0.3f)
            .build();

    // Create Map Collision Bodies
    CollisionHandler.createMapBodies(physicsWorld, level.getMap());

    // Create Enemy Bodies
    for (Enemy enemy : level.getEnemies()) {
      enemy.createBody(physicsWorld);
    }

    // Set Contact Listener
    physicsWorld.setContactListener(new GameContactListener());
  }

  /**
   * Update physics and game logic.
   *
   * @param deltaTime Time elapsed since the last update in seconds.
   */
  public void update(float deltaTime) {
    if (isPaused) return;

    // Box2D physics update
    physicsWorld.step(1 / 60f, 6, 2);

    // Update Enemies
    if (level != null) {
      List<Enemy> enemies = level.getEnemies();
      for (int i = enemies.size() - 1; i >= 0; i--) {
        Enemy enemy = enemies.get(i);
        enemy.update(player, deltaTime);
        if (enemy.getHealth() <= 0) {
          enemies.remove(i);
        }
      }
    }
  }

  // --- VIEW & CONTROLLER GETTERS ---
  public World getPhysicsWorld() {
    return physicsWorld;
  }

  public Player getPlayer() {
    return player;
  }

  public Level getLevel() {
    return level;
  }

  public TiledMap getMap() {
    return level.getMap();
  }

  public float getCurrentWill() {
    return currentWill;
  }

  public void setCurrentWill(float currentWill) {
    this.currentWill = currentWill;
  }

  public boolean isPaused() {
    return isPaused;
  }

  public void setPaused(boolean paused) {}

  // --- MEMENTO PATTERN ---
  public GameStateMemento createMemento() {
    return new GameStateMemento(currentWill, player.getPosition().x, player.getPosition().y);
  }

  public void restoreMemento(GameStateMemento memento) {
    this.currentWill = memento.will();
    this.player.getPosition().set(memento.x(), memento.y());
  }

  // --- DISPOSABLE ---
  @Override
  public void dispose() {
    physicsWorld.dispose();
    if (level != null) {
      level.dispose();
    }
  }

  /** Pattern: Memento Stores the internal state of the GameModel. Implemented as a Record. */
  public record GameStateMemento(float will, float x, float y) {}
}
