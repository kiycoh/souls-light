package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Ranger extends AbstractEnemy {

    private static final float INDICE_DI_FUGA = 0.5f;

    public Ranger(){
        super();
        this.health = 70;
        this.speed = 50.0f;
        this.attackStrategy = new ArcherAttack();
    }

    private Ranger(Ranger other){
        super(other);
        this.attackStrategy = other.attackStrategy;
    }
    @Override
    public AbstractEnemy clone(){
        return new Ranger(this);
    }

    @Override
    public void updateBehavior(List<Player> players, float deltaTime){
        if(players.isEmpty() || this.health <= 0) return;

        Player target = players.get(0);
        float distance = this.position.dst(target.getPosition());
        float maxRange = this.attackStrategy.getRange();
        float minSafeDistance = maxRange * INDICE_DI_FUGA;


        if (distance < minSafeDistance) {
            // Scappa se troppo vicino
            moveAway(target.getPosition(), deltaTime);
            //Opzionale: Se vuoi che spari MENTRE scappa, lascia this.attack(players) anche qui.

        } else if (distance <= maxRange) {
            // DISTANZA PERFETTA -> Sta fermo e spara
            this.attack(players);

        } else {
            // ZONA 3: TROPPO LONTANO -> Avvicinati
            moveTowards(target.getPosition(), deltaTime);
        }
    }

    // Va verso il target se troppo lontano
    public void moveTowards(Vector2 targetPos, float deltaTime){
        Vector2 direction = targetPos.cpy().sub(this.position);
        direction.nor();
        this.position.mulAdd(direction, this.speed * deltaTime);
    }

    // Scappa dal target
    public void moveAway(Vector2 targetPos, float deltaTime){
        // Calcolo: (MiaPosizione - TargetPosizione) crea un vettore che punta VIA dal target
        Vector2 direction = this.position.cpy().sub(targetPos);

        direction.nor(); // Normalizza a 1

        // Si muove nella direzione di fuga
        this.position.mulAdd(direction, this.speed * deltaTime);
    }

}

