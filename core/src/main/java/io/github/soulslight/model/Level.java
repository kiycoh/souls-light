package io.github.soulslight.model;

import com.badlogic.gdx.maps.tiled.TiledMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a game level containing the map and entities.
 */
public class Level {
    private TiledMap map;
    private List<Enemy> enemies;
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

    public void addEnemy(Enemy enemy) {
        this.enemies.add(enemy);
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void setMusicTrack(String musicTrack) {
        this.musicTrack = musicTrack;
    }

    public void setAmbientLight(float ambientLight) {
        this.ambientLight = ambientLight;
    }

    public void dispose() {
        if (map != null) map.dispose();
    }
}

