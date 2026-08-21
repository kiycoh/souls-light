package io.github.soulslight.model.enemies;

import java.util.function.Supplier;

/**
 * I tipi di nemico esistenti, con il modo di costruirne un prototipo.
 *
 * <p>Sostituisce le stringhe usate come chiave da {@link EnemyRegistry}, dalle factory e dai
 * salvataggi. {@link #key()} restituisce la stessa stringa di prima, così i salvataggi già scritti
 * restano leggibili.
 */
public enum EnemyKind {
  CHASER("Chaser", Chaser.class, Chaser::new, 100f),
  RANGER("Ranger", Ranger.class, Ranger::new, 70f),
  SHIELDER("Shielder", Shielder.class, Shielder::new, 250f),
  SPIKED_BALL("SpikedBall", SpikedBall.class, SpikedBall::new, 500f),
  // Oblivion decide la propria vita internamente, in base alla fase.
  OBLIVION("Oblivion", Oblivion.class, Oblivion::new, 0f);

  private final String key;
  private final Class<? extends AbstractEnemy> enemyClass;
  private final Supplier<AbstractEnemy> factory;
  private final float baseHealth;

  EnemyKind(
      String key,
      Class<? extends AbstractEnemy> enemyClass,
      Supplier<AbstractEnemy> factory,
      float baseHealth) {
    this.key = key;
    this.enemyClass = enemyClass;
    this.factory = factory;
    this.baseHealth = baseHealth;
  }

  /** La chiave stabile usata nei salvataggi. */
  public String key() {
    return key;
  }

  /** Crea l'istanza che il registry terrà come prototipo da clonare. */
  AbstractEnemy newPrototype() {
    AbstractEnemy enemy = factory.get();
    if (baseHealth > 0f) {
      enemy.setMaxHealth(baseHealth);
    }
    return enemy;
  }

  /**
   * @param enemy il nemico da classificare
   * @return il tipo corrispondente, o {@code null} se la classe non è registrata
   */
  public static EnemyKind of(AbstractEnemy enemy) {
    if (enemy == null) return null;
    for (EnemyKind kind : values()) {
      if (kind.enemyClass == enemy.getClass()) return kind;
    }
    return null;
  }

  /**
   * @param key la chiave letta da un salvataggio
   * @return il tipo corrispondente, o {@code null} se la chiave non è riconosciuta
   */
  public static EnemyKind fromKey(String key) {
    if (key == null) return null;
    for (EnemyKind kind : values()) {
      if (kind.key.equals(key) || kind.name().equals(key)) return kind;
    }
    return null;
  }
}
