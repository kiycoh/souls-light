package io.github.soulslight;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.soulslight.manager.ResourceManager;
import io.github.soulslight.manager.TextureManager;
import io.github.soulslight.view.SplashScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class SoulsLightGame extends Game {

  private SpriteBatch batch;

  public SpriteBatch getBatch() {
    return batch;
  }

  @Override
  public void setScreen(Screen screen) {
    // Dispose the previous screen if it exists to free resources
    if (this.screen != null) {
      this.screen.dispose();
    }
    super.setScreen(screen);
  }

  @Override
  public void create() {
    batch = new SpriteBatch();
    this.setScreen(new SplashScreen(this));
  }

  @Override
  public void dispose() {
    batch.dispose();
    ResourceManager.getInstance().dispose();
    TextureManager.dispose();
    super.dispose();
  }
}
