package io.github.soulslight.model.map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.model.factory.theme.GameLevelFactory;
import io.github.soulslight.model.room.RoomData;
import java.util.List;

/**
 * GoF Pattern: Builder (Director) Controls the construction order of the Level using the Builder
 * instance.
 */
public class LevelDirector {
  private final ILevelBuilder builder;

  public LevelDirector(ILevelBuilder builder) {
    this.builder = builder;
  }

  /** Constructs a Dungeon-style level (Rooms, Doors, Hallways). */
  public Level constructDungeonLevel(
      TiledMap map, List<RoomData> roomData, GameLevelFactory themeFactory, World world) {
    return builder
        .buildMap(map)
        .buildRooms(roomData)
        .initializeRoomManager(world)
        .buildPhysicsFromMap(world)
        .spawnEnemiesInRooms(themeFactory.getEnemyFactory(), world)
        .setEnvironment(themeFactory.getMusicPath(), themeFactory.getMusicVolume())
        .build();
  }

  /** Constructs a Cave-style level (Open area, random spawns, portal). */
  public Level constructCaveLevel(
      TiledMap map, GameLevelFactory themeFactory, World world, LevelFactory.EnemyConfig config) {
    return builder
        .buildMap(map)
        .buildPhysicsFromMap(world)
        .spawnRandom(
            themeFactory.getEnemyFactory(),
            world,
            config.melee(),
            config.ranged(),
            config.tank(),
            config.ball(),
            config.spawnBoss())
        .spawnCavePortal(world)
        .setEnvironment(themeFactory.getMusicPath(), themeFactory.getMusicVolume())
        .build();
  }

  /** Constructs a Boss/Arena level (Open area, specific spawns). */
  public Level constructBossLevel(
      TiledMap map, GameLevelFactory themeFactory, World world, LevelFactory.EnemyConfig config) {
    return builder
        .buildMap(map)
        .buildPhysicsFromMap(world)
        .spawnRandom(
            themeFactory.getEnemyFactory(),
            world,
            config.melee(),
            config.ranged(),
            config.tank(),
            config.ball(),
            config.spawnBoss())
        .setEnvironment(themeFactory.getMusicPath(), themeFactory.getMusicVolume())
        .build();
  }

  // --- RESTORED VARIANTS (Skip Enemy Spawning) ---

  public Level constructDungeonLevelRestored(
      TiledMap map, List<RoomData> roomData, GameLevelFactory themeFactory, World world) {
    return builder
        .buildMap(map)
        .buildRooms(roomData)
        .initializeRoomManager(world)
        .buildPhysicsFromMap(world)
        // Skip spawnEnemiesInRooms
        .setEnvironment(themeFactory.getMusicPath(), themeFactory.getMusicVolume())
        .build();
  }

  public Level constructCaveLevelRestored(
      TiledMap map, GameLevelFactory themeFactory, World world, LevelFactory.EnemyConfig config) {
    return builder
        .buildMap(map)
        .buildPhysicsFromMap(world)
        // Skip spawnRandom
        .spawnCavePortal(world)
        .setEnvironment(themeFactory.getMusicPath(), themeFactory.getMusicVolume())
        .build();
  }

  public Level constructBossLevelRestored(
      TiledMap map, GameLevelFactory themeFactory, World world, LevelFactory.EnemyConfig config) {
    return builder
        .buildMap(map)
        .buildPhysicsFromMap(world)
        // Skip spawnRandom
        .setEnvironment(themeFactory.getMusicPath(), themeFactory.getMusicVolume())
        .build();
  }
}
