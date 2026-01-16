package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.utils.LogHelper;

public class Chaser extends Enemy {

  public Chaser() {
    this(100, 80.0f, new WarriorAttack());
  }

  public Chaser(float health, float speed, AttackStrategy strategy) {
    super();
    this.health = health;
    this.speed = speed;
    this.attackStrategy = strategy;
  }

  private Chaser(Chaser other) {
    super(other);
    this.attackStrategy = other.attackStrategy;
  }

  @Override
  public Enemy clone() {
    return new Chaser(this);
  }

  @Override
  public void update(Player target, float deltaTime) {
    if (target == null || this.health <= 0) return;

    syncBody();

    float distance = this.position.dst(target.getPosition());
    float range = this.attackStrategy.getRange();

    if (distance <= range) {
      LogHelper.logThrottled("AI", "Chaser is attacking the target.", 2.0f);
      this.attack(target);
      if (body != null) body.setLinearVelocity(0, 0); // Stop when attacking
    } else {
      moveTowards(target.getPosition(), deltaTime);
    }
  }

  @Override
  public void moveTowards(Vector2 targetPos, float deltaTime) {
    super.moveTowards(targetPos, deltaTime);
  }
}
