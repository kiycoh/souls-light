package io.github.soulslight.manager;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.physics.box2d.*;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.entities.Projectile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProjectileManager {
  private final World world;
  private final List<Projectile> projectiles = new ArrayList<>();

  public ProjectileManager(World world) {
    this.world = world;
  }

  public void addProjectile(Projectile p) {
    projectiles.add(p);
  }

  // Controls the movement of all projectiles
  public void update(
      float deltaTime,
      List<Player> players,
      List<io.github.soulslight.model.enemies.AbstractEnemy> enemies) {
    Iterator<Projectile> iter = projectiles.iterator();
    while (iter.hasNext()) {
      Projectile p = iter.next();
      p.update(deltaTime);

      float distSq = p.getPosition().dst2(p.getLastPosition());

      if (distSq < 0.0001f) {

        if (p.shouldDestroy()) {
          if (p.getBody() != null) world.destroyBody(p.getBody());
          iter.remove();
        }
        continue;
      }

      final boolean[] hitWall = {false};

      // Serve per usare linee invisibili per intercettare i muri
      world.rayCast(
          (fixture, point, normal, fraction) -> {
            if (fixture.getBody().getType() == BodyDef.BodyType.StaticBody) {
              hitWall[0] = true;
              p.markDestroy();
              return fraction;
            }
            return 1;
          },
          p.getLastPosition(),
          p.getPosition());

      if (!hitWall[0]) {
        // Only check player collision if it's NOT a player projectile (Friendly Fire
        // Check)
        if (!p.isPlayerProjectile()) {
          checkPlayersCollision(p, players);
        } else {
          // Check enemy collision if it IS a player projectile
          checkEnemiesCollision(p, enemies);
        }
      }
      // Distrugge le frecce al contatto con una parete
      if (p.shouldDestroy()) {
        if (p.getBody() != null) world.destroyBody(p.getBody());
        iter.remove();
      }
    }
  }

  // Controlla se la freccia ha colpito un player
  private void checkPlayersCollision(Projectile p, List<Player> players) {
    if (players == null) return;
    float hitRadiusSq = 14f * 14f;

    for (Player player : players) {
      if (player.isDead()) continue;

      boolean closeEnough = p.getPosition().dst2(player.getPosition()) < hitRadiusSq;
      boolean intersect =
          Intersector.intersectSegmentCircle(
              p.getLastPosition(), p.getPosition(), player.getPosition(), hitRadiusSq);

      if ((closeEnough || intersect) && !player.isInvincible()) {
        player.takeDamage(p.getDamage());
        if (player.getBody() != null && p.getBody() != null) {
          player.applyKnockback(p.getBody().getLinearVelocity().cpy().nor(), 800f, 0.15f);
        }
        p.markDestroy();
        return; // Destroy projectile after first hit
      }
    }
  }

  private void checkEnemiesCollision(
      Projectile p, List<io.github.soulslight.model.enemies.AbstractEnemy> enemies) {
    if (enemies == null) return;
    float hitRadiusSq = 14f * 14f; // Similar radius to player

    for (io.github.soulslight.model.enemies.AbstractEnemy enemy : enemies) {
      if (enemy.isDead()) continue;

      boolean closeEnough = p.getPosition().dst2(enemy.getPosition()) < hitRadiusSq;
      boolean intersect =
          Intersector.intersectSegmentCircle(
              p.getLastPosition(), p.getPosition(), enemy.getPosition(), hitRadiusSq);

      if (closeEnough || intersect) {
        enemy.takeDamage(p.getDamage());
        if (enemy.getBody() != null && p.getBody() != null) {
          enemy.applyKnockback(p.getBody().getLinearVelocity().cpy().nor(), 800f, 0.15f);
        }
        p.markDestroy();
        return;
      }
    }
  }

  public List<Projectile> getProjectiles() {
    return projectiles;
  }
}
