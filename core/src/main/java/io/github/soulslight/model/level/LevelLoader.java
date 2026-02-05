package io.github.soulslight.model.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.manager.ProjectileManager;
import io.github.soulslight.model.DoorMemento;
import io.github.soulslight.model.EnemyMemento;
import io.github.soulslight.model.GameStateMemento;
import io.github.soulslight.model.PlayerMemento;
import io.github.soulslight.model.PortalMemento;
import io.github.soulslight.model.ProjectileMemento;
import io.github.soulslight.model.RoomMemento;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.enemies.Chaser;
import io.github.soulslight.model.enemies.Oblivion;
import io.github.soulslight.model.enemies.Ranger;
import io.github.soulslight.model.enemies.Shielder;
import io.github.soulslight.model.enemies.SpikedBall;
import io.github.soulslight.model.entities.ItemEntity;
import io.github.soulslight.model.entities.Projectile;
import io.github.soulslight.model.factory.EntityCreator;
import io.github.soulslight.model.factory.ItemCreator;
import io.github.soulslight.model.factory.theme.BossLevelFactory;
import io.github.soulslight.model.factory.theme.CaveLevelFactory;
import io.github.soulslight.model.factory.theme.DungeonLevelFactory;
import io.github.soulslight.model.factory.theme.GameLevelFactory;
import io.github.soulslight.model.items.HealthPotion;
import io.github.soulslight.model.map.Level;
import io.github.soulslight.model.map.LevelDirector;
import io.github.soulslight.model.map.LevelFactory;
import io.github.soulslight.model.map.NoiseMapStrategy;
import io.github.soulslight.model.map.StandardLevelBuilder;
import io.github.soulslight.model.player.PlayerManager;
import io.github.soulslight.model.room.RoomData;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles level construction and game state persistence (memento pattern). Extracted from GameModel
 * to satisfy Single Responsibility Principle.
 */
public class LevelLoader {

  private final World physicsWorld;
  private final EntityCreator itemCreator;

  public LevelLoader(World physicsWorld) {
    this.physicsWorld = physicsWorld;
    this.itemCreator = new ItemCreator();
  }

  /**
   * Builds a level based on map type (Dungeon, Cave, or Boss).
   *
   * @param map The generated TiledMap
   * @param roomData Room data extracted from dungeon maps
   * @param hasCavePortal Whether the map has a cave portal
   * @param isRestore Whether this is a restore from saved state
   * @return The constructed Level
   */
  public Level buildLevel(
      TiledMap map, List<RoomData> roomData, boolean hasCavePortal, boolean isRestore) {
    GameLevelFactory levelFactory;
    StandardLevelBuilder builder = new StandardLevelBuilder();
    LevelDirector director = new LevelDirector(builder);
    Level level;

    if (!roomData.isEmpty()) {
      levelFactory = new DungeonLevelFactory();
      if (isRestore) {
        level = director.constructDungeonLevelRestored(map, roomData, levelFactory, physicsWorld);
      } else {
        level = director.constructDungeonLevel(map, roomData, levelFactory, physicsWorld);
      }
    } else if (hasCavePortal) {
      levelFactory = new CaveLevelFactory();
      LevelFactory.EnemyConfig config =
          LevelFactory.getEnemyConfig(
              GameManager.getInstance().getCurrentLevelIndex(),
              GameManager.getInstance().getGameMode());

      if (isRestore) {
        level = director.constructCaveLevelRestored(map, levelFactory, physicsWorld, config);
      } else {
        level = director.constructCaveLevel(map, levelFactory, physicsWorld, config);
      }
    } else {
      levelFactory = new BossLevelFactory();
      LevelFactory.EnemyConfig config =
          LevelFactory.getEnemyConfig(
              GameManager.getInstance().getCurrentLevelIndex(),
              GameManager.getInstance().getGameMode());

      if (isRestore) {
        level = director.constructBossLevelRestored(map, levelFactory, physicsWorld, config);
      } else {
        level = director.constructBossLevel(map, levelFactory, physicsWorld, config);
      }
    }

    return level;
  }

  /** Spawns items in rooms. */
  public void spawnItems(Level level) {
    if (level.getRoomManager() == null) {
      Gdx.app.log("LevelLoader", "spawnItems: RoomManager is null");
      return;
    }

    int totalSpawned = 0;
    for (io.github.soulslight.model.room.Room room : level.getRoomManager().getRooms()) {
      for (int i = 0; i < 2; i++) {
        Vector2 pos = getRandomFloorPosition(level.getMap(), room);
        if (pos != null) {
          ItemEntity item =
              (ItemEntity) itemCreator.createEntity(physicsWorld, pos.x, pos.y, new HealthPotion());
          level.addItem(item);
          totalSpawned++;
          Gdx.app.log("LevelLoader", "Spawned Item at " + pos);
        } else {
          Gdx.app.log("LevelLoader", "Failed to find floor for item in room " + room.getId());
        }
      }
    }
    Gdx.app.log("LevelLoader", "Total items spawned: " + totalSpawned);
  }

