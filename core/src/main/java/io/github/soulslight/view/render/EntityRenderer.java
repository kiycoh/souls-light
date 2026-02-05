package io.github.soulslight.view.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.manager.TextureManager;
import io.github.soulslight.model.Constants;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.enemies.Chaser;
import io.github.soulslight.model.enemies.Oblivion;
import io.github.soulslight.model.enemies.Ranger;
import io.github.soulslight.model.enemies.Shielder;
import io.github.soulslight.model.enemies.SpikedBall;
import io.github.soulslight.model.entities.Player;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles rendering of game entities: players, enemies, and items. Extracted from GameScreen to
 * satisfy Single Responsibility Principle.
 */
public class EntityRenderer {

  private final SpriteBatch batch;

  // Animation timing
  private float enemyAnimTime = 0f;
  private float playerAnimTime = 0f;

  // Animation state maps
  private final Map<AbstractEnemy, Float> enemyAnimOffset = new IdentityHashMap<>();
  private final Map<AbstractEnemy, Boolean> enemyFacingRight = new IdentityHashMap<>();
  private final Map<Player, Boolean> playerFacingRight = new IdentityHashMap<>();

  private enum EnemyAnimType {
    CHASER,
    RANGER,
    SHIELDER,
    SPIKEDBALL
  }

  public EntityRenderer(SpriteBatch batch) {
    this.batch = batch;
  }

  /** Updates animation timers. Call once per frame. */
  public void updateAnimationTimers(float delta) {
    enemyAnimTime += delta;
    playerAnimTime += delta;
  }

  /** Cleans up state for a dead/removed enemy. */
  public void removeEnemyState(AbstractEnemy enemy) {
    enemyAnimOffset.remove(enemy);
    enemyFacingRight.remove(enemy);
  }

  // ============================================================
  // Player Rendering
  // ============================================================

  /** Renders a player with proper animation and facing direction. */
  public void renderPlayer(Player player, int index) {
    if (player == null) return;

    TextureRegion frame = computePlayerFrame(player, index);
    boolean flipX = shouldFlipPlayerXStable(player);

    float drawWidth = 32f;
    float drawHeight = 46f;

    if (frame != null) {
      drawEntity(frame, player.getPosition(), drawWidth, drawHeight, flipX);
    } else {
      drawEntity(
          TextureManager.getInstance().get("player"), player.getPosition(), drawWidth, drawHeight);
    }
  }

  private TextureRegion computePlayerFrame(Player player, int index) {
    boolean isIdle = true;

    if (player.getBody() != null) {
      Vector2 vel = player.getBody().getLinearVelocity();
      isIdle = vel.len2() < Constants.IDLE_VELOCITY_EPS * Constants.IDLE_VELOCITY_EPS;
    }

    if (isIdle) {
      return getPlayerAnimFrame(index, 0f);
    }

    return getPlayerAnimFrame(index, playerAnimTime);
  }

  private TextureRegion getPlayerAnimFrame(int index, float time) {
    switch (index) {
      case 0:
        return TextureManager.getInstance().getP1WalkFrame(time);
      case 1:
        return TextureManager.getInstance().getP2WalkFrame(time);
      default:
        return TextureManager.getInstance().getP1WalkFrame(time);
    }
  }

  private boolean shouldFlipPlayerXStable(Player player) {
    boolean facingRight = playerFacingRight.computeIfAbsent(player, p -> true);

    if (player.getBody() == null) {
      return !facingRight;
    }

    float vx = player.getBody().getLinearVelocity().x;

    if (vx > Constants.ENEMY_FLIP_EPS) {
      facingRight = true;
      playerFacingRight.put(player, true);
    } else if (vx < -Constants.ENEMY_FLIP_EPS) {
      facingRight = false;
      playerFacingRight.put(player, false);
    }

    return !facingRight;
  }

  // ============================================================
  // Enemy Rendering
  // ============================================================

  /**
   * Renders an enemy with proper animation and facing direction.
   *
   * @param enemy The enemy to render
   * @param players List of players (used for Oblivion facing logic)
   */
  public void renderEnemy(AbstractEnemy enemy, List<Player> players) {
    if (enemy == null || enemy.isDead() || !enemy.isSpawned()) {
      return;
    }

    boolean flipX = shouldFlipXStable(enemy, players);
    drawEnemy(enemy, flipX);
  }

