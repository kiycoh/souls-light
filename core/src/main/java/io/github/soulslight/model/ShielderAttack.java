package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class ShielderAttack extends AbstractAttack {

  private static final float KNOCKBACK_SPEED = 10.0f;

  @Override
  public float getRange() {
    return 40.0f;
  }

  @Override
  public float getDamage() {
    return 0.0f;
  }

  @Override
  public float getAttackSpeed() {
    return 1.5f;
  }

  @Override
  public String getSoundID() {
    return "shield_bash";
  }

  @Override
  public void executeAttack(Entity attacker, List<Entity> targets) {
    if (targets.isEmpty()) return;

    Entity target = targets.get(0);
    Vector2 attackerPos =
        (attacker.getBody() != null) ? attacker.getBody().getPosition() : attacker.getPosition();
    Vector2 targetPos =
        (target.getBody() != null) ? target.getBody().getPosition() : target.getPosition();

    float dist = attackerPos.dst(targetPos);

    if (dist <= getRange() + 8f) { // Margine tolleranza
      target.takeDamage(getDamage());

      // Direzione dal nemico al player
      Vector2 knockbackDir = targetPos.cpy().sub(attackerPos);
      if (knockbackDir.len2() < 0.01f) knockbackDir.set(1, 0); // Evita errori se sono sovrapposti
      knockbackDir.nor();

      // ...
      if (target instanceof Player) {
        Player p = (Player) target;
        // L'attacco potente dello scudo deve stunnare un po' (0.25s o 0.3s)
        p.applyKnockback(knockbackDir, KNOCKBACK_SPEED, 0.25f);
      }
    }
  }

  @Override
  public void attack() {}
}
