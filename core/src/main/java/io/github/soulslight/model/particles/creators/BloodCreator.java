package io.github.soulslight.model.particles.creators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import io.github.soulslight.model.particles.ParticleCreator;

public class BloodCreator extends ParticleCreator {
  @Override
  public ParticleEffect createEffect() {
    ParticleEffect effect = new ParticleEffect();
    effect.load(
        Gdx.files.internal("particles/packs/Particle Park Blood/Particle Park Blood.p"),
        Gdx.files.internal("particles/packs/Particle Park Blood"));

    effect.scaleEffect(0.5f);

    for (com.badlogic.gdx.graphics.g2d.ParticleEmitter emitter : effect.getEmitters()) {
      // n particles
      emitter
          .getEmission()
          .setHigh(
              emitter.getEmission().getHighMin() * 0.2f, emitter.getEmission().getHighMax() * 0.3f);

      // sprout wideness
      emitter
          .getVelocity()
          .setHigh(
              emitter.getVelocity().getHighMin() * 0.4f, emitter.getVelocity().getHighMax() * 0.5f);

      // life duration
      emitter
          .getLife()
          .setHigh(emitter.getLife().getHighMin() * 0.2f, emitter.getLife().getHighMax() * 0.3f);
    }

    return effect;
  }
}
