package io.github.soulslight.model;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.manager.ProjectileManager;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GameModel implements Disposable {

  public static final float MAX_WILL = 100f;
  private final World physicsWorld;
  private float currentWill;
  private boolean isPaused;
  private Player player;
  private Level level;

  // Accumulator for fixed timestep
  private float physicsAccumulator = 0;

  private final ProjectileManager projectileManager;

  public GameModel() {
    EnemyRegistry.loadCache(null);
    this.physicsWorld = new World(new Vector2(0, 0), true);
    this.physicsWorld.setContactListener(new GameContactListener());

    this.currentWill = MAX_WILL / 2;
    this.isPaused = false;

    // ---- PROCEDURALLY GENERATED MAP ----
    long seed = System.currentTimeMillis();
    TiledMap myMap = MapGenerator.generateProceduralMap(seed);

    // ---- PLAYER: spawns on valid flood tile ----
    Vector2 spawn = findFirstFloorSpawn(myMap);
    this.player = new Player(Player.PlayerClass.WARRIOR, this.physicsWorld, spawn.x, spawn.y);
    GameManager.getInstance().setPlayer(this.player);

    // Projectile Manager
    this.projectileManager = new ProjectileManager(physicsWorld);

    EnemyFactory factory = new DungeonEnemyFactory();

    // ---- LEVEL BUILDER (random spawn) ----
    this.level =
        new LevelBuilder()
            .buildMap(myMap)
            .buildPhysicsFromMap(this.physicsWorld)
            .spawnRandom(
                factory,
                this.physicsWorld,
                8, // melee
                4, // ranged
                3, // tank / shielder
                2, // ball / trap
                true // boss
                )
            .setEnvironment("dungeon_theme.mp3", 0.3f)
            .build();

    // Shielder 'target' setup
    if (this.level.getEnemies() != null) {
      for (AbstractEnemy e : this.level.getEnemies()) {
        if (e instanceof Shielder) {
          ((Shielder) e).setAllies(this.level.getEnemies());
        }
      }
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

    if (player != null) player.update(deltaTime);
    updateEnemiesLogic(deltaTime);

    physicsAccumulator += deltaTime;

    while (physicsAccumulator >= 1 / 60f) {
      physicsWorld.step(1 / 60f, 6, 2);
      projectileManager.update(1 / 60f, player);
      physicsAccumulator -= 1 / 60f;
    }

    cleanDeadEnemies();
  }

  private void updateEnemiesLogic(float deltaTime) {
    if (level == null || level.getEnemies() == null) return;

    List<Player> targets = Collections.singletonList(player);

    for (AbstractEnemy enemy : level.getEnemies()) {
      enemy.update(deltaTime);
      enemy.updateBehavior(targets, deltaTime);

      if (enemy instanceof Ranger ranger) {
        if (ranger.isReadyToShoot()) {
          projectileManager.addProjectile(
              new Projectile(
                  physicsWorld,
                  ranger.getPosition().x,
                  ranger.getPosition().y,
                  player.getPosition()));
          ranger.resetShot();
        }
      } else if (enemy instanceof Oblivion boss) {
        if (boss.isReadyToShoot()) {
          for (Vector2 targetPos : boss.getShotTargets()) {
            projectileManager.addProjectile(
                new Projectile(
                    physicsWorld, boss.getPosition().x, boss.getPosition().y, targetPos));
          }
          boss.resetShot();
        }
      }

      checkMeleeCollision(enemy);
    }
  }

  private void checkMeleeCollision(AbstractEnemy enemy) {
    if (enemy instanceof Ranger || enemy.isDead()) return;

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

  public void restoreMemento(GameStateMemento memento) {
    if (memento == null || player == null) return;

    player.setHealth(memento.health);

    if (player.getBody() != null) {
      player.getBody().setTransform(memento.playerX, memento.playerY, 0);
      player.getBody().setLinearVelocity(0, 0);
      player.getBody().setAwake(true);
    }
  }

  public List<Projectile> getProjectiles() {
    return projectileManager.getProjectiles();
  }

  public World getWorld() {
    return physicsWorld;
  }

  public Player getPlayer() {
    return player;
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

  @Override
  public void dispose() {
    if (physicsWorld != null) physicsWorld.dispose();
    if (level != null) level.dispose();
    GameManager.getInstance().cleanUp();
  }
}
