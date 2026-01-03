package io.github.soulslight.manager;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
  private static EventManager instance;
  private List<GameListener> listeners = new ArrayList<>();

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
