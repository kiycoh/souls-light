package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class Chaser extends AbstractEnemy{

    public Chaser(){
        super();
        this.hp = 100;
        this.speed = 80.0f;
        this.attackStrategy = new WarriorAttack();
    }

    private Chaser(Chaser other){
        super(other);
    }
    @Override
    public AbstractEnemy clone(){
        return new Chaser(this);
    }

    @Override
    public void updateBehavior(List<Player> players, float deltaTime){
        if(players.isEmpty() || this.hp <= 0) return;

        Player target = players.get(0);
        float distance = this.position.dst(target.getPosition());
        float range = this.attackStrategy.getRange();

        if( distance <= range) {
            this.attack(players);
        }else{
            moveTowards(target.getPosition(),deltaTime);
        }
    }

    public void moveTowards(Vector2 targetPos, float deltaTime){
        Vector2 direction = targetPos.cpy().sub(this.position);

        direction.nor();

        this.position.mulAdd(direction, this.speed * deltaTime);
    }

}
