package io.github.soulslight.model.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.Player;
import java.util.List;

public class EarthquakeAbility implements SpecialAbilityStrategy {

  private static final float RANGE = 250f;
  private static final float DAMAGE = 60f;
  private static final float COOLDOWN = 5.0f;
  private static final float KNOCKBACK_FORCE = 500f;

  @Override
  public void execute(Player player, List<AbstractEnemy> enemies) {
    if (enemies == null) return;

    boolean hitAny = false;
    for (AbstractEnemy enemy : enemies) {
      if (enemy.isDead()) continue;
      if (player.getPosition().dst(enemy.getPosition()) < RANGE) {
        enemy.takeDamage(DAMAGE);
        if (enemy.getBody() != null) {
          Vector2 knock = enemy.getPosition().cpy().sub(player.getPosition()).nor();
          enemy.applyKnockback(knock, KNOCKBACK_FORCE, 0.5f);
        }
        hitAny = true;
      }
    }

    if (hitAny) {
      Gdx.app.log("Mage", "Earthquake hit enemies!");
    } else {
      Gdx.app.log("Mage", "Earthquake missed!");
    }
  }

  @Override
  public float getCooldown() {
    return COOLDOWN;
  }

  @Override
  public String getName() {
    return "Arcane Blast";
  }
}
