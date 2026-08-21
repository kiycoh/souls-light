package io.github.soulslight.model.items;

import io.github.soulslight.model.inventory.IPickable;
import java.util.function.Supplier;

/**
 * Elenco chiuso degli item che un salvataggio può contenere.
 *
 * <p>Il ripristino legge nomi di item da un file locale, quindi modificabile. Passare quel nome a
 * {@code Class.forName(...).newInstance()} costruirebbe qualunque classe del classpath dotata di
 * costruttore senza argomenti, eseguendone il costruttore <em>prima</em> di qualsiasi verifica di
 * tipo. Questo enum è la whitelist: ciò che non compare qui non è costruibile a partire da un
 * salvataggio.
 */
public enum ItemType {
  HEALTH_POTION(HealthPotion.class, HealthPotion::new),
  DUNGEON_KEY(DungeonKey.class, DungeonKey::new);

  private final Class<? extends IPickable> itemClass;
  private final Supplier<IPickable> factory;

  ItemType(Class<? extends IPickable> itemClass, Supplier<IPickable> factory) {
    this.itemClass = itemClass;
    this.factory = factory;
  }

  /** Crea una nuova istanza dell'item di questo tipo. */
  public IPickable create() {
    return factory.get();
  }

  /** La chiave stabile da scrivere nel salvataggio. */
  public String key() {
    return name();
  }

  /**
   * @param item l'item da classificare
   * @return il tipo corrispondente, o {@code null} se l'item non è salvabile
   */
  public static ItemType of(IPickable item) {
    if (item == null) return null;
    for (ItemType type : values()) {
      if (type.itemClass == item.getClass()) return type;
    }
    return null;
  }

  /**
   * Risolve una chiave letta da un salvataggio. Accetta anche il nome di classe usato dai
   * salvataggi precedenti, così i vecchi file restano leggibili; resta comunque una corrispondenza
   * contro un insieme chiuso, mai una lookup nel classpath.
   *
   * @param key la chiave letta dal file
   * @return il tipo corrispondente, o {@code null} se la chiave non è riconosciuta
   */
  public static ItemType fromKey(String key) {
    if (key == null) return null;
    for (ItemType type : values()) {
      if (type.name().equals(key)
          || type.itemClass.getName().equals(key)
          || type.itemClass.getSimpleName().equals(key)) {
        return type;
      }
    }
    return null;
  }
}
