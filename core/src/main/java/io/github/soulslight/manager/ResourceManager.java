package io.github.soulslight.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/** Pattern: Singleton Ensures only one instance of ResourceManager exists to manage game assets. */
public class ResourceManager implements Disposable {
  private static ResourceManager instance;

  private Texture playerTexture;
  private Texture enemyTexture;
  private Texture wallTexture;
  private Texture floorTexture;
  private TextureRegion wallTextureRegion;
  private TextureRegion floorTextureRegion;

  private Texture[] floorVariantTextures;
  private TextureRegion[] floorVariantRegions;

  private ResourceManager() {}

  public static synchronized ResourceManager getInstance() {
    if (instance == null) {
      instance = new ResourceManager();
    }
    return instance;
  }

  public Texture getPlayerTexture() {
    if (playerTexture == null) {
      Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
      pixmap.setColor(Color.RED);
      pixmap.fill();
      playerTexture = new Texture(pixmap);
      pixmap.dispose();
    }
    return playerTexture;
  }

  public Texture getEnemyTexture() {
    if (enemyTexture == null) {
      Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
      pixmap.setColor(Color.GREEN);
      pixmap.fill();
      enemyTexture = new Texture(pixmap);
      pixmap.dispose();
    }
    return enemyTexture;
  }

  public TextureRegion getWallTextureRegion() {
    if (wallTextureRegion == null) {
      if (wallTexture == null) {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.DARK_GRAY); // Border
        pixmap.fill();
        pixmap.setColor(Color.BLACK); // Center
        pixmap.fillRectangle(1, 1, 30, 30);
        wallTexture = new Texture(pixmap);
        pixmap.dispose();
      }
      wallTextureRegion = new TextureRegion(wallTexture);
    }
    return wallTextureRegion;
  }

  public TextureRegion getFloorTextureRegion() {
    if (floorTextureRegion == null) {
      if (floorTexture == null) {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GRAY); // Border
        pixmap.fill();
        pixmap.setColor(Color.LIGHT_GRAY); // Center
        pixmap.fillRectangle(1, 1, 30, 30);
        floorTexture = new Texture(pixmap);
        pixmap.dispose();
      }
      floorTextureRegion = new TextureRegion(floorTexture);
    }
    return floorTextureRegion;
  }

  public TextureRegion[] getFloorTextureRegions() {
    if (floorVariantRegions == null) {
      floorVariantTextures = new Texture[8];
      floorVariantRegions = new TextureRegion[8];

      for (int i = 0; i < 8; i++) {
        String path = "tiles/floor" + (i + 1) + ".png";

        // Loads the original 16x16 PNG as a Pixmap
        Pixmap src = new Pixmap(Gdx.files.internal(path));

        // Create a 32x32 Pixmap to upscale the tile
        Pixmap dst = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        dst.setBlending(Pixmap.Blending.None);

        // 2x scale
        dst.drawPixmap(src, 0, 0, src.getWidth(), src.getHeight(), 0, 0, 32, 32);

        Texture t = new Texture(dst);
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        src.dispose();
        dst.dispose();

        floorVariantTextures[i] = t;
        floorVariantRegions[i] = new TextureRegion(t);
      }
    }
    return floorVariantRegions;
  }

  @Override
  public void dispose() {
    if (playerTexture != null) playerTexture.dispose();
    if (enemyTexture != null) enemyTexture.dispose();
    if (wallTexture != null) wallTexture.dispose();
    if (floorTexture != null) floorTexture.dispose();

    // Dispose floor variant textures
    if (floorVariantTextures != null) {
      for (Texture t : floorVariantTextures) {
        if (t != null) t.dispose();
      }
    }

    floorVariantTextures = null;
    floorVariantRegions = null;
  }
}
