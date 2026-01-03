package io.github.soulslight.model.Maps;

public interface RoomBuilder {
  void reset();

  void loadTemplate(String templateName);

  void addEnemies(String type);

  // Qua si dovrebbero aggiungere anche gli altri metodi dei diagrammi (void setDoors(), void
  // addEnemies(String type), ecc)

  Room getResult();
}
