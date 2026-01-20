package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;
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
    if (this.health <= 0) return; // Removed isDead() check if isDead flag is not consistent, relying on health

    // se non ci sono ranger da proteggere si sacrifica
    if (getRangerToProtect() <= 0) {
      this.health = 0; // Kills itself
      return;
    }

    if (players == null || players.isEmpty()) return;
    Player player = players.get(0);

    syncBody();

    // Trova il ranger piu vicino
    AbstractEnemy vip = findNearestRanger();
    if (vip == null) return; // Caso limite

    boolean canSee = canSeePlayer(player, body.getWorld());

    if (canSee) {
      // Se ci vede si mette in mezzo e ci respinge
      blockLineOfFire(player, vip, deltaTime);
      
      // Check for shield bash if close enough (Merged from HEAD logic idea, or feature branch logic?)
      // Feature branch didn't seem to have explicit attack call in updateBehavior, 
      // but maybe blockLineOfFire handles position and collision deals damage via ContactListener?
      // HEAD had: this.attack(player) if close.
      // Feature branch logic only handles movement.
      // I'll stick to Feature branch movement logic. If attack is needed, it might be contact based or separate.
      
    } else {
      // Se non ci vede cammina insieme al ranger da proteggere
      moveToFormation(vip, deltaTime);
    }
  }

  private void blockLineOfFire(Player player, AbstractEnemy vip, float deltaTime) {
    // Calcola il punto dove deve stare
    Vector2 protectionPoint = calculateInterceptionPoint(player.getPosition(), vip.getPosition());

    float distToTarget = getPosition().dst(protectionPoint);

    if (distToTarget > 5.0f) {
      // Se lontano dal punto di protezione ci va
      moveTowards(protectionPoint, deltaTime);
    } else {
      if (body != null) body.setLinearVelocity(0, 0);
    }
  }

  private void moveToFormation(AbstractEnemy vip, float deltaTime) {
    float distToVip = getPosition().dst(vip.getPosition());

    // Cerca di stare a GUARD_DISTANCE dal Ranger
    if (distToVip > GUARD_DISTANCE + 10f) {
      moveTowards(vip.getPosition(), deltaTime);
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
      if (ally.health > 0 && ally instanceof Ranger) count++;
    }
    return count;
  }

  private AbstractEnemy findNearestRanger() {
    AbstractEnemy nearest = null;
    float minDistance = Float.MAX_VALUE;

    if (knownAllies == null) return null;

    for (AbstractEnemy ally : knownAllies) {
      // Protegge solo i Ranger
      if (ally.health <= 0 || !(ally instanceof Ranger)) continue;

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