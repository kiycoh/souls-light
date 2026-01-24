package io.github.soulslight.model.room;

import com.badlogic.gdx.math.Vector2;

/**
 * Record for door positioning data from map generation.
 * Used to place doors at actual corridor entry points.
 */
public record DoorPosition(Vector2 position, Direction direction) {

    /** Cardinal direction indicating which side of the room the door is on. */
    public enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    /**
     * Creates a door position in world coordinates.
     *
     * @param x         X position in world units
     * @param y         Y position in world units
     * @param direction Door direction
     * @return DoorPosition record
     */
    public static DoorPosition of(float x, float y, Direction direction) {
        return new DoorPosition(new Vector2(x, y), direction);
    }
}
