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
        CASTING,
        TELEPORT_OUT,
        TELEPORT_IN
    }

    private State currentState = State.CHASING;
    private boolean isPhaseTwo = false;

    private float teleportTimer = 0f;
    private float shootTimer = 0f;
    private float retreatTimer = 0f;

    // Timer interno per l'attacco melee (windup + swing)
    private float attackTimer = 0f;
    private boolean hasDealtMeleeDamage = false;

    // cooldown tra un melee e l'altro
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

    private static final float MELEE_WINDUP_TIME = 0.3f;
    private static final float MELEE_ATTACK_TIME = 0.4f;
    private static final float MELEE_COOLDOWN = 1.0f; // tempo minimo tra due melee completi

    private static final float TELEPORT_ANIM_DURATION = 1.8f;

    private float teleportAnimTime = 0f;

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

    // logica principale del boss
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

        if (currentState == State.TELEPORT_OUT || currentState == State.TELEPORT_IN) {
            // sta fermo durante il teleport
            if (body != null) body.setLinearVelocity(0, 0);

            teleportAnimTime += deltaTime;

            if (teleportAnimTime >= TELEPORT_ANIM_DURATION) {
                if (currentState == State.TELEPORT_OUT) {
                    teleportToPlayer(target);
                    teleportAnimTime = 0f;
                    currentState = State.TELEPORT_IN;
                    this.attackStrategy = new WarriorAttack(45);
                } else {
                    teleportAnimTime = 0f;
                    currentState = State.CHASING;
                }
            }

            return;
        }

        Vector2 myPos = (body != null) ? body.getPosition() : this.position;
        float distance = myPos.dst(target.getPosition());

        teleportTimer += deltaTime;
        if (attackCooldown > 0) attackCooldown -= deltaTime;
        if (retreatTimer > 0) retreatTimer -= deltaTime;
        if (shootTimer > 0) shootTimer -= deltaTime;

        if (teleportTimer >= TELEPORT_COOLDOWN) {
            startTeleportOut();
            teleportTimer = 0f;
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
                    for (Vector2 t : shotTargets) {
                        notifyProjectileRequest(getPosition(), t, "fireball");
                    }
                    shootTimer = SHOOT_COOLDOWN;
                }
                break;

            case CHASING:
                if (distance > STOP_DISTANCE) {
                    // troppo lontano: avvicinati
                    moveTowards(target.getPosition(), deltaTime);
                } else {
                    // sei in range per il melee, ma solo se il cooldown è finito
                    if (attackCooldown <= 0f) {
                        if (body != null) body.setLinearVelocity(0, 0);
                        currentState = State.ATTACKING;
                        attackTimer = 0f;
                        hasDealtMeleeDamage = false;
                    } else {
                        // aspetta fermo finché il cooldown non scende a 0
                        if (body != null) body.setLinearVelocity(0, 0);
                    }
                }
                break;

            case ATTACKING:
                if (body != null) body.setLinearVelocity(0, 0);
                attackTimer += deltaTime;

                // qui parte il colpo (inizio fase swing)
                if (!hasDealtMeleeDamage && attackTimer >= MELEE_WINDUP_TIME) {
                    performBossMeleeAttack(target);
                    hasDealtMeleeDamage = true;
                }

                // fine animazione melee -> passa a RETREATING e setta cooldown
                if (attackTimer >= MELEE_WINDUP_TIME + MELEE_ATTACK_TIME) {
                    currentState = State.RETREATING;
                    retreatTimer = RETREAT_DURATION;
                    attackCooldown = MELEE_COOLDOWN;
                } else {
                    // se il bersaglio scappa fuori range, consideriamo l'attacco "whiffato"
                    float distNow = getPosition().dst(target.getPosition());
                    if (distNow > BOSS_ATTACK_RANGE + 40f) {
                        currentState = State.RETREATING;
                        retreatTimer = RETREAT_DURATION;
                        attackCooldown = MELEE_COOLDOWN;
                    }
                }
                break;

            case RETREATING:
                moveAway(target.getPosition());
                if (retreatTimer <= 0) currentState = State.CHASING;
                break;
        }
    }

    private void startTeleportOut() {
        currentState = State.TELEPORT_OUT;
        teleportAnimTime = 0f;
        if (body != null) {
            body.setLinearVelocity(0, 0);
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
        isPhaseTwo = true;
        this.health = PHASE_2_HP;
        this.maxHealth = PHASE_2_HP;
        this.attackStrategy = new MageAttack(45);
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
            this.position.set(newX, newY);
        }
    }

    // deve rimanere public per non rompere l'override
    public void moveTowards(Vector2 targetPos, float deltaTime) {
        if (body != null) {
            Vector2 direction = targetPos.cpy().sub(body.getPosition()).nor();
            body.setLinearVelocity(direction.scl(speed));
            this.position.set(body.getPosition());
        } else {
            Vector2 direction = targetPos.cpy().sub(position).nor();
            this.position.add(direction.scl(speed * deltaTime));
        }
    }

    @Override
    public void moveAway(Vector2 targetPos) {
        if (body != null) {
            Vector2 direction = body.getPosition().cpy().sub(targetPos);
            direction.nor();
            body.setLinearVelocity(direction.scl(speed));
            this.position.set(body.getPosition());
        } else {
            Vector2 direction = position.cpy().sub(targetPos).nor();
            this.position.add(direction.scl(speed * 0.016f));
        }
    }

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

    // --- Getter usati dal rendering per scegliere le animazioni ---
    public boolean isMeleeWindup() {
        return currentState == State.ATTACKING && attackTimer < MELEE_WINDUP_TIME;
    }

    public boolean isMeleeAttacking() {
        return currentState == State.ATTACKING
            && attackTimer >= MELEE_WINDUP_TIME
            && attackTimer < MELEE_WINDUP_TIME + MELEE_ATTACK_TIME;
    }

    public boolean isTeleportingOut() {
        return currentState == State.TELEPORT_OUT;
    }

    public boolean isTeleportingIn() {
        return currentState == State.TELEPORT_IN;
    }

    public float getTeleportAnimTime() {
        return teleportAnimTime;
    }

    public static float getTeleportAnimDuration() {
        return TELEPORT_ANIM_DURATION;
    }

    public boolean isPhaseTwo() {
        return isPhaseTwo;
    }
}
