package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.manager.ParticleManager;
import io.github.soulslight.manager.TextureManager;
import io.github.soulslight.model.Constants;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.entities.Projectile;
import io.github.soulslight.model.particles.ParticleType;
import io.github.soulslight.model.room.Door;
import io.github.soulslight.model.room.Portal;
import io.github.soulslight.model.room.PortalRoom;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ParticleRenderSystem {

  private final Map<Door, ParticleEffectPool.PooledEffect> doorEffectsMap = new IdentityHashMap<>();
  private final Map<Projectile, ParticleEffectPool.PooledEffect> projectileEffectsMap =
      new IdentityHashMap<>();

  private ParticleEffectPool.PooledEffect portalEffect;

  // portalEffectTime appears unused in original code, skipping

  public void renderDoorParticles(SpriteBatch batch, GameModel model, float delta) {
    if (model.getLevel() == null || model.getLevel().getRoomManager() == null) return;

    ParticleManager pm = ParticleManager.getInstance();

    // 1. Update active effects
    Iterator<Map.Entry<Door, ParticleEffectPool.PooledEffect>> it =
        doorEffectsMap.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<Door, ParticleEffectPool.PooledEffect> entry = it.next();
      Door door = entry.getKey();
      ParticleEffectPool.PooledEffect effect = entry.getValue();

      // If door is no longer locked, stop effect
      if (!door.isLocked()) {
        effect.free();
        it.remove();
        continue;
      }

      effect.update(delta);
      effect.draw(batch);

      if (effect.isComplete()) {
        effect.reset();
      }
    }

    // Spawn effects for locked doors that don't have one
    if (model.getLevel().getRoomManager() != null) {
      for (io.github.soulslight.model.room.Room room :
          model.getLevel().getRoomManager().getRooms()) {
        for (Door door : room.getDoors()) {
          if (door.isLocked()) {
            if (!doorEffectsMap.containsKey(door)) {
              ParticleEffectPool pool = pm.getPool(ParticleType.PENTAGRAM_GLITCHY);
              if (pool != null) {
                ParticleEffectPool.PooledEffect eff = pool.obtain();
                eff.setPosition(door.getPosition().x, door.getPosition().y);
                eff.start();
                doorEffectsMap.put(door, eff);
              }
            }
          }
        }
      }
    }
  }

  public void renderProjectiles(SpriteBatch batch, GameModel model, float delta) {
    com.badlogic.gdx.graphics.Texture tArrow = TextureManager.getInstance().get("arrow");
    if (tArrow == null) tArrow = TextureManager.getInstance().get("player");

    Set<Projectile> active = new HashSet<>();

    for (Projectile p : model.getProjectiles()) {
      active.add(p);
      String type = p.getType();

      if (Constants.PROJ_HOMING_FIREBALL.equals(type) // Was startsWith in original?
          || type.startsWith(Constants.PROJ_HOMING_FIREBALL)
          || Constants.PROJ_FIREBALL.equals(type)) {
        updateAndDrawParticle(batch, p, ParticleType.FIREBALL_BLUE, delta); // Using batch from arg
      } else {
        // Default Sprite Drawing
        batch.draw(
            tArrow,
            p.getPosition().x - 16,
            p.getPosition().y - 4,
            16,
            4,
            32,
            8,
            1,
            1,
            p.getRotation(),
            0,
            0,
            tArrow.getWidth(),
            tArrow.getHeight(),
            false,
            false);
      }
    }

    // Cleanup dead particles
    Iterator<Map.Entry<Projectile, ParticleEffectPool.PooledEffect>> it =
        projectileEffectsMap.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<Projectile, ParticleEffectPool.PooledEffect> entry = it.next();
      if (!active.contains(entry.getKey())) {
        entry.getValue().free();
        it.remove();
      }
    }
  }

  private void updateAndDrawParticle(
      SpriteBatch batch, Projectile p, ParticleType type, float delta) {
    ParticleEffectPool.PooledEffect effect = projectileEffectsMap.get(p);

    if (effect == null) {
      ParticleEffectPool pool = ParticleManager.getInstance().getPool(type);
      if (pool != null) {
        effect = pool.obtain();
        projectileEffectsMap.put(p, effect);
      }
    }

    if (effect != null) {
      effect.setPosition(p.getPosition().x, p.getPosition().y);

      // Rotate Blue Fireballs to face target
      if (type == ParticleType.FIREBALL_BLUE) {
        float angle = p.getRotation();
        for (ParticleEmitter emitter : effect.getEmitters()) {
          emitter.getAngle().setHigh(angle + 180f - 15f, angle + 180f + 15f);
          emitter.getAngle().setLow(angle + 180f);
          emitter.getRotation().setHigh(angle);
          emitter.getRotation().setLow(angle);
        }
      }

      effect.update(delta);
      effect.draw(batch);
    }
  }

  public void renderPortal(SpriteBatch batch, GameModel model) {
    if (model.getLevel() == null) return;

    Portal portal = null;

    // Check for dungeon-style PortalRoom first
    if (model.getLevel().getRoomManager() != null) {
      PortalRoom portalRoom = model.getLevel().getRoomManager().getPortalRoom();
      if (portalRoom != null && portalRoom.getPortal() != null) {
        portal = portalRoom.getPortal();
      }
    }

    // Fall back to cave-style direct portal
    if (portal == null) {
      portal = model.getLevel().getCavePortal();
    }

    if (portal == null) return;

    Vector2 pos = portal.getPosition();

    ParticleEffectPool pool = ParticleManager.getInstance().getPool(ParticleType.PURPLE_SPARKS);

    if (pool != null) {
      if (portalEffect == null) {
        portalEffect = pool.obtain();
        portalEffect.start();
      }

      portalEffect.setPosition(pos.x, pos.y);
      portalEffect.update(Gdx.graphics.getDeltaTime());
      portalEffect.draw(batch);

      if (portalEffect.isComplete()) {
        portalEffect.reset();
      }
    }

    // Use the portal's current frame from the state machine
    TextureRegion frame = portal.getFrame();
    if (frame != null) {
      batch.draw(
          frame,
          pos.x - 32f,
          pos.y - 32f,
          32f,
          32f,
          64f,
          64f,
          1f,
          1f,
          0f); // Replicated drawEntity simplified
    }
  }

  public void dispose() {
    // Free active effects
    for (ParticleEffectPool.PooledEffect e : doorEffectsMap.values()) e.free();
    for (ParticleEffectPool.PooledEffect e : projectileEffectsMap.values()) e.free();
    if (portalEffect != null) portalEffect.free();

    doorEffectsMap.clear();
    projectileEffectsMap.clear();
    portalEffect = null;
  }
}