  private void drawEnemy(AbstractEnemy enemy, boolean flipX) {
    if (enemy instanceof Oblivion) {
      TextureRegion frame = computeOblivionFrame((Oblivion) enemy);
      if (frame != null) {
        // Oblivion spritesheet needs to be flipped
        boolean flipOblivion = !flipX;
        drawOblivion(frame, enemy.getPosition(), flipOblivion);
        return;
      }
    }

    if (enemy instanceof Chaser) {
      TextureRegion frame = computeAnimatedFrame(enemy, EnemyAnimType.CHASER);
      if (frame != null) {
        drawEntity(frame, enemy.getPosition(), 32, 46, flipX);
        return;
      }
    }

    if (enemy instanceof Ranger) {
      TextureRegion frame = computeAnimatedFrame(enemy, EnemyAnimType.RANGER);
      if (frame != null) {
        drawEntity(frame, enemy.getPosition(), 32, 46, flipX);
        return;
      }
    }

    if (enemy instanceof Shielder) {
      TextureRegion frame = computeAnimatedFrame(enemy, EnemyAnimType.SHIELDER);
      if (frame != null) {
        drawEntity(frame, enemy.getPosition(), 32, 54, flipX);
        return;
      }
    }

    if (enemy instanceof SpikedBall) {
      SpikedBall sb = (SpikedBall) enemy;
      TextureRegion frame;

      if (sb.isCharging()) {
        float offset = enemyAnimOffset.computeIfAbsent(enemy, e -> MathUtils.random(0f, 10f));
        frame = TextureManager.getInstance().getSpikedBallChargeFrame(enemyAnimTime + offset);
      } else {
        frame = computeAnimatedFrame(enemy, EnemyAnimType.SPIKEDBALL);
      }

      if (frame != null) {
        drawEntity(frame, enemy.getPosition(), 64, 64, flipX);
        return;
      }
    }

    // Fallback: static texture
    Texture tex = TextureManager.getInstance().getEnemyTexture(enemy);
    float size = (enemy instanceof Oblivion) ? Constants.OBLIVION_HEIGHT : 32f;
    drawEntity(tex, enemy.getPosition(), size, size);
  }

  private TextureRegion computeAnimatedFrame(AbstractEnemy enemy, EnemyAnimType type) {
    boolean isIdle = true;

    if (enemy.getBody() != null) {
      Vector2 vel = enemy.getBody().getLinearVelocity();
      isIdle = vel.len2() < Constants.IDLE_VELOCITY_EPS * Constants.IDLE_VELOCITY_EPS;
    }

    if (isIdle) {
      return getAnimFrame(type, 0f);
    }

    float offset = enemyAnimOffset.computeIfAbsent(enemy, e -> MathUtils.random(0f, 10f));
    return getAnimFrame(type, enemyAnimTime + offset);
  }

  private TextureRegion getAnimFrame(EnemyAnimType type, float time) {
    switch (type) {
      case CHASER:
        return TextureManager.getInstance().getChaserWalkFrame(time);
      case RANGER:
        return TextureManager.getInstance().getRangerWalkFrame(time);
      case SHIELDER:
        return TextureManager.getInstance().getShielderWalkFrame(time);
      case SPIKEDBALL:
        return TextureManager.getInstance().getSpikedBallWalkFrame(time);
      default:
        return null;
    }
  }

