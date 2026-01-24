package io.github.soulslight.model.combat;

import com.badlogic.gdx.Gdx;

/** Pattern: Strategy (Concrete Strategy) Implements a specific attack behavior (Thief). */
public class ThiefAttack extends AbstractAttack {
  @Override
  public void attack() {
    Gdx.app.log("ThiefAttack", "Attack executed");
  }

  private final float damage;

  public ThiefAttack(float damage) {
    this.damage = damage;
  }

  @Override
  public float getRange() {
    return 0.8f;
  }

  @Override
  public float getDamage() {
    return this.damage;
  }

  @Override
  public float getAttackSpeed() {
    return 2.0f;
  }

  @Override
  public String getSoundID() {
    return "dagger_sound";
  }

  // Shadow Step Configuration
  private static final float SHADOW_STEP_RANGE = 150f; // Range to find target to jump to
  private static final float TELEPORT_OFFSET = 25f; // Distance behind target

  @Override
  public void executeAttack(
      io.github.soulslight.model.entities.Entity attacker,
      java.util.List<io.github.soulslight.model.entities.Entity> targets) {
    if (attacker instanceof io.github.soulslight.model.entities.Player player) {
      io.github.soulslight.model.entities.Entity nearest = null;
      float minDst = SHADOW_STEP_RANGE;

      for (io.github.soulslight.model.entities.Entity target : targets) {
        float dst = attacker.getPosition().dst(target.getPosition());
        if (dst < minDst) {
          minDst = dst;
          nearest = target;
        }
      }

      if (nearest != null) {
        // Calculate position behind: TargetPos + (TargetPos - PlayerPos).nor() * OFFSET
        com.badlogic.gdx.math.Vector2 dir =
            nearest.getPosition().cpy().sub(attacker.getPosition()).nor();
        com.badlogic.gdx.math.Vector2 teleportPos =
            nearest.getPosition().cpy().add(dir.scl(TELEPORT_OFFSET));

        player.setPosition(teleportPos.x, teleportPos.y);
        Gdx.app.log("ShadowStep", "Teleported behind enemy!");
      }
    }
    super.executeAttack(attacker, targets);
  }
}
