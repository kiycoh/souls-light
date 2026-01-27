package io.github.soulslight.model.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * GoF Pattern: Observer (Subject) Provides an interface for attaching and detaching Observer
 * objects.
 */
public abstract class Subject {
  private final List<Observer> observers = new ArrayList<>();

  public void attach(Observer observer) {
    if (!observers.contains(observer)) {
      observers.add(observer);
    }
  }

  public void detach(Observer observer) {
    observers.remove(observer);
  }

  protected void notifyObservers(String eventType, Object data) {
    for (Observer observer : observers) {
      observer.update(eventType, data);
    }
  }
}
