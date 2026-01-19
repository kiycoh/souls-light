package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.utils.LogHelper;

public class Ranger extends Enemy {

  private static final float FLEE_DISTANCE_RATIO = 0.5f;

  public Ranger() {
    this(70, 50.0f, new ArcherAttack(7.0f));
  }

  public Ranger(float health, float speed, AttackStrategy strategy) {
    super();
    this.health = health;
    this.speed = speed;
    this.attackStrategy = strategy;
  }

  private Ranger(Ranger other) {
    super(other);
    this.attackStrategy = other.attackStrategy;
  }

  @Override
  public Enemy clone() {
    return new Ranger(this);
  }

  @Override
  public void update(Player target, float deltaTime) {
    if (target == null || this.health <= 0) return;

    syncBody();

    float distance = this.position.dst(target.getPosition());
    float maxRange = this.attackStrategy.getRange();
    float minSafeDistance = maxRange * FLEE_DISTANCE_RATIO;

    if (distance < minSafeDistance) {
      // Escapes if too close
      moveAway(target.getPosition(), deltaTime);
    } else if (distance <= maxRange) {
      // PERFECT DISTANCE -> Stands still and shoots
      if (body != null) body.setLinearVelocity(0, 0);
      LogHelper.logThrottled("AI", "Ranger is shooting at the target.", 2.0f);
      this.attack(target);
    } else {
      // TOO FAR -> Get closer
      moveTowards(target.getPosition(), deltaTime);
    }
  }

  // Goes towards the target if too far
  @Override
  public void moveTowards(Vector2 targetPos, float deltaTime) {
    super.moveTowards(targetPos, deltaTime);
  }

  // Escapes from the target
  public void moveAway(Vector2 targetPos, float deltaTime) {
    if (body != null) {
      Vector2 direction = this.position.cpy().sub(targetPos);
      direction.nor();
      float speedMeters = this.speed / Constants.PPM;
      body.setLinearVelocity(direction.scl(speedMeters));
    } else {
      Vector2 direction = this.position.cpy().sub(targetPos);
      direction.nor();
      this.position.mulAdd(direction, this.speed * deltaTime);
    }
  }
}
