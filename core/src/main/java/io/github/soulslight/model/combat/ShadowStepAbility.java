package io.github.soulslight.model.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.Player;
import java.util.List;

public class ShadowStepAbility implements SpecialAbilityStrategy {

  private static final float COOLDOWN = 4.0f;
  private static final float DAMAGE = 50f;

  @Override
  public void execute(Player player, List<AbstractEnemy> enemies) {
    AbstractEnemy target = findNearest(player, enemies);
    if (target == null) {
      Gdx.app.log("Thief", "No target for Shadow Step!");
      return;
    }

    // Teleport behind enemy
    Vector2 enemyPos = target.getPosition();
    Vector2 dir = enemyPos.cpy().sub(player.getPosition()).nor();
    Vector2 behindPos = enemyPos.cpy().add(dir.scl(30f)); // 30 units behind

    // Teleport
    player.setPosition(behindPos.x, behindPos.y);
    if (player.getBody() != null) {
      player.getBody().setTransform(behindPos, player.getBody().getAngle());
    }

    // Deal Backstab Damage
    target.takeDamage(DAMAGE);
    Gdx.app.log("Thief", "Shadow Step Backstab!");
  }

  private AbstractEnemy findNearest(Player player, List<AbstractEnemy> enemies) {
    AbstractEnemy nearest = null;
    float minDst = 200f; // Limit range of teleport
    for (AbstractEnemy e : enemies) {
      if (e.isDead()) continue;
      float dst = player.getPosition().dst(e.getPosition());
      if (dst < minDst) {
        minDst = dst;
        nearest = e;
      }
    }
    return nearest;
  }

  @Override
  public float getCooldown() {
    return COOLDOWN;
  }

  @Override
  public String getName() {
    return "Shadow Step";
  }
}
