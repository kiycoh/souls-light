package io.github.soulslight.model;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Pattern: Prototype (via Cloneable) & Strategy (via AttackStrategy). Base class for all enemies.
 */
public abstract class Enemy extends Entity implements Cloneable {

  protected float speed;
  protected transient TextureRegion textureRegion;
  protected transient Body body; // Box2D Body

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
      // Body is NOT cloned, must be created in new world
    }
  }

  public void createBody(World world) {
      BodyDef bdef = new BodyDef();
      bdef.position.set(this.position.x / Constants.PPM, this.position.y / Constants.PPM);
      bdef.type = BodyDef.BodyType.DynamicBody;
      bdef.fixedRotation = true;
      bdef.linearDamping = 10f;
      
      this.body = world.createBody(bdef);
      
      CircleShape shape = new CircleShape();
      shape.setRadius(0.4f); // 0.4m radius (approx 12 pixels radius at PPM 32)
      
      FixtureDef fdef = new FixtureDef();
      fdef.shape = shape;
      fdef.filter.categoryBits = Constants.BIT_ENEMY;
      fdef.filter.maskBits = Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY;
      
      this.body.createFixture(fdef);
      shape.dispose();
  }

  /** Updates the enemy behavior. Pattern: Template Method (Hook for specific behaviors). */
  public abstract void update(Player player, float deltaTime);

  public void draw(SpriteBatch batch) {
    if (textureRegion != null) {
      // Draw at Body Position (Meters) converted to Pixels
      float x = (getX() * Constants.PPM) - (textureRegion.getRegionWidth() / 2f);
      float y = (getY() * Constants.PPM) - (textureRegion.getRegionHeight() / 2f);
      batch.draw(textureRegion, x, y);
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
    // TargetPos is in Meters (Player Body Position)
    // My Position should be in Meters (Body Position)
    
    // Sync position with body if body exists
    if (body != null) {
        this.position.set(body.getPosition());
        
        Vector2 direction = targetPos.cpy().sub(this.position);
        direction.nor();
        
        // Speed is likely in Pixels/sec (e.g. 80). Convert to Meters/sec
        float speedMeters = this.speed / Constants.PPM;
        
        body.setLinearVelocity(direction.scl(speedMeters));
    } else {
        // Fallback for non-physics tests
        Vector2 direction = targetPos.cpy().sub(this.getPosition());
        direction.nor();
        this.position.mulAdd(direction, this.speed * deltaTime); // Here speed is pixels/sec, pos is pixels
    }
  }
  
  // Method to sync body position back to Entity state (called in update)
  protected void syncBody() {
      if (body != null) {
          this.position.set(body.getPosition());
      }
  }
  
  @Override
  public void setPosition(float x, float y) {
      super.setPosition(x, y);
      if (body != null) {
          // If setting position manually (e.g. teleport), update body
          // Assuming x,y are in METERS if we are in physics mode, or PIXELS?
          // To be safe: setPosition is usually world coords.
          // Let's assume input is same units as current state. 
          // If body exists, we work in Meters.
           body.setTransform(x, y, body.getAngle());
      }
  }
  
  public Body getBody() {
      return body;
  }
}