  /** Finds a random floor position within a room. */
  public Vector2 getRandomFloorPosition(TiledMap map, io.github.soulslight.model.room.Room room) {
    TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("Ground");
    if (layer == null) {
      if (map.getLayers().getCount() > 0) {
        layer = (TiledMapTileLayer) map.getLayers().get(0);
      } else {
        return null;
      }
    }

    float tileSize = layer.getTileWidth();

    for (int attempts = 0; attempts < 30; attempts++) {
      float offsetX =
          MathUtils.random(
              -room.getBounds().width / 2 + tileSize, room.getBounds().width / 2 - tileSize);
      float offsetY =
          MathUtils.random(
              -room.getBounds().height / 2 + tileSize, room.getBounds().height / 2 - tileSize);

      float randX = room.getBounds().x + room.getBounds().width / 2 + offsetX;
      float randY = room.getBounds().y + room.getBounds().height / 2 + offsetY;

      int cellX = (int) (randX / tileSize);
      int cellY = (int) (randY / tileSize);

      if (cellX >= 0 && cellX < layer.getWidth() && cellY >= 0 && cellY < layer.getHeight()) {
        return new Vector2(randX, randY);
      }
    }
    return null;
  }

  /** Creates a memento snapshot of the current game state. */
  public GameStateMemento createMemento(
      PlayerManager playerManager,
      Level level,
      ProjectileManager projectileManager,
      float currentWill) {

    List<PlayerMemento> playerStates = playerManager.createMementos();

    List<EnemyMemento> enemyStates = new ArrayList<>();
    if (level != null && level.getEnemies() != null) {
      for (AbstractEnemy e : level.getEnemies()) {
        if (!e.isDead()) {
          enemyStates.add(
              new EnemyMemento(
                  getEnemyType(e), e.getPosition().x, e.getPosition().y, e.getHealth()));
        }
      }
    }

    List<ProjectileMemento> projectileStates = new ArrayList<>();
    for (Projectile p : projectileManager.getProjectiles()) {
      if (!p.shouldDestroy()) {
        Vector2 vel = p.getBody().getLinearVelocity();
        projectileStates.add(
            new ProjectileMemento(p.getPosition().x, p.getPosition().y, vel.x, vel.y));
      }
    }

    // Save Map State
    List<RoomMemento> roomStates = new ArrayList<>();
    List<DoorMemento> doorStates = new ArrayList<>();
    List<PortalMemento> portalStates = new ArrayList<>();

    if (level != null && level.getRoomManager() != null) {
      for (io.github.soulslight.model.room.Room r : level.getRoomManager().getRooms()) {
        roomStates.add(new RoomMemento(r.getId(), r.isCleared(), r.areDoorsLocked()));

        for (io.github.soulslight.model.room.Door d : r.getDoors()) {
          doorStates.add(new DoorMemento(doorStates.size(), d.isLocked()));
        }
      }

      io.github.soulslight.model.room.PortalRoom pr = level.getRoomManager().getPortalRoom();
      if (pr != null && pr.getPortal() != null) {
        portalStates.add(new PortalMemento(pr.getPortal().isActivated()));
      }
    }

    if (level != null && level.getCavePortal() != null) {
      portalStates.add(new PortalMemento(level.getCavePortal().isActivated()));
    }

    return new GameStateMemento(
        playerStates,
        enemyStates,
        projectileStates,
        roomStates,
        doorStates,
        portalStates,
        GameManager.getInstance().getCampaignSeed(),
        GameManager.getInstance().getCurrentLevelIndex(),
        currentWill);
  }

  /** Gets the type name of an enemy for memento persistence. */
  public String getEnemyType(AbstractEnemy e) {
    if (e instanceof Chaser) return "Chaser";
    if (e instanceof Ranger) return "Ranger";
    if (e instanceof Shielder) return "Shielder";
    if (e instanceof SpikedBall) return "SpikedBall";
    if (e instanceof Oblivion) return "Oblivion";
    return "Chaser"; // Fallback
  }

  /** Determines if a map has a cave portal. */
  public boolean hasCavePortal(TiledMap map) {
    return map.getProperties().containsKey(NoiseMapStrategy.PORTAL_POSITION_KEY);
  }
}
