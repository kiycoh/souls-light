package io.github.soulslight.model.lighting;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

/**
 * Subcell visibility system, tracking light using {@link FieldOfView}. Adapted for Souls Light to
 * work with TiledMap.
 */
public class LightingSystem {
  private static final int SUBDIVISIONS = 4; // Higher = smoother edges, costlier
  private static final float MIN_ALPHA = 0.0f; // Completely dark when unseen

  private double[][] resistance; // 1.0 = blocks light
  private double[][] visible; // Current light levels (0.0 to 1.0)
  private boolean[][] explored; // Fog of War: true if ever seen

  private int width; // Map width in tiles
  private int height; // Map height in tiles
  private int widthBig; // Subdivided width
  private int heightBig; // Subdivided height

  // Cache map reference to avoid re-extracting layer constantly if possible
  private TiledMapTileLayer mainLayer;

  public LightingSystem() {}

  /** The latest visibility grid. */
  public double[][] getVisible() {
    return visible;
  }

  public boolean[][] getExplored() {
    return explored;
  }

  public int getSubdivisions() {
    return SUBDIVISIONS;
  }

  /** Call when a new level is loaded. */
  public void prepareLightingOverlay(TiledMap map) {
    if (map.getLayers().getCount() == 0) return;

    // Assume main ground/wall layer is at index 0
    this.mainLayer = (TiledMapTileLayer) map.getLayers().get(0);

    width = mainLayer.getWidth();
    height = mainLayer.getHeight();
    widthBig = width * SUBDIVISIONS;
    heightBig = height * SUBDIVISIONS;

    visible = new double[heightBig][widthBig];
    resistance = new double[heightBig][widthBig];
    explored = new boolean[heightBig][widthBig];

    updateResistanceMap();
  }

  /** Updates resistance based on map walls. */
  private void updateResistanceMap() {
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        boolean blocksLight = isBlocker(x, y);

        double resValue = blocksLight ? 1.0 : 0.0;

        // Set value for the SxS sub-block
        for (int sy = 0; sy < SUBDIVISIONS; sy++) {
          for (int sx = 0; sx < SUBDIVISIONS; sx++) {
            resistance[y * SUBDIVISIONS + sy][x * SUBDIVISIONS + sx] = resValue;
          }
        }
      }
    }
  }

  private boolean isBlocker(int x, int y) {
    Cell cell = mainLayer.getCell(x, y);
    if (cell == null || cell.getTile() == null) return false;

    // Check "isWall" or not "floor"
    Object isWallObj = cell.getTile().getProperties().get("isWall");
    if (isWallObj != null) {
      return (Boolean) isWallObj;
    }

    String type = cell.getTile().getProperties().get("type", String.class);
    return !"floor".equals(type);
  }

  /**
   * Updates Line of Sight based on player position.
   *
   * @param playerX Player X in WORLD coordinates
   * @param playerY Player Y in WORLD coordinates
   * @param tileSize Size of a tile in pixels (usually 16 or 32)
   */
  public void update(float playerX, float playerY, float tileSize) {
    if (resistance == null) return;

    // Convert world pos to tile pos
    int tx = (int) (playerX / tileSize);
    int ty = (int) (playerY / tileSize);

    // Convert to subdivided pos
    // Center in the sub-cell
    int startX = tx * SUBDIVISIONS + SUBDIVISIONS / 2;
    int startY = ty * SUBDIVISIONS + SUBDIVISIONS / 2;

    // Sight radius in tiles (approx 10-12 tiles looks good)
    int sightRadius = 16;
    double radiusBig = sightRadius * SUBDIVISIONS;

    FieldOfView.reuseFOV(resistance, visible, startX, startY, radiusBig);

    // Update explored state
    for (int y = 0; y < heightBig; y++) {
      for (int x = 0; x < widthBig; x++) {
        if (visible[y][x] > 0.0) {
          explored[y][x] = true;
        }
      }
    }
  }
}
