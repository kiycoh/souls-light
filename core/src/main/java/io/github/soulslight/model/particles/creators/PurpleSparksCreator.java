package io.github.soulslight.model.particles.creators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import io.github.soulslight.model.particles.ParticleCreator;

public class PurpleSparksCreator extends ParticleCreator {
  @Override
  public ParticleEffect createEffect() {
    ParticleEffect effect = new ParticleEffect();
    effect.load(
        Gdx.files.internal("particles/packs/Particle Park Sparks/Particle Park Sparks.p"),
        Gdx.files.internal("particles/packs/Particle Park Sparks"));

    // Tint modification: Make it Purple
    for (ParticleEmitter emitter : effect.getEmitters()) {
      // We must preserve the timeline structure (array length),
      // otherwise libGDX's update logic (which relies on timeline[] length)
      // will try to access indices out of bounds if we just shrink the colors array.

      float[] colors = emitter.getTint().getColors();
      for (int i = 0; i < colors.length; i += 3) {
        // Set to Purple (R=0.6, G=0.0, B=1.0)
        colors[i] = 0.6f; // R
        if (i + 1 < colors.length) colors[i + 1] = 0.0f; // G
        if (i + 2 < colors.length) colors[i + 2] = 1.0f; // B
      }

      // Reduce particle emission to 40% of original
      com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue emission =
          emitter.getEmission();
      emission.setHigh(emission.getHighMin() * 0.4f, emission.getHighMax() * 0.4f);
    }

    return effect;
  }
}
