package io.github.soulslight.utils;

import com.badlogic.gdx.Gdx;
import io.github.soulslight.model.observer.Observer;
import java.util.HashMap;
import java.util.Map;

/**
 * Observer that monitors collisions and periodically logs a summary report. Helps reduce log spam
 * by aggregating collision events.
 */
public class CollisionMonitor implements Observer {

  private final Map<String, Integer> collisionCounts = new HashMap<>();
  private float timer = 0f;
  private static final float REPORT_INTERVAL = 10.0f;

  @Override
  public void update(String eventType, Object data) {
    if ("COLLISION_START".equals(eventType) && data instanceof String) {
      String key = (String) data;
      collisionCounts.merge(key, 1, Integer::sum);
    }
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
    for (Map.Entry<String, Integer> entry : collisionCounts.entrySet()) {
      Gdx.app.log(
          "CollisionMonitor", String.format("  [%3d] %s", entry.getValue(), entry.getKey()));
    }
    Gdx.app.log("CollisionMonitor", "---------------------------------------------");

    collisionCounts.clear();
  }
}
