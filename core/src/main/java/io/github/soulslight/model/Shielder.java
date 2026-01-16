package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class Shielder extends Enemy {

  // List of allies to know who to defend
  private List<Enemy> knownAllies;

  public Shielder() {
    super();
    this.health = 300; // Very resistant (Tank)
    this.speed = 40.0f; // Quite slow

    // Assign the strategy that pushes away
    this.attackStrategy = new ShielderAttack();
  }

  // Copy constructor for the Prototype Pattern
  private Shielder(Shielder other) {
    super(other);
    // knownAllies is not copied, must be set at every frame or spawn
  }

  @Override
  public Enemy clone() {
    return new Shielder(this);
  }

  // Call in GameScreen: shielder.setAllies(activeEnemies);
  public void setAllies(List<Enemy> allies) {
    this.knownAllies = allies;
  }

  @Override
  public void update(Player player, float deltaTime) {
    // --- DEATH LOGIC (Suicide) ---
    // If it has no list or if it's the only one in the list -> Dies
    if (knownAllies == null || knownAllies.size() <= 1) {
      this.takeDamage(this.health); // Kills itself
      return;
    }

    if (player == null) return;

    // --- ATTACK LOGIC (Shield Bash) ---
    // If the player is too close, stops moving and pushes them away
    float distToPlayer = this.getPosition().dst(player.getPosition());
    if (distToPlayer <= this.attackStrategy.getRange()) {
      this.attack(player);
      return; // to stand still while attacking, else comment out pls
    }

    // --- MOVEMENT LOGIC (Protection) ---
    // Finds the most vulnerable enemy to protect
    Enemy vip = findProtectee(player.getPosition());

    if (vip != null) {
      // Calculates the exact point BETWEEN the Player and the Ally
      Vector2 protectPos = calculateInterceptionPoint(player.getPosition(), vip.getPosition());
      moveTowards(protectPos, deltaTime);
    } else {
      // If no one is found (rare case if suicide check works), goes towards the player
      moveTowards(player.getPosition(), deltaTime);
    }
  }

  // Looks for the ally closest to the Player that is not myself.
  private Enemy findProtectee(Vector2 playerPos) {
    Enemy bestCandidate = null;
    float minDistance = Float.MAX_VALUE;

    for (Enemy ally : knownAllies) {
      if (ally == this) continue; // Do not protect itself
      if (ally instanceof Shielder) continue; // Shielders do not protect each other

      float dist = ally.getPosition().dst(playerPos);
      if (dist < minDistance) {
        minDistance = dist;
        bestCandidate = ally;
      }
    }
    return bestCandidate;
  }

  // Calculates guard position: 40 pixels in front of the ally, towards the enemy.
  private Vector2 calculateInterceptionPoint(Vector2 playerPos, Vector2 allyPos) {
    // Direction vector: From Ally -> To Player
    Vector2 direction = playerPos.cpy().sub(allyPos);
    direction.nor();

    float shieldOffset = 40.0f; // Distance from ally

    // Final position = Ally + (Direction * 40)
    return allyPos.cpy().mulAdd(direction, shieldOffset);
  }
}
