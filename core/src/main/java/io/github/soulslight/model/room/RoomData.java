package io.github.soulslight.model.room;

import com.badlogic.gdx.math.Rectangle;
import java.util.Collections;
import java.util.List;

/**
 * Data record for room generation metadata. Used to transfer room data from map generation to
 * gameplay room creation.
 */
public record RoomData(
    String id, Rectangle bounds, RoomType type, List<DoorPosition> doorPositions) {

  public enum RoomType {
    STANDARD,
    PORTAL,
    SERENITY
  }

  /**
   * Creates a standard room data entry with door positions.
   *
   * @param id Unique room identifier
   * @param x X position in world units
   * @param y Y position in world units
   * @param width Room width in world units
   * @param height Room height in world units
   * @param doorPositions List of door positions for this room
   * @return RoomData for a standard room
   */
  public static RoomData standard(
      String id, float x, float y, float width, float height, List<DoorPosition> doorPositions) {
    return new RoomData(id, new Rectangle(x, y, width, height), RoomType.STANDARD, doorPositions);
  }

  /** Creates a standard room data entry without door positions (backward compatible). */
  public static RoomData standard(String id, float x, float y, float width, float height) {
    return new RoomData(
        id, new Rectangle(x, y, width, height), RoomType.STANDARD, Collections.emptyList());
  }

  /**
   * Creates a portal room data entry with door positions.
   *
   * @param id Unique room identifier
   * @param x X position in world units
   * @param y Y position in world units
   * @param width Room width in world units
   * @param height Room height in world units
   * @param doorPositions List of door positions for this room
   * @return RoomData for a portal room
   */
  public static RoomData portal(
      String id, float x, float y, float width, float height, List<DoorPosition> doorPositions) {
    return new RoomData(id, new Rectangle(x, y, width, height), RoomType.PORTAL, doorPositions);
  }

  /** Creates a portal room data entry without door positions (backward compatible). */
  public static RoomData portal(String id, float x, float y, float width, float height) {
    return new RoomData(
        id, new Rectangle(x, y, width, height), RoomType.PORTAL, Collections.emptyList());
  }

  /**
   * Creates a serenity room data entry with door positions.
   *
   * @param id Unique room identifier
   * @param x X position in world units
   * @param y Y position in world units
   * @param width Room width in world units
   * @param height Room height in world units
   * @param doorPositions List of door positions for this room
   * @return RoomData for a serenity room
   */
  public static RoomData serenity(
      String id, float x, float y, float width, float height, List<DoorPosition> doorPositions) {
    return new RoomData(id, new Rectangle(x, y, width, height), RoomType.SERENITY, doorPositions);
  }
}
