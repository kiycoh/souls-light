package io.github.soulslight.model.enemies.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.Player;
import java.util.List;

public final class PatrolState implements EnemyState {
    private final float MAX_WANDER_DIST = 300f;
    private float patrolAngle = 0;
    private float wanderTimer = 0;

    @Override
    public void update(AbstractEnemy enemy, List<Player> players, float deltaTime) {
        if (enemy.getBody() == null) return;
        for (Player p : players) {
            if (enemy.canSeePlayer(p, enemy.getBody().getWorld())) {
                 enemy.setAIState(new ChaseState());
                 return;
            }
        }
        updateWanderPatrol(enemy, deltaTime);
    }

    private void updateWanderPatrol(AbstractEnemy enemy, float deltaTime) {
        Body body = enemy.getBody();
        if (body == null) return;

        wanderTimer -= deltaTime;
        Vector2 pos = body.getPosition();
        Vector2 spawn = enemy.getSpawnPoint();

        if (pos.dst(spawn) > MAX_WANDER_DIST) {
             patrolAngle = new Vector2(spawn).sub(pos).angleDeg();
             wanderTimer = 2.0f;
        } else if (wanderTimer <= 0) {
             patrolAngle = MathUtils.random(0, 360);
             wanderTimer = MathUtils.random(3.0f, 6.0f);
        }

        boolean hitCenter = checkObstacle(enemy, 0);
        boolean hitLeft = checkObstacle(enemy, 40);
        boolean hitRight = checkObstacle(enemy, -40);

        if (hitCenter || hitLeft || hitRight) {
            if (hitLeft && !hitRight) {
                patrolAngle -= 45f;
            } else if (hitRight && !hitLeft) {
                patrolAngle += 45f;
            } else {
                patrolAngle += 90f;
            }
            patrolAngle %= 360;
        }

        Vector2 dir = new Vector2(1, 0).setAngleDeg(patrolAngle);
        float patrolSpeed = enemy.getSpeed() * 0.3f;
        body.setLinearVelocity(dir.scl(patrolSpeed));
    }

    private boolean checkObstacle(AbstractEnemy enemy, float angleOffset) {
        final boolean[] hit = {false};
        float dynamicSensorDist = 60f;
        Body body = enemy.getBody();
        Vector2 rayStart = body.getPosition();
        Vector2 rayEnd = new Vector2(1, 0).setAngleDeg(patrolAngle + angleOffset).scl(dynamicSensorDist).add(rayStart);

        body.getWorld().rayCast((fixture, point, normal, fraction) -> {
              if (fixture.getBody().getType() == BodyDef.BodyType.StaticBody) {
                hit[0] = true;
                return fraction;
              }
              return 1;
            }, rayStart, rayEnd);
        return hit[0];
    }
}
