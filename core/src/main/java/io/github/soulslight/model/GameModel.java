package io.github.soulslight.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.manager.ProjectileManager;
import io.github.soulslight.model.combat.ProjectileListener;
import io.github.soulslight.model.enemies.*;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.entities.Projectile;
import io.github.soulslight.model.map.*;
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

    private float physicsAccumulator = 0;
    private boolean levelCompleted = false;

    private final ProjectileManager projectileManager;

    public GameModel() {
        EnemyRegistry.loadCache(null);
        this.physicsWorld = new World(new Vector2(0, 0), true);
        this.physicsWorld.setContactListener(new GameContactListener());

        this.currentWill = MAX_WILL / 2;
        this.isPaused = false;
        this.players = new java.util.ArrayList<>();

        // Initial load
        loadLevelFromStrategy();

        // Projectile Manager
        this.projectileManager = new ProjectileManager(physicsWorld);
    }

    // Refactored initialization logic to reuse in restoreMemento
    private void loadLevelFromStrategy() {
        this.currentSeed = System.currentTimeMillis();
        MapGenerationStrategy strategy = GameManager.getInstance().getCurrentLevelStrategy();
        TiledMap myMap = strategy.generate();

        setupEntitiesAndLevel(myMap);
    }

    private void setupEntitiesAndLevel(TiledMap myMap) {
        players.clear();
        GameManager.getInstance().clearPlayers();

        Vector2 spawn = findFirstFloorSpawn(myMap);

        Player.PlayerClass selectedClass = GameManager.getInstance().getSelectedPlayerClass();
        Player p1 = new Player(selectedClass, this.physicsWorld, spawn.x, spawn.y);
        players.add(p1);
        GameManager.getInstance().addPlayer(p1);

        Player p2 = new Player(Player.PlayerClass.ARCHER, this.physicsWorld, spawn.x + 20, spawn.y);
        players.add(p2);
        GameManager.getInstance().addPlayer(p2);

        EnemyFactory factory = new DungeonEnemyFactory();
        List<RoomData> roomData = DungeonMapStrategy.extractRoomData(myMap);
        boolean hasCavePortal = myMap.getProperties().containsKey(NoiseMapStrategy.PORTAL_POSITION_KEY);

        if (!roomData.isEmpty()) {
            this.level = new LevelBuilder()
                .buildMap(myMap)
                .buildRooms(roomData)
                .initializeRoomManager(this.physicsWorld)
                .buildPhysicsFromMap(this.physicsWorld)
                .spawnEnemiesInRooms(factory, this.physicsWorld)
                .setEnvironment("dungeon_theme.mp3", 0.3f)
                .build();
        } else if (hasCavePortal) {
            LevelFactory.EnemyConfig config = LevelFactory.getEnemyConfig(
                GameManager.getInstance().getCurrentLevelIndex(), GameManager.getInstance().getGameMode());
            this.level = new LevelBuilder()
                .buildMap(myMap)
                .buildPhysicsFromMap(this.physicsWorld)
                .spawnRandom(factory, this.physicsWorld, config.melee(), config.ranged(), config.tank(), config.ball(), config.spawnBoss())
                .spawnCavePortal(this.physicsWorld)
                .setEnvironment("cave_theme.mp3", 0.2f)
                .build();
        } else {
            LevelFactory.EnemyConfig config = LevelFactory.getEnemyConfig(
                GameManager.getInstance().getCurrentLevelIndex(), GameManager.getInstance().getGameMode());
            this.level = new LevelBuilder()
                .buildMap(myMap)
                .buildPhysicsFromMap(this.physicsWorld)
                .spawnRandom(factory, this.physicsWorld, config.melee(), config.ranged(), config.tank(), config.ball(), config.spawnBoss())
                .setEnvironment("boss_theme.mp3", 0.1f)
                .build();
        }

        if (this.level.getEnemies() != null) {
            for (AbstractEnemy e : this.level.getEnemies()) {
                e.addProjectileListener(this);
                if (e instanceof Shielder) {
                    ((Shielder) e).setAllies(this.level.getEnemies());
                }
            }
        }

        if (this.level.getRoomManager() != null && !players.isEmpty()) {
            this.level.getRoomManager().setPlayers(players);
        }

        GameManager.getInstance().setCurrentLevel(this.level);
    }

    private Vector2 findFirstFloorSpawn(TiledMap map) {
        if (map == null || map.getLayers().getCount() == 0) return new Vector2(17, 17);
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        for (int y = 0; y < layer.getHeight(); y++) {
            for (int x = 0; x < layer.getWidth(); x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null && cell.getTile() != null) {
                    String type = cell.getTile().getProperties().get("type", String.class);
                    if ("floor".equals(type)) {
                        return new Vector2(x * layer.getTileWidth() + layer.getTileWidth()/2f, y * layer.getTileHeight() + layer.getTileHeight()/2f);
                    }
                }
            }
        }
        return new Vector2(17, 17);
    }

    public void update(float deltaTime) {
        if (isPaused) return;

        for (Player p : players) if (p != null) p.update(deltaTime);

        updateEnemiesLogic(deltaTime);

        physicsAccumulator += deltaTime;
        while (physicsAccumulator >= 1 / 60f) {
            physicsWorld.step(1 / 60f, 6, 2);
            if (!players.isEmpty()) projectileManager.update(1 / 60f, players);
            physicsAccumulator -= 1 / 60f;
        }

        cleanDeadEnemies();

        if (level != null && level.getRoomManager() != null) {
            level.getRoomManager().update(deltaTime);
        }
    }

    private void updateEnemiesLogic(float deltaTime) {
        if (level == null || level.getEnemies() == null) return;
        for (AbstractEnemy enemy : level.getEnemies()) {
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
            float threshold = (enemy instanceof Oblivion) ? 50f : 32f;
            if (dist < threshold) {
                if (enemy instanceof Shielder && player.getBody() != null) {
                    Vector2 bounce = player.getPosition().cpy().sub(enemy.getPosition()).nor();
                    player.applyKnockback(bounce, 100f, 0.2f);
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
                // SAFE DESTROY: Check body ref first
                if (e.getBody() != null) {
                    e.destroyBody(physicsWorld);
                }
                it.remove();
            }
        }
    }

    public GameStateMemento createMemento() {
        java.util.List<PlayerMemento> pStates = new java.util.ArrayList<>();
        for (Player p : players) pStates.add(new PlayerMemento(p.getType(), p.getHealth(), p.getX(), p.getY()));

        java.util.List<EnemyMemento> eStates = new java.util.ArrayList<>();
        if (level != null && level.getEnemies() != null) {
            for (AbstractEnemy e : level.getEnemies()) {
                if (!e.isDead()) eStates.add(new EnemyMemento(getEnemyType(e), e.getX(), e.getY(), e.getHealth()));
            }
        }

        java.util.List<ProjectileMemento> projStates = new java.util.ArrayList<>();
        for (Projectile p : projectileManager.getProjectiles()) {
            if (!p.shouldDestroy()) {
                Vector2 v = p.getBody().getLinearVelocity();
                projStates.add(new ProjectileMemento(p.getX(), p.getY(), v.x, v.y));
            }
        }
        return new GameStateMemento(pStates, eStates, projStates, this.currentSeed, 1);
    }

    private String getEnemyType(AbstractEnemy e) {
        return e.getClass().getSimpleName(); // Semplificazione: usa il nome della classe che matcha il registry
    }

    public void restoreMemento(GameStateMemento memento) {
        if (memento == null || memento.players == null) return;

        if (level != null) {
            level.dispose();
            level = null;
        }

        players.clear();
        GameManager.getInstance().clearPlayers();
        projectileManager.getProjectiles().clear();

        if (physicsWorld != null && !physicsWorld.isLocked()) {
            Array<Body> bodies = new Array<>();
            physicsWorld.getBodies(bodies);
            for (Body b : bodies) {

                physicsWorld.destroyBody(b);
            }
        } else {
            Gdx.app.error("GameModel", "Cannot restore memento: World is locked or null!");
            return;
        }


        // Restore Seed
        this.currentSeed = memento.seed;

        // Rebuild Map
        MapGenerationStrategy strategy = GameManager.getInstance().getCurrentLevelStrategy();
        TiledMap newMap = strategy.generate();

        // Rebuild Level (Base)
        this.level = new LevelBuilder()
            .buildMap(newMap)
            .buildPhysicsFromMap(this.physicsWorld) // Ricostruisce muri statici
            .setEnvironment("dungeon_theme.mp3", 0.3f)
            .build();
        GameManager.getInstance().setCurrentLevel(this.level);

        // Restore Players
        for (PlayerMemento pm : memento.players) {
            Player np = new Player(pm.type, physicsWorld, pm.x, pm.y);
            np.setHealth(pm.health);
            players.add(np);
            GameManager.getInstance().addPlayer(np);
        }

        // Restore Enemies
        if (memento.enemies != null) {
            for (EnemyMemento em : memento.enemies) {
                AbstractEnemy enemy = EnemyRegistry.getEnemy(em.type); // Usa "Chaser", "Ranger" etc
                if (enemy != null) {
                    enemy.createBody(physicsWorld, em.x, em.y);
                    enemy.setHealth(em.health);
                    this.level.addEnemy(enemy);
                }
            }
            // Relink Shielder
            for (AbstractEnemy e : this.level.getEnemies()) {
                e.addProjectileListener(this);
                if (e instanceof Shielder) ((Shielder) e).setAllies(this.level.getEnemies());
            }
        }

        // Restore Projectiles
        if (memento.projectiles != null) {
            for (ProjectileMemento pm : memento.projectiles) {
                Projectile p = new Projectile(physicsWorld, pm.x, pm.y, new Vector2(pm.x + pm.vx, pm.y + pm.vy));
                p.getBody().setLinearVelocity(pm.vx, pm.vy);
                this.projectileManager.addProjectile(p);
            }
        }
    }

    public List<Projectile> getProjectiles() { return projectileManager.getProjectiles(); }
    public World getWorld() { return physicsWorld; }
    public java.util.List<Player> getPlayers() { return players; }
    public Player getPlayer() { return players.isEmpty() ? null : players.get(0); }
    public Level getLevel() { return level; }
    public TiledMap getMap() { return (level != null) ? level.getMap() : null; }
    public List<AbstractEnemy> getActiveEnemies() { return (level != null) ? level.getEnemies() : Collections.emptyList(); }
    public float getCurrentWill() { return currentWill; }
    public void setCurrentWill(float will) { this.currentWill = will; }
    public boolean isPaused() { return isPaused; }
    public void setPaused(boolean paused) { this.isPaused = paused; }
    public boolean isLevelCompleted() { return levelCompleted; }
    public void setLevelCompleted(boolean completed) { this.levelCompleted = completed; }

    @Override
    public void dispose() {
        GameManager.getInstance().cleanUp();

        if (level != null) {
            level.dispose(); // Pulisce i suoi asset
        }

        if (physicsWorld != null) {
            physicsWorld.dispose(); // Distrugge tutto il resto
        }
    }
}
