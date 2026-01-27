package io.github.soulslight.model.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.soulslight.model.combat.ProjectileListener;
import io.github.soulslight.model.enemies.ai.EnemyState;
import io.github.soulslight.model.entities.Entity;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.room.EnemyDeathListener;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEnemy extends Entity implements Cloneable {

  protected List<ProjectileListener> projectileListeners = new ArrayList<>();

  protected Vector2 lastKnownPlayerPos =
      new Vector2(); // serve oer ricordare l'ultima posizione del player per
  // cercarlo qualora lo
  // perdesse
  protected float searchTimer = 0;
  protected final float SEARCH_DURATION = 2.0f;

  protected Vector2 spawnPoint = new Vector2(); // Per ricordare ai nemici dove sono spawnati
  protected float patrolAngle = 0; // Direzione attuale in gradi
  protected float wanderTimer = 0; // Timer per cambio direzione
  protected final float MAX_WANDER_DIST = 300f; // Raggio massimo dallo spawn
  // protected final float SENSOR_DIST = 50f;
  private EnemyState aiState;
  protected float speed;
  private List<EnemyDeathListener> deathListeners = new ArrayList<>();

  // ... (in notify section)

  protected void notifyDeathListeners() {
    for (EnemyDeathListener listener : deathListeners) {
      listener.onEnemyDied(this);
    }
  }

  /** Override to notify death listener when enemy dies. */
  @Override
  public void takeDamage(float amount) {
    super.takeDamage(amount);
    if (isDead()) {
      notifyDeathListeners();
    }
  }

  // Knockback fields
  protected float knockbackTimer = 0f;
  protected Vector2 currentKnockbackVelocity = new Vector2();
  protected boolean wasInKnockback = false;

  public AbstractEnemy() {
    super();
    this.speed = 100f;
  }

  public AbstractEnemy(World world, float x, float y) {
    super();
    this.position = new Vector2(x, y);
    this.spawnPoint.set(x, y);
    this.health = 100;
    this.maxHealth = 100;
    this.speed = 100f;
    createBody(world, x, y);
  }

  public AbstractEnemy(AbstractEnemy target) {
    super();
    if (target != null) {
      this.position = new Vector2(target.getPosition());
      this.spawnPoint.set(target.spawnPoint);
      this.health = target.health;
      this.maxHealth = target.maxHealth;
      this.speed = target.speed;
      this.attackStrategy = target.attackStrategy;
      this.textureRegion = target.textureRegion;
    }
  }

  public void createBody(World world, float x, float y) {
    BodyDef bdef = new BodyDef();
    bdef.position.set(x, y);
    bdef.type = BodyDef.BodyType.DynamicBody;
    bdef.fixedRotation = true; // serve per non fare roteare a caso i nemici
    bdef.linearDamping = 10.0f; // Attrito per evitare che i nemici slittino

    this.body = world.createBody(bdef); // crea il corpo fisico

    CircleShape shape = new CircleShape();
    shape.setRadius(
        this instanceof Oblivion
            ? 80f
            : 14f); // crea la hitbox, 80 per il boss, 14 per tutti gli altri

    FixtureDef fdef = new FixtureDef();
    fdef.shape = shape;
    fdef.density = 1.0f;
    fdef.friction = 0.0f;
    fdef.filter.categoryBits = io.github.soulslight.model.Constants.BIT_ENEMY;
    fdef.filter.maskBits =
        io.github.soulslight.model.Constants.BIT_WALL
            | io.github.soulslight.model.Constants.BIT_PLAYER
            | io.github.soulslight.model.Constants.BIT_DOOR;

    this.body.createFixture(fdef);
    this.body.setUserData(this);
    shape.dispose();

    this.position.set(x, y);
  }

  // NEW METHOD: Finds the nearest living player
  public Player getNearestTarget(List<Player> players) {
    Player nearest = null;
    float minDst = Float.MAX_VALUE;

    for (Player p : players) {
      if (p.isDead()) continue;

      float dst = this.getPosition().dst(p.getPosition());
      if (dst < minDst) {
        minDst = dst;
        nearest = p;
      }
    }
    return nearest;
  }

  // metodo per vedere se i player sono nel raggio di vista
  public boolean canSeePlayer(Player player, World world) {
    if (player == null || player.isDead() || body == null) return false;

    float aggroRange = 300f;
    float dist = this.getPosition().dst(player.getPosition());
    if (dist > aggroRange) return false;

    final boolean[] hitWall = {false};
    world.rayCast(
        (fixture, point, normal, fraction) -> {
          if (fixture.getBody().getType() == BodyDef.BodyType.StaticBody) {
            hitWall[0] = true;
            return fraction;
          }
          return 1;
        },
        this.getPosition(),
        player.getPosition());

    if (!hitWall[0]) {
      lastKnownPlayerPos.set(player.getPosition());
      return true;
    }
    return false;
  }

  // movimento
  public void moveTowards(Vector2 targetPos, float deltaTime) {
    if (body == null) return;
    Vector2 direction = targetPos.cpy().sub(body.getPosition());
    if (direction.len() > 5f) { // Deadzone per evitare tremolii
      direction.nor();
      body.setLinearVelocity(direction.scl(speed));
      this.position.set(body.getPosition());
    } else {
      body.setLinearVelocity(0, 0);
    }
  }

  // metodo per scappare
  public void moveAway(Vector2 targetPos) {
    if (body == null) return;
    Vector2 direction = body.getPosition().cpy().sub(targetPos).nor();
    body.setLinearVelocity(direction.scl(speed));
    this.position.set(body.getPosition());
  }

  // Metodo per il pattugliamento
  protected void updateWanderPatrol(float deltaTime) {
    if (body == null) return;

    wanderTimer -= deltaTime;

    // serve per non farli allontanare troppo dallo spawn
    if (getPosition().dst(spawnPoint) > MAX_WANDER_DIST) {
      patrolAngle = new Vector2(spawnPoint).sub(getPosition()).angleDeg();
      wanderTimer = 2.0f;
    } else if (wanderTimer <= 0) {
      patrolAngle = MathUtils.random(0, 360);
      wanderTimer = MathUtils.random(3.0f, 6.0f);
    }

    // Sensori per capire se stanno andano contro un muro e giare
    // Controlliamo i tre sensori separatamente per sapere da che parte girare
    boolean hitCenter = checkObstacle(0);
    boolean hitLeft = checkObstacle(40); // Sensore a sinistra
    boolean hitRight = checkObstacle(-40); // Sensore a destra

    if (hitCenter || hitLeft || hitRight) {
      // Se c'è un ostacolo, ruotiamo con più decisione (45° o più)
      if (hitLeft && !hitRight) {
        patrolAngle -= 45f; // Gira a destra se il muro è a sinistra
      } else if (hitRight && !hitLeft) {
        patrolAngle += 45f; // Gira a sinistra se il muro è a destra
      } else {
        patrolAngle += 90f;
      }

      patrolAngle %= 360;
    }

    Vector2 dir = new Vector2(1, 0).setAngleDeg(patrolAngle);
    float patrolSpeed =
        speed * 0.3f; // i nemici sono più lenti durante la fase di pattuglia e accelerano quando
    // vedono igiocatori

    body.setLinearVelocity(dir.scl(patrolSpeed));
    this.position.set(body.getPosition());
  }

  // metodo che testa effettivamente la presenza di una parete lungo il cammino
  // dei nemici
  private boolean checkObstacle(float angleOffset) {
    final boolean[] hit = {false};

    // distanza da cui verifica
    float dynamicSensorDist = 60f;

    Vector2 rayStart = body.getPosition();

    Vector2 rayEnd =
        new Vector2(1, 0) // crea un vettore unitario
            .setAngleDeg(
                patrolAngle
                    + angleOffset) // indirizza il vettore nella direzione giusta grazie all'offset
            .scl(dynamicSensorDist) // lo moltiplica per la lunghezza del sensore
            .add(rayStart); // in questo modo parte dal centro del nemico

    body.getWorld()
        .rayCast(
            (fixture, point, normal, fraction) -> {
              if (fixture.getBody().getType() == BodyDef.BodyType.StaticBody) {
                hit[0] = true;
                return fraction;
              }
              return 1;
            },
            rayStart,
            rayEnd);

    return hit[0];
  }

  // setta le coordinate di spawn per farle ricordare
  public void setSpawnPoint(float x, float y) {
    this.spawnPoint.set(x, y);
  }

  // metodo che toglie il corpo fisico dal mondo una volta morto in modo da non
  // avere sovraccarico
  // in memoria
  public void destroyBody(World world) {
    if (body != null) {
      world.destroyBody(body);
      body = null;
    }
  }

  public void setAIState(EnemyState state) {
    if (this.aiState != null) {
      this.aiState.exit(this);
    }
    this.aiState = state;
    if (this.aiState != null) {
      this.aiState.enter(this);
    }
  }

  public EnemyState getCurrentState() {
    return aiState;
  }

  public Body getBody() {
    return body;
  }

  public Vector2 getSpawnPoint() {
    return spawnPoint;
  }

  public float getSpeed() {
    return speed;
  }

  public Vector2 getLastKnownPlayerPos() {
    return lastKnownPlayerPos;
  }

  public float getSearchTimer() {
    return searchTimer;
  }

  public void setSearchTimer(float searchTimer) {
    this.searchTimer = searchTimer;
  }

  /**
   * Adds a death listener for this enemy (Observer pattern).
   *
   * @param listener The listener to notify on death
   */
  public void addDeathListener(EnemyDeathListener listener) {
    if (!deathListeners.contains(listener)) {
      deathListeners.add(listener);
    }
  }

  public void removeDeathListener(EnemyDeathListener listener) {
    deathListeners.remove(listener);
  }

  public void applyKnockback(Vector2 direction, float speedForce, float duration) {
    if (body == null) return;
    this.currentKnockbackVelocity.set(direction).nor().scl(speedForce);
    this.knockbackTimer = duration;
    // Override damping to allow sliding
    body.setLinearDamping(0f);
    body.setLinearVelocity(currentKnockbackVelocity);
  }

  @Override
  public void update(float delta) {
    if (knockbackTimer > 0) {
      knockbackTimer -= delta;
      wasInKnockback = true;
      if (body != null) {
        body.setLinearDamping(0f);
        body.setLinearVelocity(currentKnockbackVelocity);
      }
      // Skip normal behavior updates during knockback if desired,
      // but we still need super.update(delta) for animations.
      super.update(delta);
      return;
    }

    if (wasInKnockback) {
      wasInKnockback = false;
      if (body != null) {
        body.setLinearVelocity(0, 0);
        body.setLinearDamping(10.0f); // Restore normal damping
      }
    }

    super.update(delta);
  }

  public abstract void updateBehavior(List<Player> players, float deltaTime);

  @Override
  public abstract AbstractEnemy clone();

  public void attack(List<Player> players) {
    if (this.attackStrategy == null) return;
    this.attackStrategy.executeAttack(this, new ArrayList<>(players));
  }

  public void addProjectileListener(ProjectileListener listener) {
    if (!projectileListeners.contains(listener)) {
      projectileListeners.add(listener);
    }
  }

  public void removeProjectileListener(ProjectileListener listener) {
    projectileListeners.remove(listener);
  }

  protected void notifyProjectileRequest(Vector2 origin, Vector2 target, String type) {
    for (ProjectileListener listener : projectileListeners) {
      float damage = (attackStrategy != null) ? attackStrategy.getDamage() : 15f;
      listener.onProjectileRequest(origin, target, type, damage);
    }
  }
}
