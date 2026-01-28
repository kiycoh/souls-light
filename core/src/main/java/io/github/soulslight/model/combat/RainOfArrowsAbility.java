package io.github.soulslight.model.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.Player;
import java.util.List;

public class RainOfArrowsAbility implements SpecialAbilityStrategy {

  private static final float COOLDOWN = 6.0f;

  @Override
  public void execute(Player player, List<AbstractEnemy> enemies) {
    // Fires 5 arrows in a spread arc
    Vector2 origin = player.getPosition();

    AbstractEnemy nearest = findNearest(player, enemies);
    Vector2 baseDir = new Vector2(1, 0);
    if (nearest != null) {
      baseDir = nearest.getPosition().cpy().sub(origin).nor();
    }

    int arrows = 5;
    float spreadAngle = 45f;
    float startAngle = -spreadAngle / 2f;
    float usageAngle = spreadAngle / (arrows - 1);

    for (int i = 0; i < arrows; i++) {
      float angle = startAngle + (i * usageAngle);
      Vector2 dir = baseDir.cpy().rotateDeg(angle);
      // Target point far away in that direction
      Vector2 target = origin.cpy().add(dir.scl(300f));

      player.notifyProjectileRequest(origin, target, "arrow", 10f);
    }

    Gdx.app.log("Archer", "Rain of Arrows unleashed!");
  }

  private AbstractEnemy findNearest(Player player, List<AbstractEnemy> enemies) {
    AbstractEnemy nearest = null;
    float minDst = Float.MAX_VALUE;
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
    return "Rain of Arrows";
  }
}
