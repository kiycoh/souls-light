package io.github.soulslight.model;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.soulslight.manager.ResourceManager;

public class MapGenerator {

    public static final int TILE_SIZE = 32;
    public static final int MAP_WIDTH = 50;
    public static final int MAP_HEIGHT = 50;

    public static TiledMap generateMap() {
        TiledMap map = new TiledMap();
        TiledMapTileLayer layer = new TiledMapTileLayer(MAP_WIDTH, MAP_HEIGHT, TILE_SIZE, TILE_SIZE);

        TextureRegion textureRegion = ResourceManager.getInstance().getTileTextureRegion();

        StaticTiledMapTile tile = new StaticTiledMapTile(textureRegion);

        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(tile);
                layer.setCell(x, y, cell);
            }
        }

        map.getLayers().add(layer);
        return map;
    }
}
