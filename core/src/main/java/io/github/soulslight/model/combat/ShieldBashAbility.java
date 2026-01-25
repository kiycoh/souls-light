package io.github.soulslight.model.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.Player;
import java.util.List;

public class ShieldBashAbility implements SpecialAbilityStrategy {

  private static final float RANGE = 60f;
  private static final float DAMAGE = 30f;
  private static final float COOLDOWN = 3.0f;
  private static final float KNOCKBACK_FORCE = 800f;

  @Override
  public void execute(Player player, List<AbstractEnemy> enemies) {
    if (enemies == null) return;

    // Shield Bash usually hits the closest enemy in front, but for simplicity let's
    // make it short-range AoE
    for (AbstractEnemy enemy : enemies) {
      if (enemy.isDead()) continue;
      if (player.getPosition().dst(enemy.getPosition()) < RANGE) {
        enemy.takeDamage(DAMAGE);
        if (enemy.getBody() != null) {
          Vector2 knock = enemy.getPosition().cpy().sub(player.getPosition()).nor();
          enemy.applyKnockback(knock, KNOCKBACK_FORCE, 0.8f); // Longer stun/knockback
        }
        Gdx.app.log("Warrior", "Shield Bash hit " + enemy.getClass().getSimpleName());
      }
    }
  }

  @Override
  public float getCooldown() {
    return COOLDOWN;
  }

  @Override
  public String getName() {
    return "Shield Bash";
  }
}
