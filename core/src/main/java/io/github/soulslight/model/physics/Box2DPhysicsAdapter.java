package io.github.soulslight.model.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * GoF Pattern: Adapter (Adapter) Adapts the Box2D ContactListener interface to the domain-specific
 * CollisionHandler interface. Ensures the domain logic operates on objects, isolating it from
 * physics engine details.
 */
public class Box2DPhysicsAdapter implements ContactListener {

  private final GameCollisionHandler handler;

  public Box2DPhysicsAdapter() {
    this.handler = new GameCollisionHandler();
  }

  public GameCollisionHandler getCollisionHandler() {
    return handler;
  }

  @Override
  public void beginContact(Contact contact) {
    Fixture fa = contact.getFixtureA();
    Fixture fb = contact.getFixtureB();

    Object userA = getUserData(fa);
    Object userB = getUserData(fb);

    handler.handleBeginContact(userA, userB);

    // SpikedBall special case needing Normal
    Vector2 normal = contact.getWorldManifold().getNormal();

    // Check A vs Wall
    if (isSpikedBall(userA) && isWall(fb)) {
      handler.handleSpikedBallHit(userA, normal.cpy());
    }
    // Check B vs Wall (invert normal)
    if (isSpikedBall(userB) && isWall(fa)) {
      handler.handleSpikedBallHit(userB, normal.cpy().scl(-1));
    }
  }

  @Override
  public void endContact(Contact contact) {
    Fixture fa = contact.getFixtureA();
    Fixture fb = contact.getFixtureB();

    Object userA = getUserData(fa);
    Object userB = getUserData(fb);

    handler.handleEndContact(userA, userB);
  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {}

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {}

  private Object getUserData(Fixture f) {
    if (f == null || f.getBody() == null) return null;
    return f.getBody().getUserData();
  }

  private boolean isSpikedBall(Object o) {
    return o instanceof io.github.soulslight.model.enemies.SpikedBall;
  }

  private boolean isWall(Fixture f) {
    // Check for static body as Wall proxy
    return f != null && f.getBody().getType() == BodyDef.BodyType.StaticBody;
  }
}
