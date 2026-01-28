package io.github.soulslight.model.particles.creators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import io.github.soulslight.model.particles.ParticleCreator;

public class FireballCreator extends ParticleCreator {
  @Override
  public ParticleEffect createEffect() {
    ParticleEffect effect = new ParticleEffect();
    effect.load(
        Gdx.files.internal("particles/packs/Particle Park Fireball/Particle Park Fireball.p"),
        Gdx.files.internal("particles/packs/Particle Park Fireball"));
    return effect;
  }
}
