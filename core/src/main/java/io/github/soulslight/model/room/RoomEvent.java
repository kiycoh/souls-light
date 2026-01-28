package io.github.soulslight.model.room;

/** Strategy interface for room events. */
public interface RoomEvent {
  void onEnter(EventRoom room);

  void update(EventRoom room, float delta);

  void onExit(EventRoom room);
}
