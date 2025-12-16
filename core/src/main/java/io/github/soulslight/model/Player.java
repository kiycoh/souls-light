package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;

public class Player{

    private final PlayerClass type;
    private final AttackStrategy attackStrategy;

            @Override
            public AttackStrategy getAttackStrategy(){
                return new WarriorAttack();
            }
        },
        MAGE{
            @Override

        };
        public abstract AttackStrategy getAttackStrategy();
    }
}

