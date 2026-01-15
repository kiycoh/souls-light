package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;

public class Ranger extends Enemy {

  private static final float FLEE_DISTANCE_RATIO = 0.5f;

  public Ranger() {
    this(70, 50.0f, new ArcherAttack());
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

    float distance = this.position.dst(target.getPosition());
    float maxRange = this.attackStrategy.getRange();
    float minSafeDistance = maxRange * FLEE_DISTANCE_RATIO;

    if (distance < minSafeDistance) {
      // Escapes if too close
      moveAway(target.getPosition(), deltaTime);
    } else if (distance <= maxRange) {
      // PERFECT DISTANCE -> Stands still and shoots
      this.attack(target);
    } else {
      // TOO FAR -> Get closer
      moveTowards(target.getPosition(), deltaTime);
    }
  }

  // Goes towards the target if too far
  @Override
  public void moveTowards(Vector2 targetPos, float deltaTime) {
    Vector2 direction = targetPos.cpy().sub(this.position);
    direction.nor();
    this.position.mulAdd(direction, this.speed * deltaTime);
  }

  // Escapes from the target
  public void moveAway(Vector2 targetPos, float deltaTime) {
    // Calculation: (MyPosition - TargetPosition) creates a vector pointing AWAY from the target
    Vector2 direction = this.position.cpy().sub(targetPos);

    direction.nor(); // Normalize to 1

    // Moves in the escape direction
    this.position.mulAdd(direction, this.speed * deltaTime);
  }
}
