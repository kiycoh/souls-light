package io.github.soulslight.model.room;

/** A room that delegates behavior to a RoomEvent strategy. */
public class EventRoom extends Room {

  private final RoomEvent eventStrategy;

  public EventRoom(
      String id, float x, float y, float width, float height, RoomEvent eventStrategy) {
    super(id, x, y, width, height);
    this.eventStrategy = eventStrategy;
  }

  private boolean pendingEventEnter = false;

  @Override
  public void onPlayerEntered() {
    super.onPlayerEntered();
    this.pendingEventEnter = true;
  }

  @Override
  public void update(float deltaTime) {
    if (pendingEventEnter) {
      pendingEventEnter = false;
      if (eventStrategy != null) {
        eventStrategy.onEnter(this);
      }
    }
    super.update(deltaTime);
    if (eventStrategy != null) {
      eventStrategy.update(this, deltaTime);
    }
  }

  @Override
  public void transitionTo(RoomState newState) {
    super.transitionTo(newState);
    // Potential hook for event strategy on state change if needed
  }
}
