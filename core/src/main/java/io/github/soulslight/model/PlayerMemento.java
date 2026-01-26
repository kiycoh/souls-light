package io.github.soulslight.model;

import io.github.soulslight.model.entities.Player;

public class PlayerMemento {
  public Player.PlayerClass type; // remembers class type
  public float health;
  public float x;
  public float y;

  // No-arg constructor
  public PlayerMemento() {}

  public PlayerMemento(Player.PlayerClass type, float health, float x, float y) {
    this.type = type;
    this.health = health;
    this.x = x;
    this.y = y;
  }
}
