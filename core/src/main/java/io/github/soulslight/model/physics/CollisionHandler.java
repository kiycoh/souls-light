package io.github.soulslight.model.physics;

/**
 * GoF Pattern: Adapter (Adaptee Interface) Defines the domain-specific collision handling methods
 * that operate on game entities rather than Box2D fixtures.
 */
public interface CollisionHandler {
  void handleBeginContact(Object userA, Object userB);

  void handleEndContact(Object userA, Object userB);
}
