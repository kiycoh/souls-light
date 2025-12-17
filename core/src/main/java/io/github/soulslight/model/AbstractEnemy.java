package io.github.soulslight.model;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;


import java.util.*;

public abstract class AbstractEnemy extends Entity implements Cloneable{
    protected Vector2 position;
    protected float hp;
    protected float speed;
    protected transient  TextureRegion textureRegion;
    protected AttackStrategy attackStrategy;


    public AbstractEnemy(){
        this.position =  new Vector2(0,0);
    }

    public AbstractEnemy(AbstractEnemy target){
        if( target != null){
            this.hp = target.hp;
            this.speed = target.speed;
            this.position = target.position.cpy();
            this.textureRegion = target.textureRegion;
        }
    }

    public void draw(SpriteBatch batch){
        if(textureRegion!= null){
            batch.draw(textureRegion, position.x, position.y);
        }
    }
    public void setTextureRegion(TextureRegion region) {
        this.textureRegion = region;
    }

    public float getDamage(){
        if( attackStrategy != null){
            return attackStrategy.getDamage();
        }
        return 0;
    }

    public void setHP(float hp){
        this.hp = hp;
    }

    public float getHP(){
        return this.hp;
    }

    public float getSpeed() {
        return this.speed;
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public void attack(List<Player> players){
        if(this.attackStrategy == null) return;

        List<Entity> targets = new ArrayList<>(players);

        this.attackStrategy.executeAttack(this, targets);
    }

    public abstract void updateBehavior(List<Player> players, float deltaTime);
    public abstract AbstractEnemy clone();
}
