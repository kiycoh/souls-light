package io.github.soulslight.model.observer;

import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.map.Level;

/**
 * GoF Pattern: Observer — l'evento notificato.
 *
 * <p>È un tipo {@code sealed}, quindi uno {@code switch} sui suoi casi è esaustivo: aggiungere un
 * evento senza gestirlo in tutti gli osservatori diventa un errore di compilazione. Prima l'evento
 * era una stringa confrontata con {@code equals()} e il payload un {@code Object} da downcastare:
 * un refuso non era un errore, era un evento che smetteva di arrivare in silenzio.
 */
public sealed interface GameEvent {

  /** Il livello corrente è stato completato. */
  record LevelCompleted(Level level) implements GameEvent {}

  /** Un livello è stato ricostruito a partire da un salvataggio. */
  record LevelRestored(Level level) implements GameEvent {}

  /** Un player ha subito danno. */
  record PlayerHit(Player player, float amount) implements GameEvent {}

  /**
   * Due corpi fisici hanno iniziato a toccarsi. Trasporta gli oggetti così come sono: comporre una
   * descrizione leggibile è compito di chi la stampa, non del percorso caldo che la emette.
   */
  record CollisionStarted(
      io.github.soulslight.model.physics.Collidable a,
      io.github.soulslight.model.physics.Collidable b)
      implements GameEvent {}
}
