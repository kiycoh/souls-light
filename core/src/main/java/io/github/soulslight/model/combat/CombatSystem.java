package io.github.soulslight.model.combat;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.manager.ProjectileManager;
import io.github.soulslight.model.Constants;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.enemies.Oblivion;
import io.github.soulslight.model.enemies.Ranger;
import io.github.soulslight.model.entities.Entity;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.entities.Projectile;
import io.github.soulslight.model.factory.EntityCreator;
import io.github.soulslight.model.factory.ProjectileCreator;
import io.github.soulslight.model.map.Level;
import java.util.Iterator;
import java.util.List;

/**
 * Manages combat mechanics: collision detection, damage handling, and enemy cleanup. Extracted from
 * GameModel to satisfy Single Responsibility Principle.
 */
public class CombatSystem implements ProjectileListener {

  private final World physicsWorld;
  private final ProjectileManager projectileManager;
  private final EntityCreator projectileCreator;

  private int totalEnemiesKilled = 0;
  private boolean bossDefeated = false;

  /** Listener for combat events. */
  public interface CombatEventListener {
    void onBossDefeated();

    void onPlayerHit(Player player);
  }

  private CombatEventListener eventListener;

  public CombatSystem(World physicsWorld, ProjectileManager projectileManager) {
    this.physicsWorld = physicsWorld;
    this.projectileManager = projectileManager;
    this.projectileCreator = new ProjectileCreator();
  }

  public void setEventListener(CombatEventListener listener) {
    this.eventListener = listener;
  }

  /**
   * Updates enemy logic and checks for melee collisions.
   *
   * @param level Current game level
   * @param players List of players
   * @param deltaTime Time since last frame
   */
  public void updateEnemies(Level level, List<Player> players, float deltaTime) {
    if (level == null || level.getEnemies() == null) return;

    for (AbstractEnemy enemy : level.getEnemies()) {
      // Skip unspawned enemies (they are inactive)
      if (!enemy.isSpawned()) continue;
      enemy.update(deltaTime);
      enemy.updateBehavior(players, deltaTime);

      // Kill-Z: Cleanup enemies that fall out of the world (Ghost Enemy Fix)
      if (enemy.getPosition().y < -100f) {
        enemy.takeDamage(Float.MAX_VALUE); // Ensures death listeners are notified
      }
      checkMeleeCollision(enemy, players);
    }
  }

  /** Checks for melee collision between an enemy and players. */
  private void checkMeleeCollision(AbstractEnemy enemy, List<Player> players) {
    // Rangers attack with projectiles, not melee
    if (enemy instanceof Ranger || enemy.isDead()) return;

    for (Player player : players) {
      if (player.isDead() || player.isInvincible()) continue;

      float dist = player.getPosition().dst(enemy.getPosition());
      // Oblivion (Boss) has larger hitbox
      float contactThreshold = (enemy instanceof Oblivion) ? 50f : 20f;

      if (dist < contactThreshold) {
        // Apply Damage
        float damage =
            (enemy.getAttackStrategy() != null) ? enemy.getAttackStrategy().getDamage() : 10f;
        player.takeDamage(damage);

        // Apply Knockback
        Vector2 bounceDir = player.getPosition().cpy().sub(enemy.getPosition()).nor();

        // Prevent zero-vector if exactly on top
        if (bounceDir.len2() < 0.01f) bounceDir.set(1, 0);

        if (player.getBody() != null) {
          player.applyKnockback(bounceDir, 600f, 0.2f);
        }
      }
    }
  }

  /**
   * Removes dead enemies from the level and destroys their physics bodies.
   *
   * @param level Current game level
   */
  public void cleanDeadEnemies(Level level) {
    if (level == null || level.getEnemies() == null) return;

    Iterator<AbstractEnemy> it = level.getEnemies().iterator();
    while (it.hasNext()) {
      AbstractEnemy e = it.next();
      if (e.isDead()) {
        // Boss Death triggers level completion
        if (e instanceof Oblivion && ((Oblivion) e).isPhaseTwo()) {
          bossDefeated = true;
          if (eventListener != null) {
            eventListener.onBossDefeated();
          }
        }

        e.destroyBody(physicsWorld);
        totalEnemiesKilled++;
        it.remove();
      }
    }
  }

  /** Handles projectile creation requests from players and enemies. */
  @Override
  public void onProjectileRequest(Vector2 origin, Vector2 target, String type, float damage) {
    boolean isPlayerSource = false;
    float speed = Constants.SPEED_ARROW;

    // Handle Player Projectile Types
    if (Constants.PROJ_ARROW.equals(type)) {
      isPlayerSource = true;
      speed = Constants.SPEED_ARROW;
    } else if (Constants.PROJ_FAST_ARROW.equals(type)) {
      isPlayerSource = true;
      speed = Constants.SPEED_FAST_ARROW;
    } else if ("homing_fireball_target".equals(type)
        || type.startsWith(Constants.PROJ_HOMING_FIREBALL)
        || Constants.PROJ_FIREBALL.equals(type)) {
      isPlayerSource = true;
    } else if (Constants.PROJ_ENEMY_ARROW.equals(type)) {
      isPlayerSource = false;
      speed = Constants.SPEED_ENEMY_ARROW;
    }

    projectileManager.addProjectile(
        (Projectile)
            projectileCreator.createEntity(
                physicsWorld,
                origin.x,
                origin.y,
                target,
                isPlayerSource,
                (Entity) null,
                speed,
                damage,
                type));
  }

  /** Handles homing projectile creation requests. */
  @Override
  public void onProjectileRequest(Vector2 origin, Entity targetEntity, String type, float damage) {
    projectileManager.addProjectile(
        (Projectile)
            projectileCreator.createEntity(
                physicsWorld,
                origin.x,
                origin.y,
                targetEntity.getPosition(),
                true, // isPlayerSource
                targetEntity,
                400f,
                damage,
                type));
  }

  public int getTotalEnemiesKilled() {
    return totalEnemiesKilled;
  }

  public boolean isBossDefeated() {
    return bossDefeated;
  }

  public void resetBossDefeated() {
    bossDefeated = false;
  }

  /**
   * Restores a projectile from memento data during game load.
   *
   * @param x X position
   * @param y Y position
   * @param vx X velocity
   * @param vy Y velocity
   */
  public void restoreProjectile(float x, float y, float vx, float vy) {
    Vector2 dummyTarget = new Vector2(x + vx, y + vy);
    Projectile p =
        (Projectile)
            projectileCreator.createEntity(
                physicsWorld, x, y, dummyTarget, false, (Entity) null, 0f, 0f);
    p.getBody().setLinearVelocity(vx, vy);
    projectileManager.addProjectile(p);
  }
}
