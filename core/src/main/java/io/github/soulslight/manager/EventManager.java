package io.github.soulslight.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {
  private static EventManager instance;
  private final List<GameListener> listeners = new CopyOnWriteArrayList<>(); // Thread-safe list for event listeners

  private EventManager() {}

  public static synchronized EventManager getInstance() {
    if (instance == null) {
      instance = new EventManager();
    }
    return instance;
  }

  public void addListener(GameListener listener) {
    listeners.add(listener);
  }

  public void removeListener(GameListener listener) {
    listeners.remove(listener);
  }

  public void notifyNewGame() {
    for (GameListener listener : listeners) {
      listener.onNewGame();
    }
  }

  public void notifyExit() {
    for (GameListener listener : listeners) {
      listener.onExit();
    }
  }

  public interface GameListener {
    void onNewGame();

    void onExit();
  }
}
