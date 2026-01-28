package io.github.soulslight.model.particles.creators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import io.github.soulslight.model.particles.ParticleCreator;

public class PentagramCreator extends ParticleCreator {
  @Override
  public ParticleEffect createEffect() {
    ParticleEffect effect = new ParticleEffect();
    effect.load(
        Gdx.files.internal(
            "particles/packs/Particle Park Pentagram Glitchy/Particle Park Pentagram Glitchy.p"),
        Gdx.files.internal("particles/packs/Particle Park Pentagram Glitchy"));

    effect.scaleEffect(0.8f);
    return effect;
  }
}
