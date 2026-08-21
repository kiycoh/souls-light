package io.github.soulslight.model.physics;

/**
 * GoF Pattern: Adapter (Target).
 *
 * <p>L'interfaccia che il dominio vuole: parla di {@link Collidable}, non di {@code Fixture},
 * {@code Contact} o {@code Object}. È {@link Box2DPhysicsAdapter} a colmare la distanza fra questa
 * forma e quella che Box2D impone.
 *
 * <p>Un parametro {@code null} significa "corpo senza controparte di dominio", tipicamente un muro
 * statico.
 */
public interface CollisionHandler {

  void handleBeginContact(Collidable a, Collidable b);

  void handleEndContact(Collidable a, Collidable b);
}
