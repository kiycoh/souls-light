package io.github.soulslight.model.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.model.combat.AttackStrategy;
import io.github.soulslight.model.combat.MageAttack;
import io.github.soulslight.model.combat.WarriorAttack;
import io.github.soulslight.model.entities.Player;
import java.util.ArrayList;
import java.util.List;

public class Oblivion extends AbstractEnemy {

    private enum State {
        CHASING,
        ATTACKING,
        RETREATING,
        CASTING
    }

    private State currentState = State.CHASING;
    private boolean isPhaseTwo = false;

    private float teleportTimer = 0f;
    private float shootTimer = 0f;
    private float retreatTimer = 0f;

    private float attackCooldown = 0f;

    private final List<Vector2> shotTargets = new ArrayList<>();

    // Servono per non far teletrasportare il boss fuori dalla mappa
    private float mapWidthBoundary = 0f;
    private float mapHeightBoundary = 0f;

    private static final float PHASE_1_HP = 800f;
    private static final float PHASE_2_HP = 1500f;

    private static final float TELEPORT_COOLDOWN = 5.0f;
    private static final float TELEPORT_OFFSET = 120f; // Distanza del teletrasporto

    private static final float SHOOT_COOLDOWN = 0.5f;
    private static final float STOP_DISTANCE = 110f;
    private static final float BOSS_ATTACK_RANGE = 120f;
    private static final float BOSS_DAMAGE = 40f;
    private static final float RETREAT_DURATION = 1.5f;

    private AttackStrategy meleeStrategy;

    public Oblivion() {
        super();
        setupStats();
    }

    private void setupStats() {
        this.speed = 120f;
        this.health = PHASE_1_HP;
        this.maxHealth = PHASE_1_HP;
        this.meleeStrategy = new WarriorAttack(45);
    }

    public Oblivion(Oblivion target) {
        super(target);
        this.isPhaseTwo = target.isPhaseTwo;
        this.teleportTimer = target.teleportTimer;
        this.meleeStrategy = target.meleeStrategy;
        // Copia i confini nel clone
        this.mapWidthBoundary = target.mapWidthBoundary;
        this.mapHeightBoundary = target.mapHeightBoundary;
    }

    @Override
    public AbstractEnemy clone() {
        return new Oblivion(this);
    }

    // setta i confini della mappa
    public void setMapBounds(float width, float height) {
        this.mapWidthBoundary = width;
        this.mapHeightBoundary = height;
    }

    @Override
    public void updateBehavior(List<Player> players, float deltaTime) {
        if (players.isEmpty()) return;
        if (isPhaseTwo && this.health <= 0) return;
        if (!isPhaseTwo && this.health <= 0) {
            startPhaseTwo();
            return;
        }

        Player target = getNearestTarget(players);
        if (target == null) return;

        // Feature Logic: RoomIdleState check
        if (getCurrentState() instanceof io.github.soulslight.model.enemies.ai.RoomIdleState) {
            getCurrentState().update(this, players, deltaTime);
            return;
        }
        Vector2 myPos = (body != null) ? body.getPosition() : this.position;
        float distance = myPos.dst(target.getPosition());

        teleportTimer += deltaTime;
        if (attackCooldown > 0) attackCooldown -= deltaTime;
        if (retreatTimer > 0) retreatTimer -= deltaTime;
        if (shootTimer > 0) shootTimer -= deltaTime;

        if (teleportTimer >= TELEPORT_COOLDOWN) {
            teleportToPlayer(target);
            teleportTimer = 0;
            currentState = State.CHASING;
            this.attackStrategy = new WarriorAttack(45);
            return;
        }

        // In fase due attacca anche a distanza
        if (isPhaseTwo && distance > 220f) {
            if (currentState != State.CASTING) {
                currentState = State.CASTING;
                this.attackStrategy = new MageAttack(45);
            }
        } else if (currentState == State.CASTING && distance <= 220f) {
            currentState = State.CHASING;
            this.attackStrategy = new WarriorAttack(45);
        }

        switch (currentState) {
            case CASTING:
                if (body != null) body.setLinearVelocity(0, 0);
                if (shootTimer <= 0) {
                    prepareTripleShot(target.getPosition());
                    // readyToShoot = true; // Removed
                    for (Vector2 t : shotTargets) {
                        notifyProjectileRequest(getPosition(), t, "fireball"); // Assuming fireball or default
                    }
                    shootTimer = SHOOT_COOLDOWN;
                }
                break;

            case CHASING:
                if (distance > STOP_DISTANCE) {
                    moveTowards(target.getPosition(), deltaTime);
                } else {
                    if (body != null) body.setLinearVelocity(0, 0);
                    currentState = State.ATTACKING;
                    attackCooldown = 0.3f;
                }
                break;

            case ATTACKING:
                if (body != null) body.setLinearVelocity(0, 0);
                if (attackCooldown <= 0) {
                    performBossMeleeAttack(target);
                    currentState = State.RETREATING;
                    retreatTimer = RETREAT_DURATION;
                    attackCooldown = 0f;
                } else if (distance > BOSS_ATTACK_RANGE + 20f) {
                    currentState = State.CHASING;
                }
                break;

            case RETREATING:
                moveAway(target.getPosition());
                if (retreatTimer <= 0) currentState = State.CHASING;
                break;
        }
    }

