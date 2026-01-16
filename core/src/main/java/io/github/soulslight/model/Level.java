package io.github.soulslight.model;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a game level containing the map and entities.
 */
public class Level implements Disposable {

    private TiledMap map;

    private List<AbstractEnemy> enemies;

    private String musicTrack;
    private float ambientLight;

    public Level() {
        this.enemies = new ArrayList<>();
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

    public void setMusicTrack(String musicTrack) {
        this.musicTrack = musicTrack;
    }

    public String getMusicTrack() {//Per quando avremo le musiche
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
        if (map != null) map.dispose();

        enemies.clear();
    }
}
