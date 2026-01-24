package io.github.soulslight.model.map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import io.github.soulslight.model.room.RoomData;
import java.util.Collections;
import java.util.List;

/** Pattern: Strategy (Abstract Strategy) Sealed interface for map generation algorithms. */
public sealed interface MapGenerationStrategy
    permits DungeonMapStrategy, NoiseMapStrategy, BossArenaStrategy {
  TiledMap generate();

  /**
   * Returns room data for room-based gameplay (lock/unlock mechanics). Default implementation
   * returns empty list for strategies without discrete rooms.
   *
   * @return List of room data, or empty list if not applicable
   */
  default List<RoomData> getRoomData() {
    return Collections.emptyList();
  }
}
