package io.github.soulslight.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import java.util.*;

public abstract class AbstractEnemy extends Entity implements Cloneable {

    // 1. RIMOSSO position, hp, attackStrategy.
    // Li ereditiamo da Entity! Non dobbiamo ridefinirli.

    protected float speed;
    protected transient TextureRegion textureRegion;

    // Costruttore vuoto
    public AbstractEnemy(){
        super(); // Chiama Entity
    }

    // Costruttore di Copia
    public AbstractEnemy(AbstractEnemy target){
        if(target != null){
            // --- CAMPI EREDITATI DA ENTITY ---
            // Copiamo i dati dal target alle variabili del PADRE (Entity)

            // Copia  della posizione
            if (target.getPosition() != null) {
                this.position = new Vector2(target.getPosition());
            }

            // Copia vita
            this.health = target.getHealth();

            // Copia l'arma
            this.attackStrategy = target.attackStrategy;

            this.speed = target.speed;
            this.textureRegion = target.textureRegion;
        }
    }

    public void draw(SpriteBatch batch){

        if(textureRegion != null){
            batch.draw(textureRegion, getX(), getY());
        }
    }

    public void setTextureRegion(TextureRegion region) {
        this.textureRegion = region;
    }

    public float getDamage(){
        if(attackStrategy != null){
            return attackStrategy.getDamage();
        }
        return 0;
    }


    public void setHP(float hp){
        this.health = hp; // Scrive sulla variabile del padre
    }

    public float getHP(){
        return this.health; // Legge dalla variabile del padre
    }

    public float getSpeed() {
        return this.speed;
    }


    public void attack(List<Player> players){
        if(this.attackStrategy == null) return;

        List<Entity> targets = new ArrayList<>(players);
        this.attackStrategy.executeAttack(this, targets);
    }

    public abstract void updateBehavior(List<Player> players, float deltaTime);

    public abstract AbstractEnemy clone();
}
