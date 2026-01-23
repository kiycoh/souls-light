package io.github.soulslight.model.room;

import com.badlogic.gdx.math.Rectangle;

/**
 * Data record for room generation metadata. Used to transfer room data from map generation to
 * gameplay room creation.
 */
public record RoomData(String id, Rectangle bounds, boolean isPortalRoom) {
  /**
   * Creates a standard room data entry.
   *
   * @param id Unique room identifier
   * @param x X position in world units
   * @param y Y position in world units
   * @param width Room width in world units
   * @param height Room height in world units
   * @return RoomData for a standard room
   */
  public static RoomData standard(String id, float x, float y, float width, float height) {
    return new RoomData(id, new Rectangle(x, y, width, height), false);
  }

  /**
   * Creates a portal room data entry.
   *
   * @param id Unique room identifier
   * @param x X position in world units
   * @param y Y position in world units
   * @param width Room width in world units
   * @param height Room height in world units
   * @return RoomData for a portal room
   */
  public static RoomData portal(String id, float x, float y, float width, float height) {
    return new RoomData(id, new Rectangle(x, y, width, height), true);
  }
}
