package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

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
    // private PlayerEntity nox;
    // private PlayerEntity lux;

    public GameModel() {
        // Physics initialization
        this.physicsWorld = new World(new Vector2(0, 0), true);

        // Game state initialization
        this.currentWill = MAX_WILL / 2;
        this.isPaused = false;

        // Entity initialization
        // createEnemies();
    }

    /**
     * Update physics and game logic.
     * @param deltaTime Time elapsed since the last update in seconds.
     */

    public void update(float deltaTime) {
        if (isPaused) return;

        // Box2D physics update
        physicsWorld.step(1/60f, 6, 2);

        //nox.update(deltaTime);
        //lux.update(deltaTime);
    }

    // --- VIEW & CONTROLLER GETTERS ---
    public World getPhysicsWorld() {
        return physicsWorld;
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


    // --- DISPOSABLE ---
    @Override
    public void dispose() {
        physicsWorld.dispose();
    }

}
