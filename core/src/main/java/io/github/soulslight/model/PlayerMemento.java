package io.github.soulslight.model;

import io.github.soulslight.model.entities.Player;

public class PlayerMemento {
  public Player.PlayerClass type; // remembers class type
  public float health;
  public float x;
  public float y;

  // No-arg constructor
  public PlayerMemento() {}

  public java.util.List<String> inventoryItems;

  public PlayerMemento(
      Player.PlayerClass type,
      float health,
      float x,
      float y,
      java.util.List<String> inventoryItems) {
    this.type = type;
    this.health = health;
    this.x = x;
    this.y = y;
    this.inventoryItems = inventoryItems;
  }
}
