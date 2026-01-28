package io.github.soulslight.manager;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.soulslight.model.particles.ParticleCreator;
import io.github.soulslight.model.particles.ParticleType;
import io.github.soulslight.model.particles.creators.BloodCreator;
import io.github.soulslight.model.particles.creators.ExplosionCreator;
import io.github.soulslight.model.particles.creators.PentagramCreator;
import io.github.soulslight.model.particles.creators.PurpleSparksCreator;
import java.util.EnumMap;
import java.util.Map;

/** Singleton Pattern. Manages the lifecycle and pooling of particle effects. */
public class ParticleManager {

  private static ParticleManager instance;

  // Pools for each particle type
  private final Map<ParticleType, ParticleEffectPool> pools;
  // Active effects to be rendered/updated
  private final Array<ParticleEffectPool.PooledEffect> activeEffects;

  // Registry of creators (Factory Method linkage)
  private final Map<ParticleType, ParticleCreator> creators;

  private ParticleManager() {
    pools = new EnumMap<>(ParticleType.class);
    activeEffects = new Array<>();
    creators = new EnumMap<>(ParticleType.class);

    // Register creators
    creators.put(ParticleType.BLOOD, new BloodCreator());
    creators.put(ParticleType.EXPLOSION, new ExplosionCreator());
    creators.put(ParticleType.PENTAGRAM_GLITCHY, new PentagramCreator());
    creators.put(ParticleType.PURPLE_SPARKS, new PurpleSparksCreator());
    creators.put(
        ParticleType.FIREBALL, new io.github.soulslight.model.particles.creators.FireballCreator());
    creators.put(
        ParticleType.FIREBALL_BLUE,
        new io.github.soulslight.model.particles.creators.FireballBlueCreator());
  }

  /** Singleton Accessor (Lazy Initialization). */
  public static ParticleManager getInstance() {
    if (instance == null) {
      instance = new ParticleManager();
    }
    return instance;
  }

  /**
   * Loads specific particle types into memory (pre-warming pools).
   *
   * @param types Types to load.
   */
  public void load(ParticleType... types) {
    for (ParticleType type : types) {
      if (!pools.containsKey(type)) {
        ParticleCreator creator = creators.get(type);
        if (creator != null) {
          ParticleEffect template = creator.createEffect();
          // Create pool: initial capacity 5, max 50
          ParticleEffectPool pool = new ParticleEffectPool(template, 5, 50);
          pools.put(type, pool);
        }
      }
    }
  }

  /**
   * Spawn a particle effect at the given position.
   *
   * @param type The type of particle to spawn.
   * @param position The world position.
   * @return The spawned effect (or null if not loaded/available).
   */
  public ParticleEffectPool.PooledEffect spawn(ParticleType type, Vector2 position) {
    // Auto-load if not loaded (convenience, though explicit load is better for
    // perf)
    if (!pools.containsKey(type)) {
      load(type);
    }

    ParticleEffectPool pool = pools.get(type);
    if (pool != null) {
      ParticleEffectPool.PooledEffect effect = pool.obtain();
      effect.setPosition(position.x, position.y);
      activeEffects.add(effect);
      return effect;
    }
    return null;
  }

  public void update(float delta) {
    for (int i = activeEffects.size - 1; i >= 0; i--) {
      ParticleEffectPool.PooledEffect effect = activeEffects.get(i);
      effect.update(delta);
      if (effect.isComplete()) {
        effect.free();
        activeEffects.removeIndex(i);
      }
    }
  }

  public void render(SpriteBatch batch) {
    for (ParticleEffectPool.PooledEffect effect : activeEffects) {
      effect.draw(batch);
    }
  }

  /**
   * Clears all active effects and disposes resources if needed. (LibGDX pools are tricky with
   * dispose, usually we just clear references or dispose the template if stored)
   */
  public void clear() {
    for (ParticleEffectPool.PooledEffect effect : activeEffects) {
      effect.free(); // Return to pool
    }
    activeEffects.clear();
  }

  // Method to get a pool directly (e.g. for long-lived effects like doors)
  public ParticleEffectPool getPool(ParticleType type) {
    if (!pools.containsKey(type)) {
      load(type);
    }
    return pools.get(type);
  }
}
