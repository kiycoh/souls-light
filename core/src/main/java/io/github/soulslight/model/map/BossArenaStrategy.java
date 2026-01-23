package io.github.soulslight.model.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import io.github.soulslight.manager.ResourceManager;

/**
 * Pattern: Strategy (Concrete Strategy)
 * Generates a large, open arena specifically designed for the Oblivion boss
 * fight.
 * The arena is a single rectangular room with walls around the perimeter.
 */
public record BossArenaStrategy(long seed, int width, int height) implements MapGenerationStrategy {

    private static final int TILE_SIZE = 32;
    private static final int WALL_THICKNESS = 3;

    @Override
    public TiledMap generate() {
        var map = new TiledMap();
        map.getProperties().put("width", width);
        map.getProperties().put("height", height);
        map.getProperties().put("tilewidth", TILE_SIZE);
        map.getProperties().put("tileheight", TILE_SIZE);

        var layer = new TiledMapTileLayer(width, height, TILE_SIZE, TILE_SIZE);

        ResourceManager rm = ResourceManager.getInstance();

        // --- FLOOR TILES ---
        TextureRegion[] floorRegions = rm.getFloorTextureRegions();
        StaticTiledMapTile[] floorTiles;

        if (floorRegions != null && floorRegions.length > 0) {
            floorTiles = new StaticTiledMapTile[floorRegions.length];
            for (int i = 0; i < floorRegions.length; i++) {
                StaticTiledMapTile t = new StaticTiledMapTile(floorRegions[i]);
                t.getProperties().put("type", "floor");
                floorTiles[i] = t;
            }
        } else {
            StaticTiledMapTile singleFloor = new StaticTiledMapTile(rm.getFloorTextureRegion());
            singleFloor.getProperties().put("type", "floor");
            floorTiles = new StaticTiledMapTile[] { singleFloor };
        }

        // --- WALL TILE ---
        StaticTiledMapTile wallTile = new StaticTiledMapTile(rm.getWallTextureRegion());
        wallTile.getProperties().put("type", "wall");

        var rnd = new java.util.Random(seed);

        // Generate the arena: walls on perimeter, floor inside
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();

                // Check if this is a perimeter wall
                boolean isWall = x < WALL_THICKNESS || x >= width - WALL_THICKNESS
                        || y < WALL_THICKNESS || y >= height - WALL_THICKNESS;

                if (isWall) {
                    cell.setTile(wallTile);
                } else {
                    // Random floor tile for visual variety
                    cell.setTile(floorTiles[rnd.nextInt(floorTiles.length)]);
                }

                layer.setCell(x, y, cell);
            }
        }

        map.getLayers().add(layer);
        return map;
    }
}
