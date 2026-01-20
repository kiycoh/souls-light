package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class Ranger extends AbstractEnemy {

  private static final float FLEE_DISTANCE = 200f;
  private static final float ATTACK_RANGE = 400f;

  private enum State {
    COMBAT, // attacco, fuga
    SEARCHING, // ricerca quando perde il player
    PATROLLING // ronda
  }

  private State currentState = State.PATROLLING;

  private float attackTimer = 0;
  private boolean readyToShoot = false;

  public Ranger() {
    this(70, 50.0f, new ArcherAttack(7.0f));
  }

  public Ranger(float health, float speed, AttackStrategy strategy) {
    super();
    setupStats(health, speed);
    this.attackStrategy = strategy;
  }

  private void setupStats(float health, float speed) {
    this.health = health;
    this.maxHealth = health;
    this.speed = 130.0f; // Feature branch overrides speed to 130.0f
    // If we want to respect constructor argument, we should use 'speed', but feature branch had 130
    // hardcoded.
    // I will use 130.0f to match feature branch behavior but keep the method generic if needed
    // later.
    this.speed = 130.0f;
  }

  public Ranger(Ranger other) {
    super(other);
    this.currentState = other.currentState;
  }

  @Override
  public AbstractEnemy clone() {
    return new Ranger(this);
  }

  @Override
  public void updateBehavior(List<Player> players, float deltaTime) {
    if (players == null || players.isEmpty() || this.health <= 0) return;
    Player target = players.get(0);

    syncBody();

    //  gestione timer
    if (attackTimer > 0) attackTimer -= deltaTime;

    // controlla se vede il nemico
    boolean canSee = canSeePlayer(target, body.getWorld());

    if (canSee) {
      currentState = State.COMBAT;
      searchTimer = SEARCH_DURATION; // Reset memoria

      handleCombatLogic(target, deltaTime);

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

  private void handleCombatLogic(Player target, float deltaTime) {
    Vector2 myPos = (body != null) ? body.getPosition() : this.position;
    float distance = myPos.dst(target.getPosition());

    // Scappa se troppo vicino
    if (distance < FLEE_DISTANCE) {
      // Troppo vicino: Scappa usando il metodo del padre!
      moveAway(target.getPosition());
    } else if (distance > ATTACK_RANGE - 50f) {
      // Troppo lontano: cerca di avvicinarsi
      moveTowards(target.getPosition(), deltaTime);
    } else {
      // Distanza perfetta:si ferma
      if (body != null) body.setLinearVelocity(0, 0);
    }

    // Se i giocatori sono entro il range attacca
    if (distance <= ATTACK_RANGE) {
      if (attackTimer <= 0) {
        readyToShoot = true;
        attackTimer = 2.0f; // Cooldown
        System.out.println("RANGER: Fire!");
        // Note: feature branch didn't call attack() here, but set readyToShoot = true.
        // If the system expects immediate attack, I might need: this.attack(List.of(target));
        // But adhering to feature branch strict logic for now.
      }
    }
  }

  private void handleSearchLogic(float deltaTime) {
    float distToLastPos = getPosition().dst(lastKnownPlayerPos);

    if (distToLastPos > 15f) {
      // Si muove per ricercare
      moveTowards(lastKnownPlayerPos, deltaTime);
    } else {
      if (body != null) body.setLinearVelocity(0, 0);
    }
  }

  public boolean isReadyToShoot() {
    return readyToShoot;
  }

  public void resetShot() {
    this.readyToShoot = false;
  }

  private void syncBody() {
    if (body != null) {
      this.position.set(body.getPosition());
    }
  }
}
