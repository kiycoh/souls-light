package io.github.soulslight.model.room;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.model.entities.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pattern: Mediator Coordinates room lifecycle and acts as central event
 * dispatcher. Manages room
 * updates, sensor creation, and level completion checks.
 */
public class RoomManager {

  private final List<Room> rooms;
  private final List<RoomSensor> sensors;
  private World world;
  private List<Player> players; // Reference for teleportation on combat start

  public RoomManager() {
    this.rooms = new ArrayList<>();
    this.sensors = new ArrayList<>();
    this.players = new ArrayList<>();
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
   * Sets the players reference for teleportation when combat starts.
   *
   * @param players The list of player entities
   */
  public void setPlayers(List<Player> players) {
    this.players = players != null ? players : new ArrayList<>();
  }

  /**
   * Gets the players reference.
   *
   * @return The list of player entities (never null)
   */
  public List<Player> getPlayers() {
    return players;
  }

  /**
   * Adds a room to the manager.
   *
   * @param room The room to add
   */
  public void addRoom(Room room) {
    rooms.add(room);
    room.setRoomManager(this);
  }

  /** Creates sensors for all rooms that don't have them yet. */
  private void createSensors() {
    if (world == null)
      return;

    for (Room room : rooms) {
      RoomSensor sensor = new RoomSensor(room);
      sensor.createSensor(world);
      sensors.add(sensor);
    }
  }

  /** Initializes doors for all rooms. */
  public void initializeDoors() {
    if (world == null)
      return;

    for (Room room : rooms) {
      for (Door door : room.getDoors()) {
        door.initialize(world);
      }
    }
  }

  /** Locks all doors in all rooms. Called when combat begins in any room. */
  public void lockAllDoors() {
    for (Room room : rooms) {
      for (Door door : room.getDoors()) {
        door.lock();
      }
    }
  }

  /** Unlocks all doors in all rooms. Called when any room is cleared. */
  public void unlockAllDoors() {
    for (Room room : rooms) {
      for (Door door : room.getDoors()) {
        door.unlock();
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

  /** Cleans up all sensors and clears rooms. */
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
