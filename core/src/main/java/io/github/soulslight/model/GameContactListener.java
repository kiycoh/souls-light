package io.github.soulslight.model; // O in un package 'physics'

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class GameContactListener implements ContactListener {

    //Serve per verificare se due corpi sono in contatto
    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        Vector2 normal = contact.getWorldManifold().getNormal();

        checkSpikedBallWallHit(fa, fb, normal, false);
        checkSpikedBallWallHit(fb, fa, normal, true);
    }

    //Verifica se spikedBall ha sbattuto contro un muro
    private void checkSpikedBallWallHit(Fixture potentialEnemy, Fixture potentialWall, Vector2 normal, boolean invertNormal) {
        Object userData = potentialEnemy.getBody().getUserData();
        if (userData instanceof SpikedBall && potentialWall.getBody().getType() == BodyDef.BodyType.StaticBody) {
            Vector2 collisionNormal = invertNormal ? normal.cpy().scl(-1) : normal.cpy();
            ((SpikedBall) userData).onWallHit(collisionNormal);
        }
    }

    //QUeste tre qua sotto si devono scrivere perche ereditate da ContactListener e senza questi GAmeContactListener non funzionerebbe
    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
