package io.github.soulslight.model;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

/**
 * Pattern: Facade
 * GameModel acts as a Facade, providing a simplified interface to the complex subsystems
 * like Physics (Box2D) and Game Logic (Entities, Map).
 */
public class GameModel implements Disposable {

    // --- CONSTANTS ---
    // "Will" is a shared value
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
        this.player = new Player(Player.PlayerClass.WARRIOR);

        // Pattern: Builder & Abstract Factory (Usage)
        // Using LevelBuilder to construct the level with enemies from a factory
        this.level = new LevelBuilder()
                .buildMap(MapGenerator.generateMap())
                .spawnEnemies(new DungeonEnemyFactory(), 2, 1) // 2 Melee, 1 Ranged
                .setEnvironment("dungeon_theme.mp3", 0.3f)
                .build();
    }

    /**
     * Update physics and game logic.
     * @param deltaTime Time elapsed since the last update in seconds.
     */

    public void update(float deltaTime) {
        if (isPaused) return;

        // Box2D physics update
        physicsWorld.step(1/60f, 6, 2);

        // Player update logic if needed
    }

    // --- VIEW & CONTROLLER GETTERS ---
    public World getPhysicsWorld() {
        return physicsWorld;
    }

    public Player getPlayer() {
        return player;
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
        return new GameStateMemento(currentWill, player.getPosition());
    }

    public void restoreMemento(GameStateMemento memento) {
        this.currentWill = memento.getWill();
        this.player.getPosition().set(memento.getPosition());
    }

    // --- DISPOSABLE ---
    @Override
    public void dispose() {
        physicsWorld.dispose();
        if (level != null) {
            level.dispose();
        }
    }

    /**
     * Pattern: Memento
     * Stores the internal state of the GameModel.
     */
    public static class GameStateMemento {
        private final float will;
        private final Vector2 position;

        public GameStateMemento(float will, Vector2 position) {
            this.will = will;
            this.position = new Vector2(position); // Deep copy
        }

        public float getWill() {
            return will;
        }

        public Vector2 getPosition() {
            return position;
        }
    }
}
