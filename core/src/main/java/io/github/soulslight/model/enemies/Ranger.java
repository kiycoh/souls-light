package io.github.soulslight.model.enemies;

import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.model.combat.ArcherAttack;
import io.github.soulslight.model.combat.AttackStrategy;
import io.github.soulslight.model.entities.Player;
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

    Player target = getNearestTarget(players);
    if (target == null) return;

    // Feature Logic: RoomIdleState check
    if (getCurrentState() instanceof io.github.soulslight.model.enemies.ai.RoomIdleState) {
      getCurrentState().update(this, players, deltaTime);
      return;
    }

    syncBody();

    // gestione timer
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
      // Troppo vicino: Scappa in modo intelligente
      smartFlee(target.getPosition(), deltaTime);
    } else if (distance > ATTACK_RANGE - 50f) {
      // Troppo lontano: cerca di avvicinarsi
      smartMoveTowards(target.getPosition(), deltaTime);
    } else {
      // Distanza perfetta:si ferma
      if (body != null) body.setLinearVelocity(0, 0);
    }

    // Se i giocatori sono entro il range attacca
    if (distance <= ATTACK_RANGE) {
      if (attackTimer <= 0) {
        // readyToShoot = true; // Removed flag
        attackTimer = 2.0f; // Cooldown
        System.out.println("RANGER: Fire!");

        if (target != null) {
          notifyProjectileRequest(getPosition(), target.getPosition(), "enemy_arrow");
        }
      }
    }
  }

  private void smartFlee(Vector2 threatPos, float deltaTime) {
    if (body == null) return;

    io.github.soulslight.manager.PathfindingManager pfm =
        io.github.soulslight.manager.GameManager.getInstance().getPathfindingManager();

    // Fallback if pathfinding is not available
    if (pfm == null) {
      moveAway(threatPos);
      return;
    }

    Vector2 start = body.getPosition();
    Vector2 awayDir = start.cpy().sub(threatPos).nor();

    // Try multiple directions to find a valid spot
    float[] angles = {0, 45, -45, 90, -90, 135, -135};
    float checkDist = 150f; // Distance to run to

    for (float angle : angles) {
      Vector2 candidateDir = awayDir.cpy().rotateDeg(angle);
      Vector2 targetPos = start.cpy().add(candidateDir.scl(checkDist));

      if (pfm.isWalkable(targetPos.x, targetPos.y)) {
        // Found a valid spot!
        smartMoveTowards(targetPos, deltaTime);
        return;
      }
    }

    // If no valid spot found (trapped?), just run away as fallback
    moveAway(threatPos);
  }

  private void handleSearchLogic(float deltaTime) {
    float distToLastPos = getPosition().dst(lastKnownPlayerPos);

    if (distToLastPos > 15f) {
      // Si muove per ricercare
      smartMoveTowards(lastKnownPlayerPos, deltaTime);
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
