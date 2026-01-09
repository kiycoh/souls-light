package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class Ranger extends AbstractEnemy {

  private static final float INDICE_DI_FUGA = 0.5f;

  public Ranger() {
    super();
    this.health = 70;
    this.speed = 50.0f;
    this.attackStrategy = new ArcherAttack();
  }

  private Ranger(Ranger other) {
    super(other);
    this.attackStrategy = other.attackStrategy;
  }

  @Override
  public AbstractEnemy clone() {
    return new Ranger(this);
  }

  @Override
  public void updateBehavior(List<Player> players, float deltaTime) {
    if (players.isEmpty() || this.health <= 0) return;

    Player target = players.get(0);
    float distance = this.position.dst(target.getPosition());
    float maxRange = this.attackStrategy.getRange();
    float minSafeDistance = maxRange * INDICE_DI_FUGA;

    if (distance < minSafeDistance) {
      // Escapes if too close
      moveAway(target.getPosition(), deltaTime);
      // Optional: If you want it to shoot WHILE escaping, leave this.attack(players) here too.

    } else if (distance <= maxRange) {
      // PERFECT DISTANCE -> Stands still and shoots
      this.attack(players);

    } else {
      // TOO FAR -> Get closer
      moveTowards(target.getPosition(), deltaTime);
    }
  }

  // Goes towards the target if too far
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
