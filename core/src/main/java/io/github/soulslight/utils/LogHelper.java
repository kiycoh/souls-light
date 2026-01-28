package io.github.soulslight.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.HashMap;
import java.util.Map;

/** Utility class for logging operations. */
public class LogHelper {

  private static String lastMessage = "";
  private static final Map<String, Long> lastLogTimes = new HashMap<>();

  /**
   * Logs a message only if it differs from the previously logged message. This prevents log
   * flooding from the game loop.
   *
   * @param tag The tag for the log entry.
   * @param message The message to log.
   */
  public static void logDistinct(String tag, String message) {
    if (message == null) return;

    String fullMessage = tag + ": " + message;

    if (!fullMessage.equals(lastMessage)) {
      lastMessage = fullMessage;
      Gdx.app.log(tag, message);
    }
  }

  /**
   * Logs a message at most once every 'interval' seconds. Useful for recurring events in the update
   * loop (e.g. "Enemy Attacking").
   *
   * @param tag The tag for the log entry.
   * @param message The message to log.
   * @param intervalSeconds The minimum time in seconds between logs of this specific message.
   */
  public static void logThrottled(String tag, String message, float intervalSeconds) {
    if (message == null) return;

    String key = tag + ":" + message;
    long currentTime = TimeUtils.millis();
    long intervalMillis = (long) (intervalSeconds * 1000);

    if (!lastLogTimes.containsKey(key) || (currentTime - lastLogTimes.get(key) > intervalMillis)) {
      lastLogTimes.put(key, currentTime);
      Gdx.app.log(tag, message);
    }
  }
}
