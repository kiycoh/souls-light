package io.github.soulslight.model.physics;

import com.badlogic.gdx.math.Vector2;
import io.github.soulslight.model.enemies.SpikedBall;
import io.github.soulslight.model.entities.ItemEntity;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.observer.GameEvent;
import io.github.soulslight.model.observer.Subject;
import io.github.soulslight.model.room.Portal;
import io.github.soulslight.model.room.RoomSensor;

/**
 * GoF Pattern: Adapter (Adaptee).
 *
 * <p>Contiene la logica di gioco delle collisioni, espressa su tipi di dominio. Prima riceveva due
 * {@code Object} e chiamava otto metodi di controllo, ciascuno con la propria coppia di {@code
 * instanceof}: sedici test di tipo per contatto. Ora il tipo è già risolto dall'adapter e lo
 * smistamento avviene una volta per lato.
 */
public class GameCollisionHandler extends Subject implements CollisionHandler {

  @Override
  public void handleBeginContact(Collidable a, Collidable b) {
    notifyContact(a, b);

    // Ogni interazione all'inizio del contatto ha un Player da un lato: si prova nei due versi.
    onPlayerTouch(a, b);
    onPlayerTouch(b, a);
  }

  @Override
  public void handleEndContact(Collidable a, Collidable b) {
    onPlayerLeave(a, b);
    onPlayerLeave(b, a);
  }

  private void onPlayerTouch(Collidable maybePlayer, Collidable other) {
    if (!(maybePlayer instanceof Player player)) return;

    switch (other) {
      case RoomSensor sensor -> sensor.onPlayerContact();
      case Portal portal -> portal.onPlayerEnter();
      case ItemEntity itemEntity -> {
        if (player.pickUpItem(itemEntity.getItem())) {
          itemEntity.kill();
        }
      }
      default -> {
        // Muri, porte, proiettili e nemici: nessuna reazione al contatto con il player.
      }
    }
  }

  private void onPlayerLeave(Collidable maybePlayer, Collidable other) {
    if (maybePlayer instanceof Player && other instanceof Portal portal) {
      portal.onPlayerExit();
    }
  }

  private void notifyContact(Collidable a, Collidable b) {
    notifyObservers(new GameEvent.CollisionStarted(a, b));
  }

  /**
   * Rimbalzo dello SpikedBall contro un muro. Richiede la normale del contatto, che solo il motore
   * fisico conosce: l'adapter la calcola e la passa già pronta.
   */
  public void handleSpikedBallHit(SpikedBall spikedBall, Vector2 normal) {
    spikedBall.onWallHit(normal);
  }
}
