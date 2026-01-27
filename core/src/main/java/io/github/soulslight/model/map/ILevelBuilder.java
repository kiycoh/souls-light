package io.github.soulslight.model.map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.model.enemies.EnemyFactory;
import io.github.soulslight.model.room.RoomData;
import java.util.List;

/**
 * GoF Pattern: Builder (Builder Interface) Check: Defines the steps to create a complex object
 * (Level).
 */
public interface ILevelBuilder {
  ILevelBuilder buildMap(TiledMap map);

  ILevelBuilder buildPhysicsFromMap(World world);

  ILevelBuilder spawnFromTiled(EnemyFactory factory, World world);

  ILevelBuilder spawnRandom(
      EnemyFactory factory,
      World world,
      int meleeCount,
      int rangedCount,
      int tankCount,
      int ballCount,
      boolean spawnBoss);

  ILevelBuilder spawnEnemiesInRooms(EnemyFactory factory, World world);

  ILevelBuilder setEnvironment(String musicTrack, float lightLevel);

  ILevelBuilder buildRooms(List<RoomData> roomDataList);

  ILevelBuilder initializeRoomManager(World world);

  ILevelBuilder spawnCavePortal(World world);

  Level build();
}
