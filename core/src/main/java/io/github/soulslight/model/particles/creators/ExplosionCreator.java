package io.github.soulslight.model.particles.creators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import io.github.soulslight.model.particles.ParticleCreator;

public class ExplosionCreator extends ParticleCreator {
  @Override
  public ParticleEffect createEffect() {
    ParticleEffect effect = new ParticleEffect();
    effect.load(
        Gdx.files.internal(
            "particles/packs/Particle Park Explosion Small/Particle Park Explosion Small.p"),
        Gdx.files.internal("particles/packs/Particle Park Explosion Small"));
    return effect;
  }
}
