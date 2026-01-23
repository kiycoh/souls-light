package io.github.soulslight.model.map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Disposable;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.room.RoomManager;
import java.util.ArrayList;
import java.util.List;

/** Represents a game level containing the map and entities. */
public class Level implements Disposable {

  private TiledMap map;

  private List<AbstractEnemy> enemies;
  private RoomManager roomManager;

  private String musicTrack;
  private float ambientLight;

  public Level() {
    this.enemies = new ArrayList<>();
    this.roomManager = new RoomManager();
  }

  public TiledMap getMap() {
    return map;
  }

  public void setMap(TiledMap map) {
    this.map = map;
  }

  public void addEnemy(AbstractEnemy enemy) {
    this.enemies.add(enemy);
  }

  public List<AbstractEnemy> getEnemies() {
    return enemies;
  }

  public RoomManager getRoomManager() {
    return roomManager;
  }

  public void setRoomManager(RoomManager roomManager) {
    this.roomManager = roomManager;
  }

  public void setMusicTrack(String musicTrack) {
    this.musicTrack = musicTrack;
  }

  public String getMusicTrack() { // Per quando avremo le musiche
    return musicTrack;
  }

  public void setAmbientLight(float ambientLight) {
    this.ambientLight = ambientLight;
  }

  public float getAmbientLight() {
    return ambientLight;
  }

  @Override
  public void dispose() {
    if (map != null)
      map.dispose();
    if (roomManager != null)
      roomManager.dispose();
    enemies.clear();
  }
}
