package io.github.soulslight.model.enemies;

import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.model.entities.Player;
import java.util.List;

public class Shielder extends AbstractEnemy {

  private List<AbstractEnemy> knownAllies;
  private final float GUARD_DISTANCE = 40.0f;

  public Shielder() {
    super();
    this.health = 300;
    this.maxHealth = 300;
    this.speed = 140.0f;
  }

  public Shielder(Shielder other) {
    super(other);
  }

  @Override
  public AbstractEnemy clone() {
    return new Shielder(this);
  }

  public void setAllies(List<AbstractEnemy> allies) {
    this.knownAllies = allies;
  }

  @Override
  public void updateBehavior(List<Player> players, float deltaTime) {
    if (this.health <= 0)
      return; // Removed isDead() check if isDead flag is not consistent, relying on health

    // se non ci sono ranger da proteggere si sacrifica
    if (getRangerToProtect() <= 0) {
      this.health = 0; // Kills itself
      return;
    }

    if (players == null || players.isEmpty()) return;
    Player player = getNearestTarget(players);
    if (player == null) return;

    // Feature Logic: RoomIdleState check
    if (getCurrentState() instanceof io.github.soulslight.model.enemies.ai.RoomIdleState) {
      getCurrentState().update(this, players, deltaTime);
      return;
    }

    syncBody();

    // Trova il ranger piu vicino
    AbstractEnemy vip = findNearestRanger();
    if (vip == null) return; // Caso limite

    boolean canSee = canSeePlayer(player, body.getWorld());

    if (canSee) {
      // Se ci vede si mette in mezzo e ci respinge
      blockLineOfFire(player, vip, deltaTime);

    } else {
      // Se non ci vede cammina insieme al ranger da proteggere
      moveToFormation(vip, deltaTime);
    }
  }

  private void blockLineOfFire(Player player, AbstractEnemy vip, float deltaTime) {
    // Calcola il punto dove deve stare
    Vector2 protectionPoint = calculateInterceptionPoint(player.getPosition(), vip.getPosition());

    float distToTarget = getPosition().dst(protectionPoint);

    if (distToTarget > 15.0f || (body.getLinearVelocity().len2() > 0.1f && distToTarget > 5.0f)) {
      // Start moving if > 15, keep moving until < 5
      smartMoveTowards(protectionPoint, deltaTime);
    } else {
      if (body != null) body.setLinearVelocity(0, 0);
    }
  }

  private void moveToFormation(AbstractEnemy vip, float deltaTime) {
    float distToVip = getPosition().dst(vip.getPosition());

    // Cerca di stare a GUARD_DISTANCE dal Ranger
    if (distToVip > GUARD_DISTANCE + 10f) {
      smartMoveTowards(vip.getPosition(), deltaTime);
    } else if (distToVip < GUARD_DISTANCE - 10f) {
      // Se è troppo vicino, si sposta per non dargli fastidio
      moveAway(vip.getPosition());
    } else {
      // Distanza perfetta
      if (body != null) body.setLinearVelocity(0, 0);
    }
  }

  private Vector2 calculateInterceptionPoint(Vector2 playerPos, Vector2 allyPos) {
    // Matematica vettoriale: (Player - Ally) normalizzato = Direzione
    Vector2 direction = playerPos.cpy().sub(allyPos).nor();
    // Punto finale = Posizione Alleato + (Direzione * DistanzaScudo)
    return allyPos.cpy().mulAdd(direction, GUARD_DISTANCE);
  }

  private int getRangerToProtect() {
    if (knownAllies == null) return 0;
    int count = 0;
    for (AbstractEnemy ally : knownAllies) {
      if (ally.getHealth() > 0 && ally instanceof Ranger) count++;
    }
    return count;
  }

  private AbstractEnemy findNearestRanger() {
    AbstractEnemy nearest = null;
    float minDistance = Float.MAX_VALUE;

    if (knownAllies == null) return null;

    for (AbstractEnemy ally : knownAllies) {
      // Protegge solo i Ranger
      if (ally.getHealth() <= 0 || !(ally instanceof Ranger)) continue;

      float dist = getPosition().dst(ally.getPosition());
      if (dist < minDistance) {
        minDistance = dist;
        nearest = ally;
      }
    }
    return nearest;
  }

  private void syncBody() {
    if (body != null) {
      this.position.set(body.getPosition());
    }
  }
}
