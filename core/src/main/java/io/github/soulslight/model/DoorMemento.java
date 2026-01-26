package io.github.soulslight.model;

public class DoorMemento {
  public int index;
  public boolean isLocked;

  public DoorMemento() {}

  public DoorMemento(int index, boolean isLocked) {
    this.index = index;
    this.isLocked = isLocked;
  }
}
