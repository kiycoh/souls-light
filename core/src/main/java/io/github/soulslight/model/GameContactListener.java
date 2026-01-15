package io.github.soulslight.model;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import io.github.soulslight.utils.LogHelper;

public class GameContactListener implements ContactListener {

  @Override
  public void beginContact(Contact contact) {
    Fixture fa = contact.getFixtureA();
    Fixture fb = contact.getFixtureB();

    String aName = getFixtureName(fa);
    String bName = getFixtureName(fb);

    // Throttle collision logs to once per second per specific collision pair
    LogHelper.logThrottled("Physics", "Collision Start: " + aName + " <-> " + bName, 1.0f);
  }

  private String getFixtureName(Fixture fixture) {
    short category = fixture.getFilterData().categoryBits;

    if ((category & Constants.BIT_PLAYER) != 0) return "Player";
    if ((category & Constants.BIT_ENEMY) != 0) return "Enemy";
    if ((category & Constants.BIT_WALL) != 0) return "Wall";

    return "Unknown";
  }

  @Override
  public void endContact(Contact contact) {}

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {}

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {}
}
