package io.github.soulslight.manager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class EventManagerTest {

  @Test
  public void testNewGame() {
    EventManager eventManager = EventManager.getInstance();
    EventManager.GameListener listener = Mockito.mock(EventManager.GameListener.class);
    eventManager.addListener(listener);

    eventManager.notifyNewGame();

    Mockito.verify(listener).onNewGame();
  }

  @Test
  public void testExit() {
    EventManager eventManager = EventManager.getInstance();
    EventManager.GameListener listener = Mockito.mock(EventManager.GameListener.class);
    eventManager.addListener(listener);

    eventManager.notifyExit();

    Mockito.verify(listener).onExit();
  }
}
