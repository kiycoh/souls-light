package io.github.soulslight.model.observer;

/** GoF Pattern: Observer (Observer). Riceve gli eventi emessi da un {@link Subject}. */
public interface Observer {
  void onEvent(GameEvent event);
}
