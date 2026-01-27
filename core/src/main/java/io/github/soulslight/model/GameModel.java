package io.github.soulslight.model;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.manager.ProjectileManager;
import io.github.soulslight.model.combat.ProjectileListener;
import io.github.soulslight.model.enemies.*;
import io.github.soulslight.model.entities.ItemEntity;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.entities.Projectile;
import io.github.soulslight.model.factory.EntityCreator;
import io.github.soulslight.model.factory.ItemCreator;
import io.github.soulslight.model.factory.PlayerCreator;
import io.github.soulslight.model.factory.ProjectileCreator;
import io.github.soulslight.model.factory.theme.BossLevelFactory;
import io.github.soulslight.model.factory.theme.CaveLevelFactory;
import io.github.soulslight.model.factory.theme.DungeonLevelFactory;
import io.github.soulslight.model.factory.theme.GameLevelFactory;
import io.github.soulslight.model.items.HealthPotion;
import io.github.soulslight.model.map.DungeonMapStrategy;
import io.github.soulslight.model.map.Level;
import io.github.soulslight.model.map.LevelDirector;
import io.github.soulslight.model.map.LevelFactory;
import io.github.soulslight.model.map.MapGenerationStrategy;
import io.github.soulslight.model.map.NoiseMapStrategy;
import io.github.soulslight.model.map.StandardLevelBuilder;
import io.github.soulslight.model.observer.Subject;
import io.github.soulslight.model.room.EnemyDeathListener;
import io.github.soulslight.model.room.RoomData;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GameModel extends Subject
    implements Disposable, ProjectileListener, EnemyDeathListener {

  private final EntityCreator playerCreator = new PlayerCreator();
  private final EntityCreator itemCreator = new ItemCreator();
  private final EntityCreator projectileCreator = new ProjectileCreator();

  public static final float MAX_WILL = 100f;
  private final World physicsWorld;
  private float currentWill;
  private boolean isPaused;
  private java.util.List<Player> players;
  private Level level;
  private long currentSeed;

  // Accumulator for fixed timestep
  private float physicsAccumulator = 0;

  // Level completion flag for portal transition
  private boolean levelCompleted = false;

  public void setLevelCompleted(boolean completed) {
    this.levelCompleted = completed;
    if (completed) {
      notifyObservers("LEVEL_COMPLETE", this.level);
    }
  }

  private int totalEnemiesKilled = 0;

  private final ProjectileManager projectileManager;
  private final io.github.soulslight.model.lighting.LightingSystem lightingSystem;

  public GameModel() {
    EnemyRegistry.loadCache(null);
    this.lightingSystem = new io.github.soulslight.model.lighting.LightingSystem();
    this.physicsWorld = new World(new Vector2(0, 0), true);
    this.physicsWorld.setContactListener(
        new io.github.soulslight.model.physics.Box2DPhysicsAdapter());

    this.currentWill = MAX_WILL / 2;
    this.isPaused = false;
    this.players = new java.util.ArrayList<>();

    // ---- PROCEDURALLY GENERATED MAP (Level-Based Strategy) ----
    this.currentSeed = System.currentTimeMillis();
    MapGenerationStrategy strategy = GameManager.getInstance().getCurrentLevelStrategy();
    TiledMap myMap = strategy.generate();
    this.lightingSystem.prepareLightingOverlay(myMap);

    // ---- PLAYERS: spawn on valid flood tile ----
    List<RoomData> roomData = DungeonMapStrategy.extractRoomData(myMap);
    Vector2 spawn = findFirstFloorSpawn(myMap, roomData);

    // Player 1: Uses class selected in ClassSelectionScreen
    Player.PlayerClass p1Class = GameManager.getInstance().getPlayerClass(0);
    Player p1 = (Player) playerCreator.createEntity(this.physicsWorld, spawn.x, spawn.y, p1Class);
    p1.addProjectileListener(this); // Register listener
    players.add(p1);
    GameManager.getInstance().addPlayer(p1);

    // Player 2 (spawn slightly offset) - Only if NOT single player
    if (!io.github.soulslight.manager.SettingsManager.getInstance().isSinglePlayer()) {
      Player.PlayerClass p2Class = GameManager.getInstance().getPlayerClass(1);
      Player p2 =
          (Player) playerCreator.createEntity(this.physicsWorld, spawn.x + 20, spawn.y, p2Class);
      p2.addProjectileListener(this); // Register listener
      players.add(p2);
      GameManager.getInstance().addPlayer(p2);
    }

    // Projectile Manager
    this.projectileManager = new ProjectileManager(physicsWorld);

    // ---- MAP TYPE DETECTION: Dungeon (rooms) vs Cave (roomless) ----
    boolean hasCavePortal = myMap.getProperties().containsKey(NoiseMapStrategy.PORTAL_POSITION_KEY);
    GameLevelFactory levelFactory;

    // GoF Builder: Create Director and Builder
    StandardLevelBuilder builder = new StandardLevelBuilder();
    LevelDirector director = new LevelDirector(builder);

    if (!roomData.isEmpty()) {
      levelFactory = new DungeonLevelFactory();
      // ---- DUNGEON-STYLE LEVEL (rooms + doors + portal room) ----
      this.level = director.constructDungeonLevel(myMap, roomData, levelFactory, this.physicsWorld);

    } else if (hasCavePortal) {
      levelFactory = new CaveLevelFactory();
      // ---- CAVE-STYLE LEVEL (random spawn + cave portal) ----
      LevelFactory.EnemyConfig config =
          LevelFactory.getEnemyConfig(
              GameManager.getInstance().getCurrentLevelIndex(),
              GameManager.getInstance().getGameMode());

      this.level = director.constructCaveLevel(myMap, levelFactory, this.physicsWorld, config);

    } else {
      levelFactory = new BossLevelFactory();
      // ---- BOSS ARENA OR CUSTOM (minimal setup) ----
      LevelFactory.EnemyConfig config =
          LevelFactory.getEnemyConfig(
              GameManager.getInstance().getCurrentLevelIndex(),
              GameManager.getInstance().getGameMode());

      this.level = director.constructBossLevel(myMap, levelFactory, this.physicsWorld, config);
    }

    // Shielder 'target' setup and Listener registration
    if (this.level.getEnemies() != null) {
      for (AbstractEnemy e : this.level.getEnemies()) {
        e.addProjectileListener(this); // Register listener
        e.addDeathListener(this); // Register death listener
        if (e instanceof Shielder) {
          ((Shielder) e).setAllies(this.level.getEnemies());
        }
      }
    }

    // Spawn Items
    spawnItems(this.level);

    // Wire player reference for teleportation on combat start
    if (this.level.getRoomManager() != null && !players.isEmpty()) {
      this.level.getRoomManager().setPlayers(players);
    }

    GameManager.getInstance().setCurrentLevel(this.level);
  }

  /** Finds a valid spawn point ("floor" tile) prioritizing start rooms over portal rooms. */
  private Vector2 findFirstFloorSpawn(TiledMap map, List<RoomData> roomData) {
    // 1. Smart Search: If we have room data, pick the first room (Start Room)
    if (roomData != null && !roomData.isEmpty()) {
      for (RoomData room : roomData) {
        if (!room.isPortalRoom()) {
          Vector2 spawn = findSpawnInRoom(map, room);
          if (spawn != null) return spawn;
        }
      }
    }

    // 2. Fallback: Naive bottom-up scan (for caves or legacy maps)
    if (map == null || map.getLayers().getCount() == 0) {
      return new Vector2(17, 17);
    }

    TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
    int w = layer.getWidth();
    int h = layer.getHeight();
    float tileSize = layer.getTileWidth();

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        if (cell == null || cell.getTile() == null) continue;

        var props = cell.getTile().getProperties();
        boolean isFloor;
        if (props.containsKey("type")) {
          isFloor = "floor".equals(props.get("type", String.class));
        } else {
          isFloor = !props.get("isWall", false, Boolean.class);
        }

        if (isFloor) {
          float px = x * tileSize + tileSize / 2f;
          float py = y * tileSize + tileSize / 2f;
          return new Vector2(px, py);
        }
      }
    }

    return new Vector2(17, 17);
  }

  private Vector2 findSpawnInRoom(TiledMap map, RoomData room) {
    TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
    float tileSize = layer.getTileWidth();

    // Convert world bounds to tile coordinates
    int startX = (int) (room.bounds().x / tileSize);
    int startY = (int) (room.bounds().y / tileSize);
    int endX = (int) ((room.bounds().x + room.bounds().width) / tileSize);
    int endY = (int) ((room.bounds().y + room.bounds().height) / tileSize);

    for (int y = startY; y < endY; y++) {
      for (int x = startX; x < endX; x++) {
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        if (cell != null && cell.getTile() != null) {
          String type = cell.getTile().getProperties().get("type", String.class);
          if ("floor".equals(type)) {
            float px = x * tileSize + tileSize / 2f;
            float py = y * tileSize + tileSize / 2f;
            return new Vector2(px, py);
          }
        }
      }
    }
    return null;
  }

  private void spawnItems(Level level) {
    if (level.getRoomManager() == null) {
      com.badlogic.gdx.Gdx.app.log("GameModel", "spawnItems: RoomManager is null");
      return;
    }

    int totalSpawned = 0;
    for (io.github.soulslight.model.room.Room room : level.getRoomManager().getRooms()) {
      for (int i = 0; i < 2; i++) {
        Vector2 pos = getRandomFloorPosition(level.getMap(), room);
        if (pos != null) {
          ItemEntity item =
              (ItemEntity)
                  itemCreator.createEntity(this.physicsWorld, pos.x, pos.y, new HealthPotion());
          level.addItem(item);
          totalSpawned++;
          com.badlogic.gdx.Gdx.app.log("GameModel", "Spawned Item at " + pos);
        } else {
          com.badlogic.gdx.Gdx.app.log(
              "GameModel", "Failed to find floor for item in room " + room.getId());
        }
      }
    }
    com.badlogic.gdx.Gdx.app.log("GameModel", "Total items spawned: " + totalSpawned);
  }

  private Vector2 getRandomFloorPosition(TiledMap map, io.github.soulslight.model.room.Room room) {
    // Check layer name "Ground" or index 0
    TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("Ground");
    if (layer == null) {
      // Fallback to index 0 if "Ground" not found
      if (map.getLayers().getCount() > 0) {
        layer = (TiledMapTileLayer) map.getLayers().get(0);
      } else {
        com.badlogic.gdx.Gdx.app.log("GameModel", "No layers found in map!");
        return null;
      }
    }

    float startX = room.getBounds().x;
    float startY = room.getBounds().y;
    float width = room.getBounds().width;
    float height = room.getBounds().height;

    // Try 10 times to find a valid floor
    for (int k = 0; k < 10; k++) {
      float randX = startX + MathUtils.random(2, width - 2);
      float randY = startY + MathUtils.random(2, height - 2);

      int cellX = (int) (randX / Constants.PPM);
      int cellY = (int) (randY / Constants.PPM);

      if (cellX >= 0 && cellX < layer.getWidth() && cellY >= 0 && cellY < layer.getHeight()) {
        // Assuming all tiles in Ground layer inside room are walkable
        // Ideally check for walls but walls shouldn't be in room interior generally
        return new Vector2(randX, randY);
      }
    }
    return null;
  }

  public void update(float deltaTime) {
    if (isPaused) return;

    for (Player p : players) {
      if (p != null) p.update(deltaTime);
    }

    // Revive Logic
    for (Player activePlayer : players) {
      if (activePlayer == null || activePlayer.isDead()) continue;

      // Check if player is still
      float velSq =
          activePlayer.getBody() != null ? activePlayer.getBody().getLinearVelocity().len2() : 0f;
      // Increased tolerance for "stillness"
      boolean isStill = activePlayer.getBody() != null && velSq < 5.0f;

      boolean revivingSomeone = false;

      if (isStill) {
        for (Player deadPlayer : players) {
          if (deadPlayer == null || !deadPlayer.isDead()) continue;

          float dist = activePlayer.getPosition().dst(deadPlayer.getPosition());
          // com.badlogic.gdx.Gdx.app.log("ReviveDebug", "Dist to dead: " + dist);

          // Check overlap (assuming radius ~14f, combined ~28f, use 40f for tolerance)
          if (dist < 40f) {
            revivingSomeone = true;
            activePlayer.setReviveAttemptTimer(activePlayer.getReviveAttemptTimer() + deltaTime);

            // Log every integer second to verify progress
            if ((int) activePlayer.getReviveAttemptTimer()
                > (int) (activePlayer.getReviveAttemptTimer() - deltaTime)) {
              com.badlogic.gdx.Gdx.app.log(
                  "ReviveDebug", "Reviving... Timer: " + activePlayer.getReviveAttemptTimer());
            }

            if (activePlayer.getReviveAttemptTimer() >= 5.0f) {
              deadPlayer.revive();
              activePlayer.setReviveAttemptTimer(0f);
              com.badlogic.gdx.Gdx.app.log("GameModel", "Player revived!");
            }
            break; // Only revive one at a time
          }
        }
      }

      if (!revivingSomeone) {
        activePlayer.setReviveAttemptTimer(0f);
      }
    }

    updateEnemiesLogic(deltaTime);

    physicsAccumulator += deltaTime;

    while (physicsAccumulator >= 1 / 60f) {
      physicsWorld.step(1 / 60f, 6, 2);
      // Update projectiles for all players
      if (!players.isEmpty()) {
        projectileManager.update(1 / 60f, players, getActiveEnemies());
      }
      physicsAccumulator -= 1 / 60f;
    }

    cleanDeadEnemies();
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
    if (!players.isEmpty() && getMap() != null) {
      Player p = players.get(0); // Follow first player for now
      TiledMapTileLayer layer = (TiledMapTileLayer) getMap().getLayers().get(0);
      float tileSize = layer.getTileWidth();
      lightingSystem.update(p.getPosition().x, p.getPosition().y, tileSize);
    }
  }

  private void updateEnemiesLogic(float deltaTime) {
    if (level == null || level.getEnemies() == null) return;

    // List<Player> targets = Collections.singletonList(player);

    for (AbstractEnemy enemy : level.getEnemies()) {
      // AbstractEnemy doesn't have update(delta) but Entity does.
      // However AbstractEnemy extends Entity.
      // We should check if update(delta) is enough or if we need to call something
      // else.
      // Entity.update(delta) syncs graphics from physics.
      enemy.update(deltaTime);
      enemy.updateBehavior(players, deltaTime);

      // Kill-Z: Cleanup enemies that fall out of the world (Ghost Enemy Fix)
      if (enemy.getPosition().y < -100f) {
        enemy.takeDamage(Float.MAX_VALUE); // Ensures death listeners are notified
      }

      checkMeleeCollision(enemy);
    }
  }

  @Override
  public void onProjectileRequest(Vector2 origin, Vector2 target, String type, float damage) {
    boolean isPlayerSource = false;
    float speed = 400f;

    // Handle Player Projectile Types
    if ("arrow".equals(type)) { // Rain of Arrows
      isPlayerSource = true;
      speed = 400f;
    } else if ("fast_arrow".equals(type)) { // Archer Base Attack
      isPlayerSource = true;
      speed = 700f; // Faster than default
    } else if ("homing_fireball_target".equals(type) || type.startsWith("homing_fireball_target")) {
      isPlayerSource = true;
    } else if ("enemy_arrow".equals(type)) {
      isPlayerSource = false; // Explicitly enemy source
      speed = 300f; // Slower than player arrows to be dodgeable
    }

    projectileManager.addProjectile(
        (Projectile)
            projectileCreator.createEntity(
                this.physicsWorld,
                origin.x,
                origin.y,
                target,
                isPlayerSource,
                (io.github.soulslight.model.entities.Entity) null,
                speed,
                damage));
  }

  @Override
  public void onProjectileRequest(
      Vector2 origin,
      io.github.soulslight.model.entities.Entity targetEntity,
      String type,
      float damage) {

    projectileManager.addProjectile(
        (Projectile)
            projectileCreator.createEntity(
                this.physicsWorld,
                origin.x,
                origin.y,
                targetEntity.getPosition(),
                true, // isPlayerSource
                targetEntity,
                400f,
                damage)); // Default speed for homing
  }

  private void checkMeleeCollision(AbstractEnemy enemy) {
    // SpikedBall logic is handled via Box2D walls or specific logic usually,
    // but if it's an enemy in the list, we might want it here too.
    // However, the original code excluded Ranger.
    if (enemy instanceof Ranger || enemy.isDead()) return;

    for (Player player : players) {
      if (player.isDead() || player.isInvincible()) continue;

      float dist = player.getPosition().dst(enemy.getPosition());
      // Oblivion (Boss) has larger hitbox
      float contactThreshold = (enemy instanceof Oblivion) ? 50f : 20f;

      if (dist < contactThreshold) {
        // 1. Apply Damage
        float damage =
            (enemy.getAttackStrategy() != null) ? enemy.getAttackStrategy().getDamage() : 10f;

        player.takeDamage(damage);

        // 2. Apply Knockback
        Vector2 bounceDir = player.getPosition().cpy().sub(enemy.getPosition()).nor();

        // Prevent zero-vector if exactly on top
        if (bounceDir.len2() < 0.01f) bounceDir.set(1, 0);

        if (player.getBody() != null) {
          // Force: 600f (strong enough to push out of overlap)
          // Duration: 0.2s
          player.applyKnockback(bounceDir, 600f, 0.2f);
        }
      }
    }
  }

  private void cleanDeadEnemies() {
    if (level == null || level.getEnemies() == null) return;

    Iterator<AbstractEnemy> it = level.getEnemies().iterator();
    while (it.hasNext()) {
      AbstractEnemy e = it.next();
      if (e.isDead()) {
        // Feature: Boss Death triggers level completion
        if (e instanceof Oblivion && ((Oblivion) e).isPhaseTwo()) {
          this.levelCompleted = true;
        }

        e.destroyBody(physicsWorld);
        totalEnemiesKilled++;
        it.remove();
      }
    }
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
    List<PlayerMemento> playerStates = new java.util.ArrayList<>();
    for (Player p : players) {
      playerStates.add(
          new PlayerMemento(p.getType(), p.getHealth(), p.getPosition().x, p.getPosition().y));
    }

    List<EnemyMemento> enemyStates = new java.util.ArrayList<>();
    if (level != null && level.getEnemies() != null) {
      for (AbstractEnemy e : level.getEnemies()) {
        if (!e.isDead()) {
          enemyStates.add(
              new EnemyMemento(
                  getEnemyType(e), e.getPosition().x, e.getPosition().y, e.getHealth()));
        }
      }
    }

    List<ProjectileMemento> projectileStates = new java.util.ArrayList<>();
    for (Projectile p : projectileManager.getProjectiles()) {
      if (!p.shouldDestroy()) {
        Vector2 vel = p.getBody().getLinearVelocity();
        projectileStates.add(
            new ProjectileMemento(p.getPosition().x, p.getPosition().y, vel.x, vel.y));
      }
    }

    // --- NEW: SAVE MAP STATE ---
    List<RoomMemento> roomStates = new java.util.ArrayList<>();
    List<DoorMemento> doorStates = new java.util.ArrayList<>();
    List<PortalMemento> portalStates = new java.util.ArrayList<>();

    if (level != null && level.getRoomManager() != null) {
      for (io.github.soulslight.model.room.Room r : level.getRoomManager().getRooms()) {
        roomStates.add(new RoomMemento(r.getId(), r.isCleared(), r.areDoorsLocked()));

        // Save doors (flattened list, relying on deterministic order from generation)
        for (io.github.soulslight.model.room.Door d : r.getDoors()) {
          doorStates.add(new DoorMemento(doorStates.size(), d.isLocked()));
        }
      }

      // Save PortalRoom state
      io.github.soulslight.model.room.PortalRoom pr = level.getRoomManager().getPortalRoom();
      if (pr != null && pr.getPortal() != null) {
        portalStates.add(new PortalMemento(pr.getPortal().isActivated()));
      }
    }

    // Cave Portal (if exists)
    if (level != null && level.getCavePortal() != null) {
      portalStates.add(new PortalMemento(level.getCavePortal().isActivated()));
    }

    // Fix: Save actual current level index instead of hardcoded 1
    // Fix: Save actual campaign seed from GameManager
    return new GameStateMemento(
        playerStates,
        enemyStates,
        projectileStates,
        roomStates,
        doorStates,
        portalStates,
        GameManager.getInstance().getCampaignSeed(),
        GameManager.getInstance().getCurrentLevelIndex());
  }

  private String getEnemyType(AbstractEnemy e) {
    if (e instanceof Chaser) return "Chaser";
    if (e instanceof Ranger) return "Ranger";
    if (e instanceof Shielder) return "Shielder";
    if (e instanceof SpikedBall) return "SpikedBall";
    if (e instanceof Oblivion) return "Oblivion";
    return "Chaser"; // Fallback
  }

  public void restoreMemento(GameStateMemento memento) {
    if (memento == null || memento.players == null) return;

    // Dispose Level first to clean up Managers (RoomManager destroys sensors)
    // allowing them to remove bodies safely before we wipe the world.
    if (level != null) level.dispose();

    // Clear logical lists
    players.clear();
    GameManager.getInstance().clearPlayers();
    this.projectileManager.getProjectiles().clear();

    com.badlogic.gdx.utils.Array<com.badlogic.gdx.physics.box2d.Body> bodies =
        new com.badlogic.gdx.utils.Array<>();
    physicsWorld.getBodies(bodies);
    for (com.badlogic.gdx.physics.box2d.Body b : bodies) {
      physicsWorld.destroyBody(b);
    }

    // Restore Seed to GameManager so map generation is consistent!
    GameManager.getInstance().setCampaignSeed(memento.seed);
    this.currentSeed = memento.seed;

    // Fix: Restore Level Index BEFORE generating map
    GameManager.getInstance().setCurrentLevelIndex(memento.currentLevelIndex);

    // Rebuild Map (using level-based strategy)
    MapGenerationStrategy strategy = GameManager.getInstance().getCurrentLevelStrategy();
    TiledMap newMap = strategy.generate();
    this.lightingSystem.prepareLightingOverlay(newMap);

    // Extract room data for reconstruction
    List<RoomData> roomData = DungeonMapStrategy.extractRoomData(newMap);
    boolean hasCavePortal =
        newMap.getProperties().containsKey(NoiseMapStrategy.PORTAL_POSITION_KEY);

    // GoF Builder: Create Director and Builder
    StandardLevelBuilder builder = new StandardLevelBuilder();
    LevelDirector director = new LevelDirector(builder);

    // Conditional setup based on level type
    GameLevelFactory levelFactory;
    if (!roomData.isEmpty()) {
      levelFactory = new DungeonLevelFactory();
      this.level =
          director.constructDungeonLevelRestored(newMap, roomData, levelFactory, this.physicsWorld);

    } else if (hasCavePortal) {
      levelFactory = new CaveLevelFactory();
      LevelFactory.EnemyConfig config =
          LevelFactory.getEnemyConfig(
              GameManager.getInstance().getCurrentLevelIndex(),
              GameManager.getInstance().getGameMode());

      this.level =
          director.constructCaveLevelRestored(newMap, levelFactory, this.physicsWorld, config);

    } else {
      levelFactory = new BossLevelFactory();
      LevelFactory.EnemyConfig config =
          LevelFactory.getEnemyConfig(
              GameManager.getInstance().getCurrentLevelIndex(),
              GameManager.getInstance().getGameMode());

      this.level =
          director.constructBossLevelRestored(newMap, levelFactory, this.physicsWorld, config);
    }

    this.level = builder.build();
    GameManager.getInstance().setCurrentLevel(this.level);

    // 2. Recreate players from Memento
    for (PlayerMemento pm : memento.players) {
      Vector2 savedPos = new Vector2(pm.x, pm.y);
      Vector2 safePos = getSafeSpawnPosition(savedPos);

      Player newPlayer =
          (Player) playerCreator.createEntity(this.physicsWorld, safePos.x, safePos.y, pm.type);
      newPlayer.addProjectileListener(this); // Register listener
      newPlayer.setHealth(pm.health);

      players.add(newPlayer);
      GameManager.getInstance().addPlayer(newPlayer);
    }

    // Update RoomManager with restored players so states can detect them
    level.getRoomManager().setPlayers(players);

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

    // Recreate Projectiles
    if (memento.projectiles != null) {
      for (ProjectileMemento pm : memento.projectiles) {
        // Workaround: Create with dummy target, then override velocity.
        Vector2 dummyTarget = new Vector2(pm.x + pm.vx, pm.y + pm.vy);
        Projectile p =
            (Projectile)
                projectileCreator.createEntity(
                    this.physicsWorld,
                    pm.x,
                    pm.y,
                    dummyTarget,
                    false,
                    (io.github.soulslight.model.entities.Entity) null,
                    0f,
                    0f);
        p.getBody().setLinearVelocity(pm.vx, pm.vy);
        this.projectileManager.addProjectile(p);
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
    return players;
  }

  // Backward compatibility
  public Player getPlayer() {
    return players.isEmpty() ? null : players.get(0);
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
      // Boss Killed!
      setLevelCompleted(true);
    }
    // Could track total kills here too if needed
    // totalEnemiesKilled++; // Already handled in update loop? Use this instead?
    // Current update loop removes dead enemies and increments counter.
    // Listener is cleaner, but let's stick to Boss logic for now to avoid double
    // counting
    // if we don't refactor the update loop.
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
    return totalEnemiesKilled;
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

  /** Checks if a position is safe (floor) and returns it. If not, searches for nearest floor. */
  private Vector2 getSafeSpawnPosition(Vector2 pos) {
    if (isFloorTile(pos)) return pos;

    // If inside wall, search outwards (Spiral/Grid Search)
    // Radius in TILES
    int searchRadius = 3;

    if (level == null || level.getMap() == null) return pos;
    TiledMapTileLayer layer = (TiledMapTileLayer) level.getMap().getLayers().get(0);
    float tileSize = layer.getTileWidth();
    int cx = (int) (pos.x / tileSize);
    int cy = (int) (pos.y / tileSize);

    for (int r = 1; r <= searchRadius; r++) {
      for (int y = cy - r; y <= cy + r; y++) {
        for (int x = cx - r; x <= cx + r; x++) {
          Vector2 candidate =
              new Vector2(x * tileSize + tileSize / 2f, y * tileSize + tileSize / 2f);
          if (isFloorTile(candidate)) {
            com.badlogic.gdx.Gdx.app.log(
                "SafeSpawn", "Moved player from " + pos + " to " + candidate);
            return candidate;
          }
        }
      }
    }

    return pos; // Fallback
  }

  private boolean isFloorTile(Vector2 pos) {
    if (level == null || level.getMap() == null) return true;
    TiledMapTileLayer layer = (TiledMapTileLayer) level.getMap().getLayers().get(0);
    if (layer == null) return true;

    int x = (int) (pos.x / layer.getTileWidth());
    int y = (int) (pos.y / layer.getTileHeight());

    if (x < 0 || x >= layer.getWidth() || y < 0 || y >= layer.getHeight()) return false;

    TiledMapTileLayer.Cell cell = layer.getCell(x, y);
    // Null cell usually means void, which is arguably NOT floor. Depends on map.
    // Assuming void is unsafe.
    if (cell == null || cell.getTile() == null) return false;

    var props = cell.getTile().getProperties();
    if (props.containsKey("type")) {
      return "floor".equals(props.get("type", String.class));
    }
    return !props.get("isWall", false, Boolean.class);
  }
}
