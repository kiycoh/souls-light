package io.github.soulslight.model;

import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
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

    // Accumulatore per il time step fisso
    private float physicsAccumulator = 0;

    private final ProjectileManager projectileManager;

    public GameModel() {
        EnemyRegistry.loadCache(null);
        this.physicsWorld = new World(new Vector2(0, 0), true);


        this.physicsWorld.setContactListener(new GameContactListener());

        this.currentWill = MAX_WILL / 2;
        this.isPaused = false;

        this.player = new Player(Player.PlayerClass.WARRIOR, this.physicsWorld, 17, 17);
        GameManager.getInstance().setPlayer(this.player);

        // Setup Projectile Manager
        this.projectileManager = new ProjectileManager(physicsWorld);

        EnemyFactory factory = new DungeonEnemyFactory();
        TiledMap myMap = new TmxMapLoader().load("maps/MAppa2.tmx");

        //Level Builder
        this.level = new LevelBuilder()
            .buildMap(myMap)
            .buildPhysicsFromMap(this.physicsWorld)
            .spawnFromTiled(factory, this.physicsWorld)
            .setEnvironment("dungeon_theme.mp3", 0.3f)
            .build();

        if (this.level.getEnemies() != null) {
            for (AbstractEnemy e : this.level.getEnemies()) {
                if (e instanceof Shielder) ((Shielder) e).setAllies(this.level.getEnemies());
            }
        }

        GameManager.getInstance().setCurrentLevel(this.level);
    }

    public void update(float deltaTime) {
        if (isPaused) return;

        if (player != null) player.update(deltaTime);
        updateEnemiesLogic(deltaTime);


        physicsAccumulator += deltaTime;

        // Eseguiamo step fissi di 1/60s
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

            // gestione proiettili nemici
            if (enemy instanceof Ranger) {
                Ranger ranger = (Ranger) enemy;
                if (ranger.isReadyToShoot()) {
                    projectileManager.addProjectile(new Projectile(physicsWorld, ranger.getPosition().x, ranger.getPosition().y, player.getPosition()));
                    ranger.resetShot();
                }
            } else if (enemy instanceof Oblivion) {
                Oblivion boss = (Oblivion) enemy;
                if (boss.isReadyToShoot()) {
                    for (Vector2 targetPos : boss.getShotTargets()) {
                        projectileManager.addProjectile(new Projectile(physicsWorld, boss.getPosition().x, boss.getPosition().y, targetPos));
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
            // Logica Shielder
            if (enemy instanceof Shielder) {
                Vector2 bounceDir = player.getPosition().cpy().sub(enemy.getPosition()).nor();
                if (bounceDir.len2() < 0.01f) bounceDir.set(1, 0);
                if (player.getBody() != null) {
                    player.applyKnockback(bounceDir, 100f, 0.2f);
                }
            }
        }
    }

    // pulisce la mappa dai cadaveri
    private void cleanDeadEnemies() {
        if (level != null && level.getEnemies() != null) {
            Iterator<AbstractEnemy> iterEnemies = level.getEnemies().iterator();
            while (iterEnemies.hasNext()) {
                AbstractEnemy e = iterEnemies.next();
                if (e.isDead()) {
                    e.destroyBody(physicsWorld);
                    iterEnemies.remove();
                }
            }
        }
    }

    //Ricarica il salvataggio
    public void restoreMemento(GameStateMemento memento) {
        if (memento == null) return;

        // Ripristina la VITA
        if (this.player != null) {
            // Assicurati che Player abbia un metodo setHealth(float h)
            this.player.setHealth(memento.health);

        }

        // Ripristina Posizione Fisica
        if (this.player.getBody() != null) {
            this.player.getBody().setTransform(memento.playerX, memento.playerY, 0);
            this.player.getBody().setLinearVelocity(0, 0);
            this.player.getBody().setAwake(true);
        }

        System.out.println("CARICATO: HP=" + memento.health + " Pos=(" + memento.playerX + "," + memento.playerY + ")");
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
