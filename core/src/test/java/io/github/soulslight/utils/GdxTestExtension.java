package io.github.soulslight.utils;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mockito;

/**
 * Questa estensione inizializza l'ambiente LibGDX Headless prima dei test. Permette di usare classi
 * come Vector2, MathUtils, e file system Gdx.
 */
public class GdxTestExtension implements BeforeAllCallback {
  /**
   * This is the JUnit 5 replacement for JUnit 4's Rules and Runners. It is more flexible and allows
   * you to easily plug in the LibGDX Headless backend for any test class just by
   * adding @ExtendWith(GdxTestExtension.class).
   */
  @Override
  public void beforeAll(ExtensionContext context) {
    if (Gdx.app == null) {
      HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();

      // Inizializza l'applicazione Headless
      new HeadlessApplication(
          new ApplicationListener() {
            @Override
            public void create() {}

            @Override
            public void resize(int width, int height) {}

            @Override
            public void render() {}

            @Override
            public void pause() {}

            @Override
            public void resume() {}

            @Override
            public void dispose() {}
          },
          config);

      // Mock OpenGL (classi grafiche)
      Gdx.gl = Mockito.mock(GL20.class);
      Gdx.gl20 = Gdx.gl;
    }
  }
}
