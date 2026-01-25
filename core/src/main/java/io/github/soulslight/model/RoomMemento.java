package io.github.soulslight.model;

public class RoomMemento {
  public String id;
  public boolean isCleared;
  public boolean doorsLocked;

  public RoomMemento() {}

  public RoomMemento(String id, boolean isCleared, boolean doorsLocked) {
    this.id = id;
    this.isCleared = isCleared;
    this.doorsLocked = doorsLocked;
  }
}
