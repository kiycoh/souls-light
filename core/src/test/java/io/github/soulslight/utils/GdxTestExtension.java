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

      // Mock OpenGL
      Gdx.gl = Mockito.mock(GL20.class);
      Gdx.gl20 = Gdx.gl;

      // Fix Asset Path: Wrap Gdx.files to look in ../assets/ if not found in core/
      final com.badlogic.gdx.Files originalFiles = Gdx.files;
      Gdx.files =
          new com.badlogic.gdx.Files() {
            @Override
            public com.badlogic.gdx.files.FileHandle getFileHandle(String path, FileType type) {
              return originalFiles.getFileHandle(path, type);
            }

            @Override
            public com.badlogic.gdx.files.FileHandle classpath(String path) {
              return originalFiles.classpath(path);
            }

            @Override
            public com.badlogic.gdx.files.FileHandle internal(String path) {
              com.badlogic.gdx.files.FileHandle handle = originalFiles.internal(path);
              if (!handle.exists()) {
                // Try looking in ../assets
                com.badlogic.gdx.files.FileHandle alt = originalFiles.internal("../assets/" + path);
                if (alt.exists()) return alt;
              }
              return handle;
            }

            @Override
            public com.badlogic.gdx.files.FileHandle external(String path) {
              return originalFiles.external(path);
            }

            @Override
            public com.badlogic.gdx.files.FileHandle absolute(String path) {
              return originalFiles.absolute(path);
            }

            @Override
            public com.badlogic.gdx.files.FileHandle local(String path) {
              return originalFiles.local(path);
            }

            @Override
            public String getLocalStoragePath() {
              return originalFiles.getLocalStoragePath();
            }

            @Override
            public boolean isLocalStorageAvailable() {
              return originalFiles.isLocalStorageAvailable();
            }

            @Override
            public boolean isExternalStorageAvailable() {
              return originalFiles.isExternalStorageAvailable();
            }

            @Override
            public String getExternalStoragePath() {
              return originalFiles.getExternalStoragePath();
            }
          };
    }
  }
}
