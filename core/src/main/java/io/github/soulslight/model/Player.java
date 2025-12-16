package io.github.soulslight.model;
import java.util.List;
import com.badlogic.gdx.math.Vector2;

public class Player {

    private final PlayerClass type;             // Il tipo (es. WARRIOR) - utile per UI o salvataggi
    private final AttackStrategy attackStrategy;

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
                return new ThiefAttack(); // Assicurati di aver creato questa classe
            }
        },
        ARCHER {
            @Override
            public AttackStrategy getStrategy() {
                return new ArcherAttack(); // Assicurati di aver creato questa classe
            }
        };

        // Metodo astratto che ogni costante qui sopra DEVE implementare
        public abstract AttackStrategy getStrategy();
    }

    public Player(PlayerClass type) {
        if (type == null) {
            throw new IllegalArgumentException("Il tipo di giocatore non può essere nullo!");
        }
        this.type = type;

        // QUI avviene la magia: chiediamo all'Enum di darci l'arma giusta
        this.attackStrategy = type.getStrategy();
    }


}
