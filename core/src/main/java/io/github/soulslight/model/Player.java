package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;

public class Player {

  private final PlayerClass type; // Il tipo (es. WARRIOR) - utile per UI o salvataggi
  private final Vector2 position;
  private AttackStrategy attackStrategy;

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
    this.position = new Vector2(0, 0);

    // QUI avviene la magia: chiediamo all'Enum di darci l'arma giusta
    this.attackStrategy = type.getStrategy();
  }

  public AttackStrategy getAttackStrategy() {
    return attackStrategy;
  }

  public void setAttackStrategy(AttackStrategy attackStrategy) {
    this.attackStrategy = attackStrategy;
  }

  public void doAnAttack() {
    if (attackStrategy != null) {
      attackStrategy.attack();
    }
  }

  public void move(float x, float y) {
    this.position.add(x, y);
  }

  public Vector2 getPosition() {
    return position;
  }
}
