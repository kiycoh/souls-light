package io.github.soulslight.model;
import java.util.List;
import com.badlogic.gdx.math.Vector2;

public class Player extends Entity{

    private final PlayerClass type;

    public enum PlayerClass {
        WARRIOR {
            @Override
            public AttackStrategy getStrategy() {
                return new WarriorAttack();
            }
        },
        MAGE {
            @Override
            public AttackStrategy getStrategy() {
                return new MageAttack();
            }
        },
        THIEF {
            @Override
            public AttackStrategy getStrategy() {
                return new ThiefAttack();
            }
        },
        ARCHER {
            @Override
            public AttackStrategy getStrategy() {
                return new ArcherAttack();
            }
        };

        public abstract AttackStrategy getStrategy();
    }

    public Player(PlayerClass type) {
       super();
        if (type == null) {
            throw new IllegalArgumentException("Il tipo di giocatore non può essere nullo!");
        }
        this.type = type;

        this.attackStrategy = type.getStrategy();
    }


}
