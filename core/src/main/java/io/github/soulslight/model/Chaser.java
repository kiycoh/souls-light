package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;
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
    super();
    this.health = 100;
    this.maxHealth = 100;
    this.speed = 90.0f;
    this.attackStrategy = new WarriorAttack(20); // logica della classe guerriero per semplicità
  }

  public Chaser(Chaser other) {
    super(other);
  }

  @Override
  public AbstractEnemy clone() {
    return new Chaser(this);
  }

  // metodo che definisce il comportamentod el chaser
  @Override
  public void updateBehavior(List<Player> players, float deltaTime) {
    if (players.isEmpty() || this.health <= 0) return;

    if (attackCooldown > 0) attackCooldown -= deltaTime;
    if (retreatTimer > 0) retreatTimer -= deltaTime;

    Player target = players.get(0);
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

  // metodo per scappare
  @Override
  public void moveAway(Vector2 targetPos) {
    if (body != null) {
      Vector2 direction = body.getPosition().cpy().sub(targetPos);
      direction.nor();

      // Applica velocità
      body.setLinearVelocity(direction.scl(speed));

      // Sync grafica
      this.position.set(body.getPosition());
    }
  }
}
