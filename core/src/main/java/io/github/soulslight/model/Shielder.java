package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class Shielder extends AbstractEnemy {

    // Lista degli alleati per sapere chi difendere
    private List<AbstractEnemy> knownAllies;

    public Shielder() {
        super();
        this.health = 300;  // Molto resistente (Tank)
        this.speed = 40.0f; // Piuttosto lento

        // Assegniamo la strategia che spinge via
        this.attackStrategy = new ShielderAttack();
    }

    // Costruttore di copia per il Prototype Pattern
    private Shielder(Shielder other) {
        super(other);
        // knownAllies non si copia, va settato a ogni frame o spawn
    }

    @Override
    public AbstractEnemy clone() {
        return new Shielder(this);
    }

    /**
     * Da chiamare nel GameScreen: shielder.setAllies(activeEnemies);
     */
    public void setAllies(List<AbstractEnemy> allies) {
        this.knownAllies = allies;
    }

    @Override
    public void updateBehavior(List<Player> players, float deltaTime) {
        // --- 1. LOGICA DI MORTE (Suicide Pact) ---
        // Se non ha la lista o se c'è solo lui nella lista -> Muore
        if (knownAllies == null || knownAllies.size() <= 1) {
            this.takeDamage(this.health); // Si uccide
            return;
        }

        if (players.isEmpty()) return;
        Player player = players.get(0);

        // --- 2. LOGICA DI ATTACCO (Shield Bash) ---
        // Se il player è troppo vicino, smette di muoversi e lo spinge via
        float distToPlayer = this.getPosition().dst(player.getPosition());
        if (distToPlayer <= this.attackStrategy.getRange()) {
            this.attack(players);
            // Opzionale: return qui se vuoi che stia fermo mentre attacca
        }

        // --- 3. LOGICA DI MOVIMENTO (Protezione) ---
        // Trova il nemico più vulnerabile da proteggere
        AbstractEnemy vip = findProtectee(player.getPosition());

        if (vip != null) {
            // Calcola il punto esatto TRA il Player e l'Alleato
            Vector2 protectPos = calculateInterceptionPoint(player.getPosition(), vip.getPosition());
            moveTowards(protectPos, deltaTime);
        } else {
            // Se non trova nessuno (caso raro se il controllo suicide funziona), va verso il player
            moveTowards(player.getPosition(), deltaTime);
        }
    }

    /**
     * Cerca l'alleato più vicino al Player che non sia io stesso.
     */
    private AbstractEnemy findProtectee(Vector2 playerPos) {
        AbstractEnemy bestCandidate = null;
        float minDistance = Float.MAX_VALUE;

        for (AbstractEnemy ally : knownAllies) {
            if (ally == this) continue; // Non proteggere se stesso
            if (ally instanceof Shielder) continue; // Gli Shielder non si proteggono a vicenda

            float dist = ally.getPosition().dst(playerPos);
            if (dist < minDistance) {
                minDistance = dist;
                bestCandidate = ally;
            }
        }
        return bestCandidate;
    }

    /**
     * Calcola la posizione di guardia: 40 pixel davanti all'alleato, verso il nemico.
     */
    private Vector2 calculateInterceptionPoint(Vector2 playerPos, Vector2 allyPos) {
        // Vettore direzione: Da Alleato -> A Player
        Vector2 direction = playerPos.cpy().sub(allyPos);
        direction.nor();

        float shieldOffset = 40.0f; // Distanza dall'alleato

        // Posizione finale = Alleato + (Direzione * 40)
        return allyPos.cpy().mulAdd(direction, shieldOffset);
    }
}
