package io.github.soulslight.model.enemies;

import java.util.EnumMap;
import java.util.Map;

/**
 * GoF Pattern: Prototype (Registry).
 *
 * <p>Tiene un'istanza pre-configurata per ogni {@link EnemyKind} e ne restituisce cloni. La grafica
 * non passa di qui: i nemici vengono disegnati leggendo TextureManager al momento del rendering,
 * quindi il prototipo porta solo lo stato di dominio.
 */
public final class EnemyRegistry {

  private static final Map<EnemyKind, AbstractEnemy> cache = new EnumMap<>(EnemyKind.class);

  private EnemyRegistry() {}

  /** Costruisce un prototipo per ogni tipo di nemico. */
  public static void loadCache() {
    for (EnemyKind kind : EnemyKind.values()) {
      cache.put(kind, kind.newPrototype());
    }
  }

  /**
   * @return un nuovo nemico del tipo richiesto, o {@code null} se la cache non è stata caricata
   */
  public static AbstractEnemy getEnemy(EnemyKind kind) {
    AbstractEnemy prototype = (kind != null) ? cache.get(kind) : null;
    return (prototype != null) ? prototype.clone() : null;
  }

  /**
   * Variante per le chiavi che arrivano dai salvataggi.
   *
   * @return un nuovo nemico, o {@code null} se la chiave non è riconosciuta
   */
  public static AbstractEnemy getEnemy(String key) {
    return getEnemy(EnemyKind.fromKey(key));
  }
}
