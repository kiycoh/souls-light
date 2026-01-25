package io.github.soulslight.model;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.manager.ProjectileManager;
import io.github.soulslight.model.combat.ProjectileListener;
import io.github.soulslight.model.enemies.*;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.entities.Projectile;
import io.github.soulslight.model.map.DungeonMapStrategy;
import io.github.soulslight.model.map.Level;
import io.github.soulslight.model.map.LevelBuilder;
import io.github.soulslight.model.map.LevelFactory;
import io.github.soulslight.model.map.MapGenerationStrategy;
import io.github.soulslight.model.map.NoiseMapStrategy;
import io.github.soulslight.model.physics.GameContactListener;
import io.github.soulslight.model.room.RoomData;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GameModel implements Disposable, ProjectileListener {

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

  private final ProjectileManager projectileManager;

  public GameModel() {
    EnemyRegistry.loadCache(null);
    this.physicsWorld = new World(new Vector2(0, 0), true);
    this.physicsWorld.setContactListener(new GameContactListener());

    this.currentWill = MAX_WILL / 2;
    this.isPaused = false;
    this.players = new java.util.ArrayList<>();

    // ---- PROCEDURALLY GENERATED MAP (Level-Based Strategy) ----
    this.currentSeed = System.currentTimeMillis();
    MapGenerationStrategy strategy = GameManager.getInstance().getCurrentLevelStrategy();
    TiledMap myMap = strategy.generate();

    // ---- PLAYERS: spawn on valid flood tile ----
    Vector2 spawn = findFirstFloorSpawn(myMap);

    // Player 1: Uses class selected in ClassSelectionScreen
    Player.PlayerClass selectedClass = GameManager.getInstance().getSelectedPlayerClass();
    Player p1 = new Player(selectedClass, this.physicsWorld, spawn.x, spawn.y);
    players.add(p1);
    GameManager.getInstance().addPlayer(p1);

    // Player 2 (spawn slightly offset) - for co-op testing
    Player p2 = new Player(Player.PlayerClass.ARCHER, this.physicsWorld, spawn.x + 20, spawn.y);
    players.add(p2);
    GameManager.getInstance().addPlayer(p2);

    // Projectile Manager
    this.projectileManager = new ProjectileManager(physicsWorld);

    EnemyFactory factory = new DungeonEnemyFactory();

    // ---- MAP TYPE DETECTION: Dungeon (rooms) vs Cave (roomless) ----
    List<RoomData> roomData = DungeonMapStrategy.extractRoomData(myMap);
    boolean hasCavePortal = myMap.getProperties().containsKey(NoiseMapStrategy.PORTAL_POSITION_KEY);

    if (!roomData.isEmpty()) {
      // ---- DUNGEON-STYLE LEVEL (rooms + doors + portal room) ----
      this.level =
          new LevelBuilder()
              .buildMap(myMap)
              .buildRooms(roomData)
              .initializeRoomManager(this.physicsWorld)
              .buildPhysicsFromMap(this.physicsWorld)
              .spawnEnemiesInRooms(factory, this.physicsWorld)
              .setEnvironment("dungeon_theme.mp3", 0.3f)
              .build();
    } else if (hasCavePortal) {
      // ---- CAVE-STYLE LEVEL (random spawn + cave portal) ----
      LevelFactory.EnemyConfig config =
          LevelFactory.getEnemyConfig(
              GameManager.getInstance().getCurrentLevelIndex(),
              GameManager.getInstance().getGameMode());
      this.level =
          new LevelBuilder()
              .buildMap(myMap)
              .buildPhysicsFromMap(this.physicsWorld)
              .spawnRandom(
                  factory,
                  this.physicsWorld,
                  config.melee(),
                  config.ranged(),
                  config.tank(),
                  config.ball(),
                  config.spawnBoss())
              .spawnCavePortal(this.physicsWorld)
              .setEnvironment("cave_theme.mp3", 0.2f)
              .build();
    } else {
      // ---- BOSS ARENA OR CUSTOM (minimal setup) ----
      LevelFactory.EnemyConfig config =
          LevelFactory.getEnemyConfig(
              GameManager.getInstance().getCurrentLevelIndex(),
              GameManager.getInstance().getGameMode());
      this.level =
          new LevelBuilder()
              .buildMap(myMap)
              .buildPhysicsFromMap(this.physicsWorld)
              .spawnRandom(
                  factory,
                  this.physicsWorld,
                  config.melee(),
                  config.ranged(),
                  config.tank(),
                  config.ball(),
                  config.spawnBoss())
              .setEnvironment("boss_theme.mp3", 0.1f)
              .build();
    }

    // Shielder 'target' setup and Listener registration
    if (this.level.getEnemies() != null) {
      for (AbstractEnemy e : this.level.getEnemies()) {
        e.addProjectileListener(this); // Register listener
        if (e instanceof Shielder) {
          ((Shielder) e).setAllies(this.level.getEnemies());
        }
      }
    }

    // Wire player reference for teleportation on combat start
    if (this.level.getRoomManager() != null && !players.isEmpty()) {
      this.level.getRoomManager().setPlayers(players);
    }

    GameManager.getInstance().setCurrentLevel(this.level);
  }

  /** Finds a valid spawn point ("floor" tile) and returns pixel coordinates */
  private Vector2 findFirstFloorSpawn(TiledMap map) {
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

  public void update(float deltaTime) {
    if (isPaused) return;

    for (Player p : players) {
      if (p != null) p.update(deltaTime);
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

    // Update room states (lock/unlock, clear checks)
    if (level != null && level.getRoomManager() != null) {
      level.getRoomManager().update(deltaTime);
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

      checkMeleeCollision(enemy);
    }
  }

  @Override
  public void onProjectileRequest(Vector2 origin, Vector2 target, String type) {
    projectileManager.addProjectile(new Projectile(physicsWorld, origin.x, origin.y, target));
  }

  private void checkMeleeCollision(AbstractEnemy enemy) {
    if (enemy instanceof Ranger || enemy.isDead()) return;

    for (Player player : players) {
      float dist = player.getPosition().dst(enemy.getPosition());
      float contactThreshold = (enemy instanceof Oblivion) ? 50f : 32f;

      if (dist < contactThreshold) {
        if (enemy instanceof Shielder) {
          Vector2 bounceDir = player.getPosition().cpy().sub(enemy.getPosition()).nor();

          if (bounceDir.len2() < 0.01f) bounceDir.set(1, 0);

          if (player.getBody() != null) {
            player.applyKnockback(bounceDir, 100f, 0.2f);
          }
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
        e.destroyBody(physicsWorld);
        it.remove();
      }
    }
  }

  public GameStateMemento createMemento() {
    java.util.List<PlayerMemento> playerStates = new java.util.ArrayList<>();
    for (Player p : players) {
      playerStates.add(
          new PlayerMemento(p.getType(), p.getHealth(), p.getPosition().x, p.getPosition().y));
    }

    java.util.List<EnemyMemento> enemyStates = new java.util.ArrayList<>();
    if (level != null && level.getEnemies() != null) {
      for (AbstractEnemy e : level.getEnemies()) {
        if (!e.isDead()) {
          enemyStates.add(
              new EnemyMemento(
                  getEnemyType(e), e.getPosition().x, e.getPosition().y, e.getHealth()));
        }
      }
    }

    java.util.List<ProjectileMemento> projectileStates = new java.util.ArrayList<>();
    for (Projectile p : projectileManager.getProjectiles()) {
      if (!p.shouldDestroy()) {
        Vector2 vel = p.getBody().getLinearVelocity();
        projectileStates.add(
            new ProjectileMemento(p.getPosition().x, p.getPosition().y, vel.x, vel.y));
      }
    }

    return new GameStateMemento(playerStates, enemyStates, projectileStates, this.currentSeed, 1);
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

    // Clear EVERYTHING from physics world
    com.badlogic.gdx.utils.Array<com.badlogic.gdx.physics.box2d.Body> bodies =
        new com.badlogic.gdx.utils.Array<>();
    physicsWorld.getBodies(bodies);
    for (com.badlogic.gdx.physics.box2d.Body b : bodies) {
      physicsWorld.destroyBody(b);
    }

    // Clear lists
    players.clear();
    GameManager.getInstance().clearPlayers();

    // Restore Seed
    this.currentSeed = memento.seed;

    // Rebuild Map (using level-based strategy)
    MapGenerationStrategy strategy = GameManager.getInstance().getCurrentLevelStrategy();
    TiledMap newMap = strategy.generate();
    if (level != null) level.dispose();

    // Rebuild Level (No random spawn)
    this.level =
        new LevelBuilder()
            .buildMap(newMap)
            .buildPhysicsFromMap(this.physicsWorld)
            .setEnvironment("dungeon_theme.mp3", 0.3f)
            .build();
    GameManager.getInstance().setCurrentLevel(this.level);

    // 2. Recreate players from Memento
    for (PlayerMemento pm : memento.players) {
      Player newPlayer = new Player(pm.type, physicsWorld, pm.x, pm.y);
      newPlayer.setHealth(pm.health);

      players.add(newPlayer);
      GameManager.getInstance().addPlayer(newPlayer);
    }

    // Recreate Enemies
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
        if (e instanceof Shielder) {
          ((Shielder) e).setAllies(this.level.getEnemies());
        }
      }
    }

    // Recreate Projectiles
    this.projectileManager.getProjectiles().clear();

    if (memento.projectiles != null) {
      for (ProjectileMemento pm : memento.projectiles) {
        // Workaround: Create with dummy target, then override velocity.
        Vector2 dummyTarget = new Vector2(pm.x + pm.vx, pm.y + pm.vy);
        Projectile p = new Projectile(physicsWorld, pm.x, pm.y, dummyTarget);
        p.getBody().setLinearVelocity(pm.vx, pm.vy);
        this.projectileManager.addProjectile(p);
      }
    }
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

  public void setLevelCompleted(boolean completed) {
    this.levelCompleted = completed;
  }

  @Override
  public void dispose() {
    if (physicsWorld != null) physicsWorld.dispose();
    if (level != null) level.dispose();
    GameManager.getInstance().cleanUp();
  }
}