    private void performBossMeleeAttack(Player target) {
        float dist = this.getPosition().dst(target.getPosition());
        if (dist <= BOSS_ATTACK_RANGE) {
            target.takeDamage(BOSS_DAMAGE);
            if (target.getBody() != null) {
                Vector2 knockDir = target.getPosition().cpy().sub(this.getPosition()).nor();
                target.applyKnockback(knockDir, 600f, 0.2f);
            }
        }
    }

    private void startPhaseTwo() {
        this.isPhaseTwo = true;
        this.health = PHASE_2_HP;
        this.maxHealth = PHASE_2_HP;
        this.speed = 170f;
        this.isDead = false;
        this.currentState = State.CHASING;
    }

    // Logica del teletrasporto a destra e a sinistra del player
    private void teleportToPlayer(Player target) {

        float direction = MathUtils.randomBoolean() ? 1 : -1;
        float newX = target.getX() + (direction * TELEPORT_OFFSET);

        float newY = target.getY();

        // Serve per stare entro i limiti della mappa
        if (mapWidthBoundary > 0 && mapHeightBoundary > 0) {
            float margin = 60f;
            newX = MathUtils.clamp(newX, margin, mapWidthBoundary - margin);
            newY = MathUtils.clamp(newY, margin, mapHeightBoundary - margin);
        }

        if (body != null) {
            body.setTransform(newX, newY, body.getAngle());
            body.setLinearVelocity(0, 0);
            this.position.set(newX, newY);
        } else {
            this.setPosition(newX, newY);
        }

        currentState = State.CHASING;
        System.out.println("OBLIVION: Teleported Horizontal (Dir: " + direction + ")");
    }

    @Override
    public void moveAway(Vector2 targetPos) {
        if (body != null) {
            Vector2 direction = body.getPosition().cpy().sub(targetPos);
            direction.nor();
            body.setLinearVelocity(direction.scl(speed));
            this.position.set(body.getPosition());
        }
    }

    // Spara attacchi a distanza in tre direzioni
    private void prepareTripleShot(Vector2 playerPos) {
        shotTargets.clear();
        Vector2 myPos = getPosition();
        Vector2 mainDir = playerPos.cpy().sub(myPos);
        shotTargets.add(myPos.cpy().add(mainDir));
        Vector2 leftDir = mainDir.cpy().rotateDeg(20);
        shotTargets.add(myPos.cpy().add(leftDir));
        Vector2 rightDir = mainDir.cpy().rotateDeg(-20);
        shotTargets.add(myPos.cpy().add(rightDir));
    }

    public boolean isPhaseTwo() {
        return isPhaseTwo;
    }
}
