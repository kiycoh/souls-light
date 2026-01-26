package io.github.soulslight.model.enemies;

import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.model.combat.AttackStrategy;
import io.github.soulslight.model.combat.WarriorAttack;
import io.github.soulslight.model.entities.Player;
import java.util.List;

public class Chaser extends AbstractEnemy {

  // Diversi stati
  private enum State {
    CHASING, // Inseguimento quando vede i players
    ATTACKING, // Attacco
    RETREATING, // Fuga dopo ogni colpo inferto
    SEARCHING, // Ricerca subito dopo aver perso le tracce dei giocatori
    PATROLLING // Ronda intorno allo spawn quando non sa dove siamo
  }

  // Inizia nello stato di ronda
  private State currentState = State.PATROLLING;

  private float retreatTimer = 0f;
  private float attackCooldown = 0f;

  private static final float STOP_DISTANCE = 30f;

  // Durata tempo di ritirata dopo un colpo
  private static final float RETREAT_DURATION = 0.8f;

  // Tempo tra un colpo ed un altro
  private static final float ATTACK_RATE = 1.5f;

  public Chaser() {
    this(100, 90.0f, new WarriorAttack(20.0f));
  }

  public Chaser(float health, float speed, AttackStrategy strategy) {
    super();
    this.health = health;
    this.maxHealth = health;
    this.speed = speed;
    this.attackStrategy = strategy;
  }

  public Chaser(Chaser other) {
    super(other);
  }

  @Override
  public AbstractEnemy clone() {
    return new Chaser(this);
  }

  @Override
  public void updateBehavior(List<Player> players, float deltaTime) {
    if (players == null || players.isEmpty() || this.health <= 0) return;

    // CHANGED: Use nearest target instead of players.get(0)
    Player target = getNearestTarget(players);
    if (target == null) return; // Stop if everyone is dead

    // Feature Logic: RoomIdleState check
    // If the enemy is in RoomIdleState (waiting for player to enter room), do
    // nothing.
    if (getCurrentState() instanceof io.github.soulslight.model.enemies.ai.RoomIdleState) {
      getCurrentState().update(this, players, deltaTime);
      return;
    }

    syncBody();

    if (attackCooldown > 0) attackCooldown -= deltaTime;
    if (retreatTimer > 0) retreatTimer -= deltaTime;

    Vector2 myPos = (body != null) ? body.getPosition() : this.position;

    boolean canSee = canSeePlayer(target, body.getWorld());

    if (canSee) {
      searchTimer = SEARCH_DURATION; // Reset della memoria
      float distance = myPos.dst(target.getPosition());

      if (currentState == State.SEARCHING || currentState == State.PATROLLING) {
        currentState = State.CHASING;
      }
      switch (currentState) {
        case CHASING:
          if (distance <= STOP_DISTANCE) {
            if (body != null) body.setLinearVelocity(0, 0);
            currentState = State.ATTACKING;
          } else {
            moveTowards(target.getPosition(), deltaTime);
          }
          break;

        case ATTACKING:
          if (body != null) body.setLinearVelocity(0, 0);
          if (attackCooldown <= 0) {
            this.attack(players);
            currentState = State.RETREATING;
            retreatTimer = RETREAT_DURATION;
            attackCooldown = ATTACK_RATE;
          } else if (distance > STOP_DISTANCE + 15f) {
            currentState = State.CHASING;
          }
          break;

        case RETREATING:
          moveAway(target.getPosition());
          if (retreatTimer <= 0) currentState = State.CHASING;
          break;
        default:
          break;
      }
    } else {
      if (currentState == State.RETREATING && retreatTimer > 0) {
        moveAway(target.getPosition());
        return;
      }

      if (searchTimer > 0) {
        currentState = State.SEARCHING;
        searchTimer -= deltaTime;

        float distToLastPos = myPos.dst(lastKnownPlayerPos);
        if (distToLastPos > 15f) {
          moveTowards(lastKnownPlayerPos, deltaTime);
        } else {
          if (body != null) body.setLinearVelocity(0, 0);
        }
      } else {
        currentState = State.PATROLLING;
        updateWanderPatrol(deltaTime);
      }
    }
  }

  // Method to sync body position back to Entity state (from Enemy.java, moved
  // here or rely on
  // AbstractEnemy?)
  // AbstractEnemy doesn't seem to have syncBody(), but it updates position in
  // moveTowards/moveAway.
  // However, Chaser.updateBehavior calls syncBody().
  // Enemy.java had syncBody(). AbstractEnemy.java did not.
  // I should add syncBody() to Chaser or AbstractEnemy.
  // Let's add it to Chaser for now to be safe, or just inline it:
  // this.position.set(body.getPosition());

  private void syncBody() {
    if (body != null) {
      this.position.set(body.getPosition());
    }
  }
}
