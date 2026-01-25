package io.github.soulslight.model.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Projectile extends Entity {

  private float lifeTime = 10.0f; // Durata della freccia prima di sparire
  private boolean destroy = false;
  private final float damage = 15f;

  // Memorizza la posizione precedente per calcolare la traiettoria
  private final Vector2 lastPosition = new Vector2();
  private boolean isPlayerProjectile = false;

  public Projectile(World world, float x, float y, Vector2 targetPos, boolean isPlayerProjectile) {
    super();
    this.position = new Vector2(x, y);
    this.lastPosition.set(x, y); // Inizializza la posizione precedente
    this.isPlayerProjectile = isPlayerProjectile;

    // Calcola direzione
    Vector2 direction = new Vector2(targetPos).sub(x, y).nor();
    float speed = 400f;

    createBody(world, x, y);

    this.body.setLinearVelocity(direction.scl(speed));
    this.body.setTransform(this.body.getPosition(), direction.angleRad());
  }

  // Costruttore per compatibilità (default nemici)
  public Projectile(World world, float x, float y, Vector2 targetPos) {
    this(world, x, y, targetPos, false);
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

    fdef.filter.categoryBits = 0x0002; // Categoria proiettile
    fdef.filter.maskBits = 0x0001; // Colpisce solo categoria solitamente i muri/player

    this.body.createFixture(fdef);
    this.body.setUserData(this);
    shape.dispose();
  }

  @Override
  public void update(float delta) {

    lastPosition.set(this.position);

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
