package io.github.soulslight.model.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.soulslight.model.enemies.SpikedBall;

/**
 * GoF Pattern: Adapter (Adapter).
 *
 * <p>Traduce l'interfaccia {@link ContactListener} di Box2D in {@link CollisionHandler}. Il lavoro
 * vero è in {@link #resolve(Fixture)}: è l'unico punto del progetto in cui lo userData opaco di
 * Box2D diventa un tipo di dominio.
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

    Collidable a = resolve(fa);
    Collidable b = resolve(fb);

    handler.handleBeginContact(a, b);

    // Solo lo SpikedBall ha bisogno della normale del contatto. getWorldManifold() attraversa
    // JNI verso Box2D nativo, quindi costa ordini di grandezza più di un test di tipo: prima
    // veniva invocata a ogni contatto, ora solo quando serve davvero.
    boolean aRimbalza = a instanceof SpikedBall && isWall(fb);
    boolean bRimbalza = b instanceof SpikedBall && isWall(fa);
    if (!aRimbalza && !bRimbalza) return;

    Vector2 normal = contact.getWorldManifold().getNormal();
    if (aRimbalza) {
      handler.handleSpikedBallHit((SpikedBall) a, normal.cpy());
    }
    if (bRimbalza) {
      handler.handleSpikedBallHit((SpikedBall) b, normal.cpy().scl(-1));
    }
  }

  @Override
  public void endContact(Contact contact) {
    handler.handleEndContact(resolve(contact.getFixtureA()), resolve(contact.getFixtureB()));
  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {}

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {}

  /**
   * @return la controparte di dominio del corpo, o {@code null} se non ne ha (i muri statici)
   */
  private Collidable resolve(Fixture fixture) {
    if (fixture == null || fixture.getBody() == null) return null;
    Object userData = fixture.getBody().getUserData();
    return (userData instanceof Collidable collidable) ? collidable : null;
  }

  private boolean isWall(Fixture f) {
    // In questo progetto i corpi statici sono i muri.
    return f != null && f.getBody() != null && f.getBody().getType() == BodyDef.BodyType.StaticBody;
  }
}
