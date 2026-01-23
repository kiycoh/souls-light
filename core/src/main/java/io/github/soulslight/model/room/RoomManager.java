package io.github.soulslight.model.room;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pattern: Mediator
 * Coordinates room lifecycle and acts as central event dispatcher.
 * Manages room updates, sensor creation, and level completion checks.
 */
public class RoomManager {

    private final List<Room> rooms;
    private final List<RoomSensor> sensors;
    private World world;

    public RoomManager() {
        this.rooms = new ArrayList<>();
        this.sensors = new ArrayList<>();
    }

    /**
     * Initializes the room manager with a physics world.
     *
     * @param world The Box2D physics world
     */
    public void initialize(World world) {
        this.world = world;
        createSensors();
    }

    /**
     * Adds a room to the manager.
     *
     * @param room The room to add
     */
    public void addRoom(Room room) {
        rooms.add(room);
    }

    /**
     * Creates sensors for all rooms that don't have them yet.
     */
    private void createSensors() {
        if (world == null)
            return;

        for (Room room : rooms) {
            RoomSensor sensor = new RoomSensor(room);
            sensor.createSensor(world);
            sensors.add(sensor);
        }
    }

    /**
     * Initializes doors for all rooms.
     */
    public void initializeDoors() {
        if (world == null)
            return;

        for (Room room : rooms) {
            for (Door door : room.getDoors()) {
                door.initialize(world);
            }
        }
    }

    /**
     * Updates all rooms.
     *
     * @param deltaTime Time since last update
     */
    public void update(float deltaTime) {
        for (Room room : rooms) {
            room.update(deltaTime);
        }
    }

    /**
     * Finds the room containing the specified position.
     *
     * @param position The position to check
     * @return The room containing the position, or null if none
     */
    public Room findRoomContaining(Vector2 position) {
        for (Room room : rooms) {
            if (room.contains(position.x, position.y)) {
                return room;
            }
        }
        return null;
    }

    /**
     * Checks if all rooms in the level are cleared.
     *
     * @return True if all rooms are cleared
     */
    public boolean allRoomsCleared() {
        for (Room room : rooms) {
            if (!room.isCleared()) {
                return false;
            }
        }
        return !rooms.isEmpty();
    }

    /**
     * Gets the number of rooms in the manager.
     *
     * @return The room count
     */
    public int getRoomCount() {
        return rooms.size();
    }

    /**
     * Gets the number of cleared rooms.
     *
     * @return The cleared room count
     */
    public int getClearedRoomCount() {
        return (int) rooms.stream().filter(Room::isCleared).count();
    }

    /**
     * Returns an unmodifiable view of all rooms.
     *
     * @return Unmodifiable list of rooms
     */
    public List<Room> getRooms() {
        return Collections.unmodifiableList(rooms);
    }

    /**
     * Cleans up all sensors and clears rooms.
     */
    public void dispose() {
        if (world != null) {
            for (RoomSensor sensor : sensors) {
                sensor.destroySensor(world);
            }
        }
        sensors.clear();
        rooms.clear();
    }
}
