package io.github.soulslight.model.particles.creators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import io.github.soulslight.model.particles.ParticleCreator;

public class FireballBlueCreator extends ParticleCreator {
  @Override
  public ParticleEffect createEffect() {
    ParticleEffect effect = new ParticleEffect();
    effect.load(
        Gdx.files.internal(
            "particles/packs/Particle Park Fireball Blue/Particle Park Fireball Blue.p"),
        Gdx.files.internal("particles/packs/Particle Park Fireball Blue"));

    effect.scaleEffect(0.2f);

    // CONFIGURATION
    // Enable "Angle" so particles spawn in the direction of the projectile
    // (controlled by GameScreen).
    // Enable "Velocity" and set explicit values. If velocity is 0 (or scaled to
    // near-zero),
    // Angle logic might be ignored or invisible. The user suspected 0 velocity was
    // the cause.
    // Enable "Rotation" and disable "relative" so we can set absolute rotation
    // in GameScreen to face the target.
    for (com.badlogic.gdx.graphics.g2d.ParticleEmitter emitter : effect.getEmitters()) {
      emitter.getAngle().setActive(true);

      emitter.getVelocity().setActive(true);
      emitter.getVelocity().setHigh(60f, 150f); // Ensure visible movement

      emitter.getRotation().setActive(true);
      emitter.getRotation().setRelative(false);

      // Disable 'aligned' to ensure our manual Rotation values (from GameScreen) take
      // precedence
      emitter.setAligned(false);
    }

    return effect;
  }
}
