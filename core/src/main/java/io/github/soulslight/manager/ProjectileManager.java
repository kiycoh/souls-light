package io.github.soulslight.manager;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.physics.box2d.*;
import io.github.soulslight.model.Player;
import io.github.soulslight.model.Projectile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProjectileManager {
    private final World world;
    private final List<Projectile> projectiles = new ArrayList<>();

    public ProjectileManager(World world) {
        this.world = world;
    }

    public void addProjectile(Projectile p) {
        projectiles.add(p);
    }

    //metodo che controlla tutto il movimento della freccia
    public void update(float deltaTime, Player player) {
        Iterator<Projectile> iter = projectiles.iterator();
        while (iter.hasNext()) {
            Projectile p = iter.next();
            p.update(deltaTime);

            float distSq = p.getPosition().dst2(p.getLastPosition());

            if (distSq < 0.0001f) {

                if (p.shouldDestroy()) {
                    if (p.getBody() != null) world.destroyBody(p.getBody());
                    iter.remove();
                }
                continue;
            }

            final boolean[] hitWall = {false};

            //Serve per usare linee invisibili per intercettare i muri
            world.rayCast((fixture, point, normal, fraction) -> {
                if (fixture.getBody().getType() == BodyDef.BodyType.StaticBody) {
                    hitWall[0] = true;
                    p.markDestroy();
                    return fraction;
                }
                return 1;
            }, p.getLastPosition(), p.getPosition());

            if (!hitWall[0]) {
                checkPlayerCollision(p, player);
            }
            //Distrugge le frecce al contatto con una parete
            if (p.shouldDestroy()) {
                if (p.getBody() != null) world.destroyBody(p.getBody());
                iter.remove();
            }
        }
    }

    //Controlla se la freccia ha colpito un player
    private void checkPlayerCollision(Projectile p, Player player) {
        float hitRadiusSq = 14f * 14f;
        boolean closeEnough = p.getPosition().dst2(player.getPosition()) < hitRadiusSq;
        boolean intersect = Intersector.intersectSegmentCircle(p.getLastPosition(), p.getPosition(), player.getPosition(), hitRadiusSq);

        if ((closeEnough || intersect) && !player.isInvincible()) {
            player.takeDamage(p.getDamage());
            if (player.getBody() != null && p.getBody() != null) {
                player.applyKnockback(p.getBody().getLinearVelocity().cpy().nor(), 800f, 0.15f);
            }
            p.markDestroy();
        }
    }

    public List<Projectile> getProjectiles() { return projectiles; }
}
