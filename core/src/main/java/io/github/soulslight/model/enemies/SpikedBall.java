package io.github.soulslight.model.enemies;

import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.model.combat.ContactDamageAttack;
import io.github.soulslight.model.entities.Player;
import java.util.Collections;
import java.util.List;

public class SpikedBall extends AbstractEnemy {

  // La Spikedball ha 4 possibili stadi
  private enum State {
    PATROLLING,
    SEARCHING,
    COOLDOWN,
    CHARGING
  }

  private State currentState = State.PATROLLING;
  private float stateTimer;
  private final float COOLDOWN_TIME = 1.5f;
  private final float MAX_CHARGE_TIME = 2.5f;

  private Vector2 chargeDirection;
  private boolean hasHitPlayer = false;

  public SpikedBall() {
    super();
    setupStats();
    this.currentState = State.PATROLLING;
    this.chargeDirection = new Vector2(0, 0);
    this.attackStrategy = new ContactDamageAttack();
  }

  private void setupStats() {
    this.health = 500.0f;
    this.maxHealth = 500.0f;
    this.speed = 250.0f;
  }

  public SpikedBall(SpikedBall other) {
    super(other);
    this.currentState = State.PATROLLING;
    this.chargeDirection = new Vector2(0, 0);
  }

  @Override
  public AbstractEnemy clone() {
    return new SpikedBall(this);
  }

  @Override
  public void updateBehavior(List<Player> players, float deltaTime) {
    if (players.isEmpty() || this.health <= 0) return;
    Player target = getNearestTarget(players);
    if (target == null) return;

    // Feature Logic: RoomIdleState check
    if (getCurrentState() instanceof io.github.soulslight.model.enemies.ai.RoomIdleState) {
      getCurrentState().update(this, players, deltaTime);
      return;
    }

    syncBody();

    boolean canSee = canSeePlayer(target, body.getWorld());

    if (currentState == State.CHARGING) {
      updateCharge(players, deltaTime);
    } else if (canSee) {
      searchTimer = SEARCH_DURATION;
      if (currentState != State.COOLDOWN) {
        currentState = State.COOLDOWN;
        stateTimer = COOLDOWN_TIME;
        if (body != null) body.setLinearVelocity(0, 0);
      }

      stateTimer -= deltaTime;
      if (stateTimer <= 0) {
        prepareCharge(target.getPosition());
      }
    } else {
      if (searchTimer > 0) {
        currentState = State.SEARCHING;
        searchTimer -= deltaTime;
        handleSearchLogic(deltaTime);
      } else {
        currentState = State.PATROLLING;
        updateWanderPatrol(deltaTime);
      }
    }
  }

  private void updateCharge(List<Player> players, float deltaTime) {
    stateTimer -= deltaTime;

    if (body != null) {
      body.setLinearVelocity(chargeDirection.x * speed, chargeDirection.y * speed);
      this.position.set(body.getPosition());
    }

    checkCollisions(players);

    if (stateTimer <= 0) {
      stopCharge();
    }
  }

  private void prepareCharge(Vector2 targetPos) {
    hasHitPlayer = false;
    Vector2 myPos = (body != null) ? body.getPosition() : this.position;
    this.chargeDirection = targetPos.cpy().sub(myPos).nor();
    this.currentState = State.CHARGING;
    this.stateTimer = MAX_CHARGE_TIME;
  }

  private void stopCharge() {
    this.currentState = State.COOLDOWN;
    this.stateTimer = COOLDOWN_TIME;
    if (body != null) body.setLinearVelocity(0, 0);
  }

  private void handleSearchLogic(float deltaTime) {
    float distToLastPos = getPosition().dst(lastKnownPlayerPos);
    if (distToLastPos > 20f) {
      moveTowards(lastKnownPlayerPos, deltaTime);
    } else {
      if (body != null) body.setLinearVelocity(0, 0);
    }
  }

  private void checkCollisions(List<Player> players) {
    Vector2 myPos = getPosition();
    float collisionRadius = 35f;

    for (Player p : players) {
      if (myPos.dst(p.getPosition()) < collisionRadius && !hasHitPlayer) {
        // Respinge il player
        this.chargeDirection.scl(-1);
        this.attackStrategy.executeAttack(this, Collections.singletonList(p));
        this.stateTimer = 0.4f; // Rimbalza per mezzo secondo e poi stop
        hasHitPlayer = true;

        if (p.getBody() != null) {
          Vector2 knockbackDir = p.getPosition().cpy().sub(myPos).nor();
          p.applyKnockback(knockbackDir, 800f, 0.2f);
        }
      }
    }
  }

  public void onWallHit(Vector2 normal) {
    if (currentState == State.CHARGING) {

      float dot = chargeDirection.dot(normal);
      chargeDirection.mulAdd(normal, -2f * dot);
      chargeDirection.nor();

      stateTimer -= 0.6f;

      if (stateTimer <= 0) {
        stopCharge();
      }

      System.out.println("SpikedBall: RIMBALZO ANGOLARE!");
    }
  }

  public boolean isCharging() {
    return currentState == State.CHARGING;
  }

  private void syncBody() {
    if (body != null) {
      this.position.set(body.getPosition());
    }
  }
}