  private TextureRegion computeOblivionFrame(Oblivion boss) {
    if (boss.isDying()) {
      float t = boss.getDeathAnimTime();
      float duration = Oblivion.getDeathAnimDuration();
      if (t > duration) t = duration;
      return TextureManager.getInstance().getOblivionDeathFrame(t);
    }

    if (boss.isTeleportingOut() || boss.isTeleportingIn()) {
      float t = boss.getTeleportAnimTime();
      float duration = Oblivion.getTeleportAnimDuration();
      if (t > duration) t = duration;

      float animTime;
      if (boss.isTeleportingOut()) {
        animTime = Math.max(0f, duration - t);
      } else {
        animTime = t;
      }

      return TextureManager.getInstance().getOblivionTeleportFrame(animTime);
    }

    float offset = enemyAnimOffset.computeIfAbsent(boss, e -> MathUtils.random(0f, 10f));
    float time = enemyAnimTime + offset;

    if (boss.isMeleeWindup()) {
      return TextureManager.getInstance().getOblivionMeleeWindupFrame(time);
    }

    if (boss.isMeleeAttacking()) {
      return TextureManager.getInstance().getOblivionMeleeAttackFrame(time);
    }

    boolean isIdle = true;
    if (boss.getBody() != null) {
      Vector2 vel = boss.getBody().getLinearVelocity();
      isIdle = vel.len2() < Constants.IDLE_VELOCITY_EPS * Constants.IDLE_VELOCITY_EPS;
    }

    if (isIdle) {
      if (boss.isPhaseTwo()) {
        return TextureManager.getInstance().getOblivionSpellFrame(time);
      } else {
        return TextureManager.getInstance().getOblivionIdleFrame(time);
      }
    } else {
      return TextureManager.getInstance().getOblivionWalkFrame(time);
    }
  }

  private boolean shouldFlipXStable(AbstractEnemy enemy, List<Player> players) {
    boolean facingRight = enemyFacingRight.computeIfAbsent(enemy, e -> true);

    if (enemy instanceof Oblivion) {
      Oblivion ob = (Oblivion) enemy;

      // Locks animation direction in set states
      if (ob.isMeleeWindup()
          || ob.isMeleeAttacking()
          || ob.isTeleportingOut()
          || ob.isTeleportingIn()
          || ob.isDying()) {
        return !facingRight;
      }

      // else, flips towards nearest player
      if (players != null && !players.isEmpty()) {
        Player nearest = players.get(0);
        float bestDist2 = nearest.getPosition().dst2(ob.getPosition());
        for (int i = 1; i < players.size(); i++) {
          Player p = players.get(i);
          float d2 = p.getPosition().dst2(ob.getPosition());
          if (d2 < bestDist2) {
            bestDist2 = d2;
            nearest = p;
          }
        }

        float dx = nearest.getPosition().x - ob.getPosition().x;
        float EPS_X = 4f;
        if (dx > EPS_X) {
          facingRight = true;
        } else if (dx < -EPS_X) {
          facingRight = false;
        }
        enemyFacingRight.put(enemy, facingRight);
      }

      return !facingRight;
    }

    if (enemy.getBody() == null) {
      return !facingRight;
    }

    float vx = enemy.getBody().getLinearVelocity().x;

    if (vx > Constants.ENEMY_FLIP_EPS) {
      facingRight = true;
      enemyFacingRight.put(enemy, true);
    } else if (vx < -Constants.ENEMY_FLIP_EPS) {
      facingRight = false;
      enemyFacingRight.put(enemy, false);
    }

    return !facingRight;
  }

  // ============================================================
  // Generic Drawing Helpers
  // ============================================================

  /** Draws a texture centered at position. */
  public void drawEntity(Texture tex, Vector2 pos, float width, float height) {
    if (tex != null) {
      batch.draw(tex, pos.x - width / 2, pos.y - height / 2, width, height);
    }
  }

  /** Draws a texture region centered at position with optional horizontal flip. */
  public void drawEntity(
      TextureRegion region, Vector2 pos, float width, float height, boolean flipX) {
    if (region == null) return;

    float x = pos.x - width / 2f;
    float y = pos.y - height / 2f;

    if (!flipX) {
      batch.draw(region, x, y, width, height);
    } else {
      batch.draw(region, x + width, y, -width, height);
    }
  }

  /** Draws Oblivion boss with special offset for sprite-hitbox alignment. */
  private void drawOblivion(TextureRegion region, Vector2 pos, boolean flipX) {
    if (region == null) return;

    float width = Constants.OBLIVION_WIDTH;
    float height = Constants.OBLIVION_HEIGHT;

    float x = pos.x - width / 2f;
    // Matching sprite to hitbox
    float y = pos.y - height / 2f + Constants.OBLIVION_Y_OFFSET;

    if (!flipX) {
      batch.draw(region, x, y, width, height);
    } else {
      batch.draw(region, x + width, y, -width, height);
    }
  }
}
