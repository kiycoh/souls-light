package io.github.soulslight.model.room;

import com.badlogic.gdx.math.Rectangle;
import io.github.soulslight.model.enemies.AbstractEnemy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a gameplay room with state management.
 * Implements the Context role in the State pattern.
 * Rooms transition between Passive, ActiveCombat, and Cleared states.
 */
public class Room implements EnemyDeathListener {

    private final String id;
    private final Rectangle bounds;
    private RoomState currentState;
    private final List<AbstractEnemy> enemies;
    private final List<Door> doors;
    private boolean doorsLocked;
    private boolean cleared;

    /**
     * Creates a new room with the specified bounds.
     *
     * @param id     Unique identifier for this room
     * @param x      X position of the room in world units
     * @param y      Y position of the room in world units
     * @param width  Width of the room in world units
     * @param height Height of the room in world units
     */
    public Room(String id, float x, float y, float width, float height) {
        this.id = id;
        this.bounds = new Rectangle(x, y, width, height);
        this.enemies = new ArrayList<>();
        this.doors = new ArrayList<>();
        this.doorsLocked = false;
        this.cleared = false;
        this.currentState = PassiveState.INSTANCE;
    }

    /**
     * Transitions to a new state, calling exit/enter hooks.
     *
     * @param newState The state to transition to
     */
    public void transitionTo(RoomState newState) {
        if (currentState != null) {
            currentState.onExit(this);
        }
        currentState = newState;
        currentState.onEnter(this);
    }

    /**
     * Updates the room each frame.
     *
     * @param deltaTime Time since last update
     */
    public void update(float deltaTime) {
        currentState.update(this, deltaTime);
    }

    /**
     * Called when the player enters this room's sensor zone.
     */
    public void onPlayerEntered() {
        currentState.onPlayerEntered(this);
    }

    /**
     * Checks if the room clear condition is met and transitions if so.
     */
    public void checkClearCondition() {
        currentState.checkClearCondition(this);
    }

    /**
     * Observer callback: called when an enemy in this room dies.
     *
     * @param enemy The enemy that died
     */
    @Override
    public void onEnemyDied(AbstractEnemy enemy) {
        enemies.remove(enemy);
        checkClearCondition();
    }

    /**
     * Adds an enemy to this room and registers as its death listener.
     *
     * @param enemy The enemy to add
     */
    public void addEnemy(AbstractEnemy enemy) {
        enemies.add(enemy);
        enemy.setDeathListener(this);
    }

    /**
     * Adds a door to this room.
     *
     * @param door The door to add
     */
    public void addDoor(Door door) {
        doors.add(door);
    }

    /**
     * Sets the locked state of all doors.
     *
     * @param locked True to lock, false to unlock
     */
    public void setDoorsLocked(boolean locked) {
        this.doorsLocked = locked;
        for (Door door : doors) {
            if (locked) {
                door.lock();
            } else {
                door.unlock();
            }
        }
    }

    /**
     * Marks this room as permanently cleared.
     */
    public void markAsCleared() {
        this.cleared = true;
    }

    // --- Getters ---

    public String getId() {
        return id;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public RoomState getCurrentState() {
        return currentState;
    }

    public boolean areDoorsLocked() {
        return doorsLocked;
    }

    public boolean isCleared() {
        return cleared;
    }

    public int getRemainingEnemyCount() {
        return (int) enemies.stream().filter(e -> !e.isDead()).count();
    }

    /**
     * Returns an unmodifiable view of the enemies in this room.
     *
     * @return Unmodifiable list of enemies
     */
    public List<AbstractEnemy> getEnemies() {
        return Collections.unmodifiableList(enemies);
    }

    /**
     * Returns an iterator over the enemies for safe removal.
     *
     * @return Iterator over enemies
     */
    public Iterator<AbstractEnemy> enemyIterator() {
        return enemies.iterator();
    }

    /**
     * Returns an unmodifiable view of the doors in this room.
     *
     * @return Unmodifiable list of doors
     */
    public List<Door> getDoors() {
        return Collections.unmodifiableList(doors);
    }

    /**
     * Checks if a position is within this room's bounds.
     *
     * @param x X coordinate to check
     * @param y Y coordinate to check
     * @return True if the position is inside the room
     */
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }
}
