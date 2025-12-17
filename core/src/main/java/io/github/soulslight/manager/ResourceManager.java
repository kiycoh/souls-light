package io.github.soulslight.manager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/**
 * Pattern: Singleton
 * Ensures only one instance of ResourceManager exists to manage game assets.
 */
public class ResourceManager implements Disposable {
    private static ResourceManager instance;

    private Texture playerTexture;
    private Texture tileTexture;
    private TextureRegion tileTextureRegion;

    private ResourceManager() {}

    public static ResourceManager getInstance() {
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

    public TextureRegion getTileTextureRegion() {
        if (tileTextureRegion == null) {
            if (tileTexture == null) {
                Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
                pixmap.setColor(Color.DARK_GRAY);
                pixmap.fill();
                pixmap.setColor(Color.GRAY);
                pixmap.drawRectangle(0, 0, 32, 32);
                tileTexture = new Texture(pixmap);
                pixmap.dispose();
            }
            tileTextureRegion = new TextureRegion(tileTexture);
        }
        return tileTextureRegion;
    }

    @Override
    public void dispose() {
        if (playerTexture != null) playerTexture.dispose();
        if (tileTexture != null) tileTexture.dispose();
    }
}
