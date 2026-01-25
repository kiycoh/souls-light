package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import io.github.soulslight.model.lighting.LightingSystem;

public class LightingRenderer {

  private final ShapeRenderer shapeRenderer;
  private final float[] viewportSize = new float[2];

  public LightingRenderer() {
    this.shapeRenderer = new ShapeRenderer();
  }

  public void render(
      LightingSystem lightingSystem, TiledMap map, com.badlogic.gdx.math.Matrix4 projectionMatrix) {
    if (lightingSystem == null || map == null) return;

    double[][] visible = lightingSystem.getVisible();
    boolean[][] explored = lightingSystem.getExplored();

    if (visible == null || explored == null) return;

    MapProperties prop = map.getProperties();
    int tileWidth = prop.get("tilewidth", Integer.class);
    int tileHeight = prop.get("tileheight", Integer.class);

    int subDiv = lightingSystem.getSubdivisions();
    float subWidth = (float) tileWidth / subDiv;
    float subHeight = (float) tileHeight / subDiv;

    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    shapeRenderer.setProjectionMatrix(projectionMatrix);
    shapeRenderer.begin(ShapeType.Filled);

    int height = visible.length;
    int width = visible[0].length;

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        double light = visible[y][x];
        boolean isExplored = explored[y][x];

        float alpha;
        if (!isExplored) {
          alpha = 0.8f; // Unexplored = Black
        } else {
          // Explored but currently dark = Fog (0.7f max darkness?)
          // Light 1.0 = Alpha 0.0 (transparent)
          // Light 0.0 = Alpha 0.6 (dim)
          // You can tweak this "Memory" darkness
          float maxDarkness = 0.6f; // 0.8f for "seen but dark"
          alpha = maxDarkness * (1.0f - (float) light);
        }

        if (alpha > 0.05f) {
          shapeRenderer.setColor(0, 0, 0, alpha);
          shapeRenderer.rect(x * subWidth, y * subHeight, subWidth, subHeight);
        }
      }
    }

    shapeRenderer.end();
    Gdx.gl.glDisable(GL20.GL_BLEND);
  }

  public void dispose() {
    shapeRenderer.dispose();
  }
}
