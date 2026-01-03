package io.github.soulslight.manager;

public class GameManager {
  private static GameManager instance;

  private GameManager() {}

  public static synchronized GameManager getInstance() {
    if (instance == null) {
      instance = new GameManager();
    }
    return instance;
  }
}
