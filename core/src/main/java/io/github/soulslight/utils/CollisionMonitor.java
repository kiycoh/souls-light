package io.github.soulslight.utils;

import com.badlogic.gdx.Gdx;
import io.github.soulslight.model.observer.GameEvent;
import io.github.soulslight.model.observer.Observer;
import java.util.HashMap;
import java.util.Map;

/**
 * Observer that monitors collisions and periodically logs a summary report. Helps reduce log spam
 * by aggregating collision events.
 */
public class CollisionMonitor implements Observer {

  /**
   * La chiave è la coppia di classi coinvolte, non una stringa già composta: gli oggetti Class sono
   * stabili, quindi il percorso caldo alloca solo questo record e non più due getSimpleName() e una
   * concatenazione per ogni contatto. I nomi si formano alla stampa, una volta ogni REPORT_INTERVAL
   * secondi.
   */
  private record ContactPair(Class<?> a, Class<?> b) {}

  private final Map<ContactPair, Integer> collisionCounts = new HashMap<>();
  private float timer = 0f;
  private static final float REPORT_INTERVAL = 10.0f;

  @Override
  public void onEvent(GameEvent event) {
    if (event instanceof GameEvent.CollisionStarted collision) {
      collisionCounts.merge(
          new ContactPair(classOf(collision.a()), classOf(collision.b())), 1, Integer::sum);
    }
  }

  private static Class<?> classOf(Object o) {
    return (o != null) ? o.getClass() : null;
  }

  /** I muri hanno userData null: in Box2D sono corpi statici senza controparte di dominio. */
  private static String describe(Class<?> c) {
    return (c != null) ? c.getSimpleName() : "Wall/Static";
  }

  public void tick(float deltaTime) {
    timer += deltaTime;
    if (timer >= REPORT_INTERVAL) {
      printReport();
      timer = 0f;
    }
  }

  private void printReport() {
    if (collisionCounts.isEmpty()) {
      // Optional: Log silence or heartbeat
      // Gdx.app.log("CollisionMonitor", "No collisions in the last " +
      // REPORT_INTERVAL + "s");
      return;
    }

    Gdx.app.log("CollisionMonitor", "--- Collision Status (Last " + REPORT_INTERVAL + "s) ---");
    for (Map.Entry<ContactPair, Integer> entry : collisionCounts.entrySet()) {
      ContactPair pair = entry.getKey();
      Gdx.app.log(
          "CollisionMonitor",
          String.format(
              "  [%3d] %s <-> %s", entry.getValue(), describe(pair.a()), describe(pair.b())));
    }
    Gdx.app.log("CollisionMonitor", "---------------------------------------------");

    collisionCounts.clear();
  }
}
