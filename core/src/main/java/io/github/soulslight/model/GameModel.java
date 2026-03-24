package io.github.soulslight.model;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.manager.ParticleManager;
import io.github.soulslight.manager.PathfindingManager;
import io.github.soulslight.manager.ProjectileManager;
import io.github.soulslight.model.combat.CombatSystem;
import io.github.soulslight.model.combat.ProjectileListener;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.enemies.EnemyRegistry;
import io.github.soulslight.model.enemies.Oblivion;
import io.github.soulslight.model.enemies.Shielder;
import io.github.soulslight.model.entities.ItemEntity;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.entities.Projectile;
import io.github.soulslight.model.level.LevelLoader;
import io.github.soulslight.model.map.DungeonMapStrategy;
import io.github.soulslight.model.map.Level;
import io.github.soulslight.model.map.MapGenerationStrategy;
import io.github.soulslight.model.observer.Subject;
import io.github.soulslight.model.player.PlayerManager;
import io.github.soulslight.model.room.EnemyDeathListener;
import io.github.soulslight.model.room.RoomData;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GameModel extends Subject
    implements Disposable,
        ProjectileListener,
        EnemyDeathListener,
        Player.DamageListener,
        CombatSystem.CombatEventListener {

  private final PlayerManager playerManager;
  private final CombatSystem combatSystem;
  private final LevelLoader levelLoader;

  private final World physicsWorld;
  private float currentWill;
  private boolean isPaused;
  private Level level;

  // Accumulator for fixed timestep
  private float physicsAccumulator = 0;

  private boolean levelCompleted = false;

  private io.github.soulslight.utils.CollisionMonitor collisionMonitor;

  public void setLevelCompleted(boolean completed) {
    this.levelCompleted = completed;
    if (completed) {
      notifyObservers("LEVEL_COMPLETE", this.level);
    }
  }


  private final ProjectileManager projectileManager;
  private final io.github.soulslight.model.lighting.LightingSystem lightingSystem;

  public GameModel() {
    EnemyRegistry.loadCache(null);
    this.lightingSystem = new io.github.soulslight.model.lighting.LightingSystem();
    this.physicsWorld = new World(new Vector2(0, 0), true);
    io.github.soulslight.model.physics.Box2DPhysicsAdapter physicsAdapter =
        new io.github.soulslight.model.physics.Box2DPhysicsAdapter();
    this.physicsWorld.setContactListener(physicsAdapter);

    // Initialize Collision Monitor (Observer)
    this.collisionMonitor = new io.github.soulslight.utils.CollisionMonitor();
    physicsAdapter.getCollisionHandler().attach(this.collisionMonitor);

    this.currentWill = Constants.MAX_WILL / 2;
    this.isPaused = false;

    // Initialize PlayerManager
    this.playerManager = new PlayerManager();

    // Initialize LevelLoader
    this.levelLoader = new LevelLoader(physicsWorld);

    // ---- PROCEDURALLY GENERATED MAP (Level-Based Strategy) ----
    MapGenerationStrategy strategy = GameManager.getInstance().getCurrentLevelStrategy();
    TiledMap myMap = strategy.generate();
    this.lightingSystem.prepareLightingOverlay(myMap);

    // ---- PLAYERS: spawn on valid flood tile ----
    List<RoomData> roomData = DungeonMapStrategy.extractRoomData(myMap);
    Vector2 spawn = playerManager.findFirstFloorSpawn(myMap, roomData);

    // Delegate player spawning to PlayerManager
    playerManager.spawnPlayers(this.physicsWorld, spawn, this, this);

    // Projectile Manager
    this.projectileManager = new ProjectileManager(physicsWorld);

    // Initialize CombatSystem
    this.combatSystem = new CombatSystem(physicsWorld, projectileManager);
    this.combatSystem.setEventListener(this);

    // Initialize Pathfinding
    TiledMapTileLayer groundLayer = (TiledMapTileLayer) myMap.getLayers().get(0);
    GameManager.getInstance().setPathfindingManager(new PathfindingManager(groundLayer));

    // ---- MAP TYPE DETECTION: Dungeon (rooms) vs Cave (roomless) ----
    boolean hasCavePortal = levelLoader.hasCavePortal(myMap);

    // Unified Level Construction - delegate to LevelLoader
    this.level = levelLoader.buildLevel(myMap, roomData, hasCavePortal, false);

    // Shielder 'target' setup and Listener registration
    if (this.level.getEnemies() != null) {
      for (AbstractEnemy e : this.level.getEnemies()) {
        e.addProjectileListener(combatSystem); // Register combatSystem as listener
        e.addDeathListener(this); // Register death listener
        if (e instanceof Shielder) {
          ((Shielder) e).setAllies(this.level.getEnemies());
        }
      }
    }

    // Spawn Items - delegate to LevelLoader
    levelLoader.spawnItems(this.level);

    // Wire player reference for teleportation on combat start
    if (this.level.getRoomManager() != null && !getPlayers().isEmpty()) {
      this.level.getRoomManager().setPlayers(getPlayers());
    }

    GameManager.getInstance().setCurrentLevel(this.level);
  }

  public void update(float deltaTime) {
    if (isPaused) return;

    // Delegate player updates to PlayerManager
    playerManager.updatePlayers(deltaTime);

    // Revive Logic - delegate to PlayerManager
    playerManager.updateReviveLogic(deltaTime);

    // Combat updates - delegate to CombatSystem
    combatSystem.updateEnemies(level, getPlayers(), deltaTime);

    physicsAccumulator += deltaTime;

    while (physicsAccumulator >= 1 / 60f) {
      physicsWorld.step(1 / 60f, 6, 2);
      // Update projectiles for all players
      if (!getPlayers().isEmpty()) {
        projectileManager.update(1 / 60f, getPlayers(), getActiveEnemies());
      }
      physicsAccumulator -= 1 / 60f;
    }

    // Enemy cleanup - delegate to CombatSystem
    combatSystem.cleanDeadEnemies(level);
    cleanPickedUpItems();

    // Update room states (lock/unlock, clear checks)
    if (level != null && level.getRoomManager() != null) {
      level.getRoomManager().update(deltaTime);
    }

    // Update Cave Portal if exists
    if (level != null && level.getCavePortal() != null) {
      level.getCavePortal().update(deltaTime);
    }

    // Update Lighting
    if (!getPlayers().isEmpty() && getMap() != null) {
      Player p = getPlayers().get(0); // Follow first player for now
      TiledMapTileLayer layer = (TiledMapTileLayer) getMap().getLayers().get(0);
      float tileSize = layer.getTileWidth();
      lightingSystem.update(p.getPosition().x, p.getPosition().y, tileSize);
    }

    // Update Collision Monitor
    if (collisionMonitor != null) {
      collisionMonitor.tick(deltaTime);
    }
  }

  @Override
  public void onProjectileRequest(Vector2 origin, Vector2 target, String type, float damage) {
    // Delegate to CombatSystem
    combatSystem.onProjectileRequest(origin, target, type, damage);
  }

  @Override
  public void onDamageTaken(Player player, float amount) {
    notifyObservers("PLAYER_HIT", player);
  }

  // --- CombatSystem.CombatEventListener Implementation ---

  @Override
  public void onBossDefeated() {
    this.levelCompleted = true;
    notifyObservers("BOSS_DEFEATED", null);
  }

  @Override
  public void onPlayerHit(Player player) {
    notifyObservers("PLAYER_HIT", player);
  }

  @Override
  public void onProjectileRequest(
      Vector2 origin,
      io.github.soulslight.model.entities.Entity targetEntity,
      String type,
      float damage) {
    // Delegate to CombatSystem
    combatSystem.onProjectileRequest(origin, targetEntity, type, damage);
  }

  private void cleanPickedUpItems() {
    if (level == null) return;

    Iterator<ItemEntity> it = level.getItems().iterator();
    while (it.hasNext()) {
      ItemEntity item = it.next();
      item.update(0); // Update pos if dynamic
      if (item.isMarkedForRemoval()) {
        physicsWorld.destroyBody(item.getBody());
        it.remove();
      }
    }
  }

  public GameStateMemento createMemento() {
    // Delegate memento creation to LevelLoader
    return levelLoader.createMemento(playerManager, level, projectileManager, currentWill);
  }

  public void restoreMemento(GameStateMemento memento) {
    if (memento == null || memento.players == null) return;

    // Dispose Level first to clean up Managers (RoomManager destroys sensors)
    // allowing them to remove bodies safely before we wipe the world.
    if (level != null) level.dispose();

    // Clear logical lists
    playerManager.clearPlayers();
    this.projectileManager.getProjectiles().clear();
    ParticleManager.getInstance().clear();

    com.badlogic.gdx.utils.Array<com.badlogic.gdx.physics.box2d.Body> bodies =
        new com.badlogic.gdx.utils.Array<>();
    physicsWorld.getBodies(bodies);
    for (com.badlogic.gdx.physics.box2d.Body b : bodies) {
      physicsWorld.destroyBody(b);
    }

    // Restore Seed to GameManager so map generation is consistent!
    GameManager.getInstance().setCampaignSeed(memento.seed);

    // Fix: Restore Level Index BEFORE generating map
    GameManager.getInstance().setCurrentLevelIndex(memento.currentLevelIndex);
    this.currentWill = memento.currentWill;

    // Rebuild Map (using level-based strategy)
    MapGenerationStrategy strategy = GameManager.getInstance().getCurrentLevelStrategy();
    TiledMap newMap = strategy.generate();
    this.lightingSystem.prepareLightingOverlay(newMap);

    // Initialize Pathfinding for restored map
    TiledMapTileLayer groundLayer = (TiledMapTileLayer) newMap.getLayers().get(0);
    GameManager.getInstance().setPathfindingManager(new PathfindingManager(groundLayer));

    // Extract room data for reconstruction
    List<RoomData> roomData = DungeonMapStrategy.extractRoomData(newMap);
    boolean hasCavePortal = levelLoader.hasCavePortal(newMap);

    // Unified Level Construction (Restore Mode) - delegate to LevelLoader
    this.level = levelLoader.buildLevel(newMap, roomData, hasCavePortal, true);

    GameManager.getInstance().setCurrentLevel(this.level);

    // Delegate player restoration to PlayerManager
    playerManager.restorePlayers(memento.players, physicsWorld, level, this, this);

    // Update RoomManager with restored players so states can detect them
    level.getRoomManager().setPlayers(getPlayers());

    // 3. Restore Map State (Rooms, Doors, Portals)
    if (level.getRoomManager() != null && memento.rooms != null) {
      List<io.github.soulslight.model.room.Room> currentRooms = level.getRoomManager().getRooms();

      // We assume strict ordering: generated rooms match memento rooms 1:1
      for (int i = 0; i < currentRooms.size() && i < memento.rooms.size(); i++) {
        io.github.soulslight.model.room.Room room = currentRooms.get(i);
        RoomMemento rm = memento.rooms.get(i);

        if (rm.isCleared) {
          room.forceCleared(); // Marks cleared, kills enemies, unlocks doors
        } else if (rm.doorsLocked) {
          // If doors are locked and not cleared, we were in combat.
          // Force transition to ActiveCombatState.
          room.transitionTo(io.github.soulslight.model.room.ActiveCombatState.INSTANCE);
        } else {
          // Default is PassiveState. Ensure doors are unlocked.
          room.setDoorsLocked(false);
        }
      }

      // Restore Doors (Specific states override room defaults if needed)
      // Actually doorStates is a flattened list.
      if (memento.doors != null) {
        int doorIndex = 0;
        for (io.github.soulslight.model.room.Room r : currentRooms) {
          for (io.github.soulslight.model.room.Door d : r.getDoors()) {
            if (doorIndex < memento.doors.size()) {
              DoorMemento dm = memento.doors.get(doorIndex);
              if (dm.isLocked) d.lock();
              else d.unlock();
            }
            doorIndex++;
          }
        }
      }

      // Restore PortalRoom portal
      if (memento.portals != null && !memento.portals.isEmpty()) {
        io.github.soulslight.model.room.PortalRoom pr = level.getRoomManager().getPortalRoom();
        if (pr != null && pr.getPortal() != null && memento.portals.get(0).isActivated) {
          // Use reflection or add setter if needed, or just force flag
          // Since Portal field is 'activated', we might need a method or simulate
          // interaction
          // For now, let's assume we just want to save completion state
          if (memento.portals.get(0).isActivated) {
            pr.getPortal().tryActivate(); // Attempt activation
          }
        }
      }
    }

    // Restore Cave Portal
    if (level.getCavePortal() != null && memento.portals != null) {
      // Cave portal is usually last or second if existing
      // If dungeon, portal[0] is portal room. If cave, portal[0] is cave portal.
      // Simplification: just check if any in list are true.
      for (PortalMemento pm : memento.portals) {
        if (pm.isActivated) level.getCavePortal().tryActivate();
      }
    }

    // Recreate Enemies
    // CRITICAL: We only want to spawn enemies that were alive.
    if (memento.enemies != null) {
      for (EnemyMemento em : memento.enemies) {
        AbstractEnemy enemy = EnemyRegistry.getEnemy(em.type);
        if (enemy != null) {
          enemy.createBody(physicsWorld, em.x, em.y);
          enemy.setHealth(em.health);
          this.level.addEnemy(enemy);
        }
      }
      // Restore Shielder links and Listener registration
      for (AbstractEnemy e : this.level.getEnemies()) {
        e.addProjectileListener(this); // Register listener
        e.addDeathListener(this); // Register death listener

        // Fix: Add enemy to containing room so RoomState logic works!
        if (this.level.getRoomManager() != null) {
          io.github.soulslight.model.room.Room room =
              this.level.getRoomManager().findRoomContaining(e.getPosition());
          if (room != null) {
            room.addEnemy(e);
          }
        }

        if (e instanceof Shielder) {
          ((Shielder) e).setAllies(this.level.getEnemies());
        }
      }
    } else {
      // Fallback for legacy saves or if no enemy list:
      // If we loaded a level but have no enemy data, we might be stuck in empty
      // level.
      // But memento.enemies should be empty only if all dead.
    }

    // Recreate Projectiles - delegate to CombatSystem
    if (memento.projectiles != null) {
      for (ProjectileMemento pm : memento.projectiles) {
        combatSystem.restoreProjectile(pm.x, pm.y, pm.vx, pm.vy);
      }
    }

    notifyObservers("LEVEL_RESTORED", this.level);
  }

  public List<Projectile> getProjectiles() {
    return projectileManager.getProjectiles();
  }

  public World getWorld() {
    return physicsWorld;
  }

  public java.util.List<Player> getPlayers() {
    return playerManager.getPlayers();
  }

  // Backward compatibility
  public Player getPlayer() {
    return playerManager.getPlayer();
  }

  public Level getLevel() {
    return level;
  }

  public TiledMap getMap() {
    return (level != null) ? level.getMap() : null;
  }

  public List<AbstractEnemy> getActiveEnemies() {
    return (level != null) ? level.getEnemies() : Collections.emptyList();
  }

  @Override
  public void onEnemyDied(AbstractEnemy enemy) {
    if (enemy instanceof Oblivion && ((Oblivion) enemy).isPhaseTwo()) {
      setLevelCompleted(true);
    }
  }

  public float getCurrentWill() {
    return currentWill;
  }

  public void setCurrentWill(float will) {
    this.currentWill = will;
  }

  public boolean isPaused() {
    return isPaused;
  }

  public void setPaused(boolean paused) {
    this.isPaused = paused;
  }

  public boolean isLevelCompleted() {
    return levelCompleted;
  }

  public int getTotalEnemiesKilled() {
    return combatSystem.getTotalEnemiesKilled();
  }

  public io.github.soulslight.model.lighting.LightingSystem getLightingSystem() {
    return lightingSystem;
  }

  @Override
  public void dispose() {
    // Dispose level and entities FIRST before destroying the world they live in!
    if (level != null) level.dispose();

    if (physicsWorld != null) physicsWorld.dispose();

    GameManager.getInstance().cleanUp();
  }
}
