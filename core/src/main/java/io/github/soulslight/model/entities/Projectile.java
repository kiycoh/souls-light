package io.github.soulslight.model.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Projectile extends Entity {

  private float lifeTime = 10.0f; // Durata della freccia prima di sparire
  private boolean destroy = false;
  private final float damage;

  // Memorizza la posizione precedente per calcolare la traiettoria
  private final Vector2 lastPosition = new Vector2();
  private boolean isPlayerProjectile = false;
  private io.github.soulslight.model.entities.Entity target; // For homing projectiles

  // Compatibility Constructor 1
  public Projectile(World world, float x, float y, Vector2 targetPos, boolean isPlayerProjectile) {
    this(world, x, y, targetPos, isPlayerProjectile, null, 400f, 15f);
  }

  // Compatibility Constructor 2
  public Projectile(
      World world,
      float x,
      float y,
      Vector2 targetPos,
      boolean isPlayerProjectile,
      io.github.soulslight.model.entities.Entity target) {
    this(world, x, y, targetPos, isPlayerProjectile, target, 400f, 15f);
  }

  // Compatibility Constructor 3: Speed but no damage
  public Projectile(
      World world,
      float x,
      float y,
      Vector2 targetPos,
      boolean isPlayerProjectile,
      io.github.soulslight.model.entities.Entity target,
      float speed) {
    this(world, x, y, targetPos, isPlayerProjectile, target, speed, 15f);
  }

  // Master Constructor
  public Projectile(
      World world,
      float x,
      float y,
      Vector2 targetPos,
      boolean isPlayerProjectile,
      io.github.soulslight.model.entities.Entity target,
      float speed,
      float damage) {
    super();
    this.position = new Vector2(x, y);
    this.lastPosition.set(x, y); // Inizializza la posizione precedente
    this.isPlayerProjectile = isPlayerProjectile;
    this.target = target;
    this.damage = damage;

    // Calcola direzione
    Vector2 direction = new Vector2(targetPos).sub(x, y).nor();

    createBody(world, x, y);

    this.body.setLinearVelocity(direction.scl(speed));
    this.body.setTransform(this.body.getPosition(), direction.angleRad());
  }

  // Costruttore per compatibilità (default nemici)
  public Projectile(World world, float x, float y, Vector2 targetPos) {
    this(world, x, y, targetPos, false, null, 400f, 15f);
  }

  private void createBody(World world, float x, float y) {
    BodyDef bdef = new BodyDef();
    bdef.position.set(x, y);
    bdef.type = BodyDef.BodyType.DynamicBody;
    bdef.bullet = true;

    this.body = world.createBody(bdef);

    CircleShape shape = new CircleShape();
    shape.setRadius(7f);

    FixtureDef fdef = new FixtureDef();
    fdef.shape = shape;
    fdef.isSensor = true;

    fdef.filter.categoryBits = io.github.soulslight.model.Constants.BIT_PROJECTILE;

    // Friendly Fire / Collision Logic
    if (isPlayerProjectile) {
      // Player shots hit Enemies and Walls (Ignore Player)
      fdef.filter.maskBits =
          (short)
              (io.github.soulslight.model.Constants.BIT_ENEMY
                  | io.github.soulslight.model.Constants.BIT_WALL);
    } else {
      // Enemy shots hit Players and Walls (Ignore Enemy)
      fdef.filter.maskBits =
          (short)
              (io.github.soulslight.model.Constants.BIT_PLAYER
                  | io.github.soulslight.model.Constants.BIT_WALL);
    }

    this.body.createFixture(fdef);
    this.body.setUserData(this);
    shape.dispose();
  }

  @Override
  public void update(float delta) {
    lastPosition.set(this.position);

    // Homing Logic
    if (target != null && !target.isDead() && body != null) {
      Vector2 myPos = body.getPosition();
      Vector2 targetPos = target.getPosition();
      Vector2 desiredVelocity = targetPos.cpy().sub(myPos).nor().scl(400f);

      Vector2 currentVelocity = body.getLinearVelocity();
      // Steer factor: 0.1 smooth, 1.0 instant
      float steerFactor = 0.15f;
      Vector2 newVelocity = currentVelocity.lerp(desiredVelocity, steerFactor);

      body.setLinearVelocity(newVelocity);
      body.setTransform(myPos, newVelocity.angleRad());
    }

    super.update(delta);

    lifeTime -= delta;
    if (lifeTime <= 0) destroy = true;
  }

  public boolean shouldDestroy() {
    return destroy;
  }

  public void markDestroy() {
    this.destroy = true;
  }

  public float getDamage() {
    return damage;
  }

  public float getRotation() {
    return (float) Math.toDegrees(body.getAngle());
  }

  public Vector2 getLastPosition() {
    return lastPosition;
  }

  public boolean isPlayerProjectile() {
    return isPlayerProjectile;
  }
}
