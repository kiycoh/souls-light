package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class SpikedBall extends AbstractEnemy {

    // Stati
    private enum State { COOLDOWN, CHARGING }
    private State currentState;

    // Timer e Parametri
    private float stateTimer;//duarata di uno stato (carica o attacco)
    private final float COOLDOWN_TIME = 2.0f;
    private final float MAX_CHARGE_TIME = 3.0f; // Rotola per 1 secondo

    private Vector2 chargeDirection; // Non cambia direzione durante la carica

    public SpikedBall() {
        super();
        this.health = 500.0f; // Invincibile (o molto dura)
        this.speed = 300.0f; // Molto veloce! (Il Chaser era 80, questa è un missile)

        this.currentState = State.COOLDOWN;
        this.stateTimer = COOLDOWN_TIME;
        this.chargeDirection = new Vector2(0,0);

        this.attackStrategy = new ContactDamageAttack();
    }

    public SpikedBall(SpikedBall other) {
        super(other);
        this.currentState = State.COOLDOWN;
        this.stateTimer = COOLDOWN_TIME;
        this.chargeDirection = new Vector2(0,0);
    }

    @Override
    public AbstractEnemy clone() {
        return new SpikedBall(this);
    }

    @Override
    public void updateBehavior(List<Player> players, float deltaTime) {
        if (players.isEmpty()) return;
        Player target = players.get(0);

        // Gestione timer unico per entrambi gli stati
        stateTimer -= deltaTime;

        if (currentState == State.COOLDOWN) {
            // --- FASE 1: MIRA E ASPETTA

            if (stateTimer <= 0) {
                //Cerca la posizione del player
                prepareCharge(target.getPosition());
            }

        } else if (currentState == State.CHARGING) {
            // --- FASE 2: CARICA IL giocatore

            Vector2 currentPos = this.getPosition();
            currentPos.mulAdd(chargeDirection, speed * deltaTime);

            // Colpisce chiunque sia lungo il suo cammino
            checkCollisions(players);

            // Se il timer finisce, fermati e ricarica
            if (stateTimer <= 0) {
                stopCharge();
            }
        }
    }

    public void onWallHit() {
        if (currentState == State.CHARGING) {
            System.out.println("SBAM! Toccato il muro. Stop carica.");
            stopCharge();
        }
    }

    private void prepareCharge(Vector2 targetPos) {
        /*System.out.println("--- PREPARAZIONE CARICA ---");
        System.out.println("Mia Posizione: " + this.getPosition());
        System.out.println("Target Posizione: " + targetPos);*/

        this.chargeDirection = targetPos.cpy().sub(this.getPosition());

        System.out.println("Vettore Direzione (Pre-Normalize): " + this.chargeDirection);

        this.chargeDirection.nor();

       // System.out.println("Vettore Direzione (Finale): " + this.chargeDirection);

        this.currentState = State.CHARGING;
        this.stateTimer = MAX_CHARGE_TIME;
    }

    private void stopCharge() {
        this.currentState = State.COOLDOWN;
        this.stateTimer = COOLDOWN_TIME; // Resetta timer per la pausa
    }

    private void checkCollisions(List<Player> players) {
        for (Player p : players) {
            // Se tocca il player (distanza < somma raggi, es. 20 pixel)
            if (this.getPosition().dst(p.getPosition()) < 20f) {
                this.attack(players);
            }
        }
    }
}
