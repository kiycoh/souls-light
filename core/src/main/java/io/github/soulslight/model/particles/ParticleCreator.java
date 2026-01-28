package io.github.soulslight.model.particles;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;

/**
 * Creator Participant (Factory Method Pattern). Defines the interface for creating ParticleEffect
 * objects. Subclasses (ConcreteCreators) decide which specific effect to instantiate.
 */
public abstract class ParticleCreator {

  /**
   * Factory Method.
   *
   * @return A new instance of the specific ParticleEffect.
   */
  public abstract ParticleEffect createEffect();
}
