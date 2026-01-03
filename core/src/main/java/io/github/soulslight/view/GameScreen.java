package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.soulslight.controller.GameController;
import io.github.soulslight.manager.ResourceManager;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.Player;

public class GameScreen implements Screen {

  // --- MVC DEPENDENCIES ---
  private final SpriteBatch batch;
  private final GameModel model;
  private final GameController controller;

  // --- PIXEL ART RENDERING ---
  private static final float WORLD_WIDTH = 480;
  private static final float WORLD_HEIGHT = 270;

  private final OrthographicCamera camera;
  private final Viewport viewport;
  private final OrthogonalTiledMapRenderer mapRenderer;
  private final Texture playerTexture;

  public GameScreen(SpriteBatch batch, GameModel model, GameController controller) {
    this.batch = batch;
    this.model = model;
    this.controller = controller;

    // Setup Camera e Viewport for Pixel Art
    this.camera = new OrthographicCamera();

    // FitViewport maintains aspect ratio while scaling to fit the screen.
    this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

    // Map Renderer
    this.mapRenderer = new OrthogonalTiledMapRenderer(model.getMap(), batch);

    // Use ResourceManager for player texture
    this.playerTexture = ResourceManager.getInstance().getPlayerTexture();
  }

  @Override
  public void show() {
    // Activate controller as input processor
    Gdx.input.setInputProcessor(controller);
  }

  @Override
  public void render(float delta) {
    // MVC Update Loop
    controller.update(delta);
    model.update(delta);

    // Update camera position to follow player
    Player player = model.getPlayer();
    if (player != null) {
      camera.position.set(player.getPosition().x, player.getPosition().y, 0);
    }
    camera.update();

    // Rendering
    ScreenUtils.clear(0, 0, 0, 1); // (black background)

    // Render Map
    mapRenderer.setView(camera);
    mapRenderer.render();

    // Apply batch with camera projection
    batch.setProjectionMatrix(camera.combined);

    batch.begin();
    if (player != null) {
      batch.draw(playerTexture, player.getPosition().x, player.getPosition().y);
    }
    batch.end();

    // Debug Renderer for Box2D (to be added)
  }

  @Override
  public void resize(int width, int height) {
    // 5. IMPORTANT: Window resizing handling true pixel
    viewport.update(width, height, true); // true = centra la camera
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {
    // Dispose resources that are not managed by ResourceManager or Game class
    // Note: batch is managed by Game class, textures by ResourceManager
    mapRenderer.dispose();
    model.dispose();
  }
}
