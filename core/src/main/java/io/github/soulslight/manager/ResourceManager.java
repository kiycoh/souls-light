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
  private Texture wallTexture;
  private Texture floorTexture;

  private TextureRegion wallTextureRegion;
  private TextureRegion floorTextureRegion;

  private Texture[] floorVariantTextures;
  private TextureRegion[] floorVariantRegions;

  private Texture[] wallMaskTextures;
  private TextureRegion[] wallMaskRegions;

  private Texture innerNeWallTexture;
  private Texture innerNwWallTexture;
  private Texture innerSeWallTexture;
  private Texture innerSwWallTexture;

  private TextureRegion innerNeWallRegion;
  private TextureRegion innerNwWallRegion;
  private TextureRegion innerSeWallRegion;
  private TextureRegion innerSwWallRegion;

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

  public TextureRegion getWallTextureRegion() {
    if (wallTextureRegion == null) {
      if (wallTexture == null) {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.DARK_GRAY); // border
        pixmap.fill();
        pixmap.setColor(Color.BLACK); // center
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
        pixmap.setColor(Color.GRAY); // border
        pixmap.fill();
        pixmap.setColor(Color.LIGHT_GRAY); // center
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

  public TextureRegion[] getWallMaskRegions() {
    if (wallMaskRegions == null) {
      final int MASK_COUNT = 16;
      wallMaskTextures = new Texture[MASK_COUNT];
      wallMaskRegions = new TextureRegion[MASK_COUNT];

      for (int i = 0; i < MASK_COUNT; i++) {
        String path = "tiles/wall_" + String.format("%02d", i) + ".png";

        if (!Gdx.files.internal(path).exists()) {
          wallMaskTextures[i] = null;
          wallMaskRegions[i] = null;
          continue;
        }

        Pixmap src = new Pixmap(Gdx.files.internal(path));
        Pixmap dst = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        dst.setBlending(Pixmap.Blending.None);

        // 2x scale
        dst.drawPixmap(src, 0, 0, src.getWidth(), src.getHeight(), 0, 0, 32, 32);

        Texture t = new Texture(dst);
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        src.dispose();
        dst.dispose();

        wallMaskTextures[i] = t;
        wallMaskRegions[i] = new TextureRegion(t);
      }
    }
    return wallMaskRegions;
  }

  private TextureRegion loadInnerCornerRegion(String path, Texture fallbackTexture) {
    if (!Gdx.files.internal(path).exists()) {
      return new TextureRegion(fallbackTexture);
    }

    Pixmap src = new Pixmap(Gdx.files.internal(path));
    Pixmap dst = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
    dst.setBlending(Pixmap.Blending.None);

    // 2x scale
    dst.drawPixmap(src, 0, 0, src.getWidth(), src.getHeight(), 0, 0, 32, 32);

    Texture t = new Texture(dst);
    t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

    src.dispose();
    dst.dispose();

    return new TextureRegion(t);
  }

  public TextureRegion getInnerCornerWallNE() {
    if (innerNeWallRegion == null) {
      String path = "tiles/wall_inner_ne.png";
      if (Gdx.files.internal(path).exists()) {
        innerNeWallRegion = loadInnerCornerRegion(path, getWallTextureRegion().getTexture());
      } else {
        innerNeWallRegion = getWallTextureRegion();
      }
    }
    return innerNeWallRegion;
  }

  public TextureRegion getInnerCornerWallNW() {
    if (innerNwWallRegion == null) {
      String path = "tiles/wall_inner_nw.png";
      if (Gdx.files.internal(path).exists()) {
        innerNwWallRegion = loadInnerCornerRegion(path, getWallTextureRegion().getTexture());
      } else {
        innerNwWallRegion = getWallTextureRegion();
      }
    }
    return innerNwWallRegion;
  }

  public TextureRegion getInnerCornerWallSE() {
    if (innerSeWallRegion == null) {
      String path = "tiles/wall_inner_se.png";
      if (Gdx.files.internal(path).exists()) {
        innerSeWallRegion = loadInnerCornerRegion(path, getWallTextureRegion().getTexture());
      } else {
        innerSeWallRegion = getWallTextureRegion();
      }
    }
    return innerSeWallRegion;
  }

  public TextureRegion getInnerCornerWallSW() {
    if (innerSwWallRegion == null) {
      String path = "tiles/wall_inner_sw.png";
      if (Gdx.files.internal(path).exists()) {
        innerSwWallRegion = loadInnerCornerRegion(path, getWallTextureRegion().getTexture());
      } else {
        innerSwWallRegion = getWallTextureRegion();
      }
    }
    return innerSwWallRegion;
  }

  @Override
  public void dispose() {
    if (playerTexture != null) playerTexture.dispose();
    if (wallTexture != null) wallTexture.dispose();
    if (floorTexture != null) floorTexture.dispose();

    // Dispose floor variant textures
    if (floorVariantTextures != null) {
      for (Texture t : floorVariantTextures) {
        if (t != null) t.dispose();
      }
    }

    // Dispose wall bitmask variant textures
    if (wallMaskTextures != null) {
      for (Texture t : wallMaskTextures) {
        if (t != null) t.dispose();
      }
    }

    // Dispose inner wall textures
    if (innerNeWallTexture != null) innerNeWallTexture.dispose();
    if (innerNwWallTexture != null) innerNwWallTexture.dispose();
    if (innerSeWallTexture != null) innerSeWallTexture.dispose();
    if (innerSwWallTexture != null) innerSwWallTexture.dispose();

    floorVariantTextures = null;
    floorVariantRegions = null;
    wallMaskTextures = null;
    wallMaskRegions = null;

    innerNeWallTexture = null;
    innerNwWallTexture = null;
    innerSeWallTexture = null;
    innerSwWallTexture = null;

    innerNeWallRegion = null;
    innerNwWallRegion = null;
    innerSeWallRegion = null;
    innerSwWallRegion = null;
  }
}
