package io.github.soulslight.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class SpikedBall extends AbstractEnemy {

  // States
  private enum State {
    COOLDOWN,
    CHARGING
  }

  private State currentState;

  // Timer and Parameters
  private float stateTimer; // duration of a state (charge or attack)
  private final float COOLDOWN_TIME = 2.0f;
  private final float MAX_CHARGE_TIME = 3.0f; // Rolls for 1 second

  private Vector2 chargeDirection; // Does not change direction during charge

  public SpikedBall() {
    super();
    this.health = 500.0f; // tough
    this.speed = 300.0f; // fast (Chaser is 80)

    this.currentState = State.COOLDOWN;
    this.stateTimer = COOLDOWN_TIME;
    this.chargeDirection = new Vector2(0, 0);

    this.attackStrategy = new ContactDamageAttack();
  }

  public SpikedBall(SpikedBall other) {
    super(other);
    this.currentState = State.COOLDOWN;
    this.stateTimer = COOLDOWN_TIME;
    this.chargeDirection = new Vector2(0, 0);
  }

  @Override
  public AbstractEnemy clone() {
    return new SpikedBall(this);
  }

  @Override
  public void updateBehavior(List<Player> players, float deltaTime) {
    if (players.isEmpty()) return;
    Player target = players.get(0);

    // Single timer management for both states
    stateTimer -= deltaTime;

    if (currentState == State.COOLDOWN) {
      // --- AIM AND WAIT

      if (stateTimer <= 0) {
        // Looks for player position
        prepareCharge(target.getPosition());
      }

    } else if (currentState == State.CHARGING) {
      // --- CHARGE THE PLAYER

      Vector2 currentPos = this.getPosition();
      currentPos.mulAdd(chargeDirection, speed * deltaTime);

      // Hits anyone in its path
      checkCollisions(players);

      // If timer ends, stop and recharge
      if (stateTimer <= 0) {
        stopCharge();
      }
    }
  }

  public void onWallHit() {
    if (currentState == State.CHARGING) {
      Gdx.app.log("SpikedBall", "SBAM! Toccato il muro. Stop carica.");
      stopCharge();
    }
  }

  private void prepareCharge(Vector2 targetPos) {
    /*System.out.println("--- CHARGE PREPARATION ---");
    System.out.println("My Position: " + this.getPosition());
    System.out.println("Target Position: " + targetPos);*/

    this.chargeDirection = targetPos.cpy().sub(this.getPosition());

    Gdx.app.log("SpikedBall", "Vettore Direzione (Pre-Normalize): " + this.chargeDirection);

    this.chargeDirection.nor();

    // System.out.println("Direction Vector (Final): " + this.chargeDirection);

    this.currentState = State.CHARGING;
    this.stateTimer = MAX_CHARGE_TIME;
  }

  private void stopCharge() {
    this.currentState = State.COOLDOWN;
    this.stateTimer = COOLDOWN_TIME; // Resets timer for pause
  }

  private void checkCollisions(List<Player> players) {
    for (Player p : players) {
      // If it touches the player (distance < sum of radii, e.g. 20 pixels)
      if (this.getPosition().dst(p.getPosition()) < 20f) {
        this.attack(players);
      }
    }
  }
}
