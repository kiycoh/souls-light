package io.github.soulslight.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

/**
 * Pattern: Prototype (via Cloneable) & Strategy (via AttackStrategy). Base class for all enemies.
 */
public abstract class Enemy extends Entity implements Cloneable {

  protected float speed;
  protected transient TextureRegion textureRegion;

  public Enemy() {
    super();
  }

  public Enemy(Enemy target) {
    if (target != null) {
      if (target.getPosition() != null) {
        this.position = new Vector2(target.getPosition());
      }
      this.health = target.getHealth();
      this.attackStrategy = target.attackStrategy;
      this.speed = target.speed;
      this.textureRegion = target.textureRegion;
    }
  }

  /** Updates the enemy behavior. Pattern: Template Method (Hook for specific behaviors). */
  public abstract void update(Player player, float deltaTime);

  public void draw(SpriteBatch batch) {
    if (textureRegion != null) {
      batch.draw(textureRegion, getX(), getY());
    }
  }

  public void setTextureRegion(TextureRegion region) {
    this.textureRegion = region;
  }

  public float getDamage() {
    return (attackStrategy != null) ? attackStrategy.getDamage() : 0;
  }

  public void attack(Player player) {
    if (this.attackStrategy == null) return;
    List<Entity> targets = new ArrayList<>();
    targets.add(player);
    this.attackStrategy.executeAttack(this, targets);
  }

  /** Helper for subclasses to attack a list (legacy support or multi-player). */
  protected void attack(List<Player> players) {
    if (this.attackStrategy == null) return;
    List<Entity> targets = new ArrayList<>(players);
    this.attackStrategy.executeAttack(this, targets);
  }

  @Override
  public abstract Enemy clone();

  public void moveTowards(Vector2 targetPos, float deltaTime) {
    Vector2 direction = targetPos.cpy().sub(this.getPosition());
    direction.nor();
    this.position.mulAdd(direction, this.speed * deltaTime);
  }
}
