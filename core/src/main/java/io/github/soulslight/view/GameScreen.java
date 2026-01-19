package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.soulslight.controller.GameController;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.manager.TextureManager;
import io.github.soulslight.model.AbstractEnemy;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.Oblivion;
import io.github.soulslight.model.Player;
import io.github.soulslight.model.Projectile;

public class GameScreen implements Screen {

  private final SpriteBatch batch;
  private final GameModel model;
  private final GameController controller;

  private final GameHUD hud;
  private final OrthographicCamera camera;
  private final Viewport viewport;
  private final OrthogonalTiledMapRenderer mapRenderer;
  private final Box2DDebugRenderer debugRenderer;

  // Map size in pixels, used to clamp the camera within map bounds
  private float mapPixelWidth = 0f;
  private float mapPixelHeight = 0f;

  public GameScreen(SpriteBatch batch, GameModel model, GameController controller) {
    this.batch = batch;
    this.model = model;
    this.controller = controller;

    // Camera + viewport: defines the visible area on screen, independent of map size
    this.camera = new OrthographicCamera();
    this.viewport = new FitViewport(720, 480, camera);

    // Map renderer
    this.mapRenderer = new OrthogonalTiledMapRenderer(model.getMap(), batch);

    // HUD and Debug
    this.hud = new GameHUD();
    this.debugRenderer = new Box2DDebugRenderer();

    // Assets
    TextureManager.load();
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(controller);
    cacheMapSizeInPixels();
    centerCameraOnPlayer(); // Set initial camera position on player
  }

  @Override
  public void render(float delta) {
    if (!model.isPaused()) {
      controller.update(delta);
      model.update(delta);
    }

    // --- CAMERA CENTERED ON PLAYER (clamped to map bounds) ---
    followPlayerCamera();

    ScreenUtils.clear(0, 0, 0, 1);

    mapRenderer.setView(camera);
    mapRenderer.render();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();

    Player player = model.getPlayer();
    if (player != null) {
      if (player.isDead()) batch.setColor(Color.RED);
      drawEntity(TextureManager.get("player"), player.getPosition(), 32, 32);
      batch.setColor(Color.WHITE);
    }

    for (AbstractEnemy enemy : model.getActiveEnemies()) {
      if (enemy.isDead()) continue;

      Texture tex = TextureManager.getEnemyTexture(enemy);
      float size = (enemy instanceof Oblivion) ? 64 : 32;
      drawEntity(tex, enemy.getPosition(), size, size);
    }

    Texture tArrow = TextureManager.get("arrow");
    if (tArrow == null) tArrow = TextureManager.get("player");

    for (Projectile p : model.getProjectiles()) {
      batch.draw(
          tArrow,
          p.getPosition().x - 16,
          p.getPosition().y - 4,
          16,
          4,
          32,
          8,
          1,
          1,
          p.getRotation(),
          0,
          0,
          tArrow.getWidth(),
          tArrow.getHeight(),
          false,
          false);
    }

    batch.end();

    hud.render(batch, player, model.getActiveEnemies());

    if (GameManager.DEBUG_MODE) {
      debugRenderer.render(model.getWorld(), camera.combined);
    }
  }

  private void followPlayerCamera() {
    Player player = model.getPlayer();
    if (player == null) {
      camera.update();
      return;
    }

    Vector2 p = player.getPosition();

    // Half viewport size (taking camera zoom into account)
    float halfW = (camera.viewportWidth * camera.zoom) / 2f;
    float halfH = (camera.viewportHeight * camera.zoom) / 2f;

    float targetX = p.x;
    float targetY = p.y;

    // clamp: prevents camera from going out of bounds
    if (mapPixelWidth > 0 && mapPixelHeight > 0) {
      targetX = MathUtils.clamp(targetX, halfW, Math.max(halfW, mapPixelWidth - halfW));
      targetY = MathUtils.clamp(targetY, halfH, Math.max(halfH, mapPixelHeight - halfH));
    }

    camera.position.set(targetX, targetY, 0);
    camera.update();
  }

  private void centerCameraOnPlayer() {
    Player player = model.getPlayer();
    if (player == null) return;

    Vector2 p = player.getPosition();
    camera.position.set(p.x, p.y, 0);
    camera.update();
  }

  // Cache map dimensions in pixels from Tiled map properties
  private void cacheMapSizeInPixels() {
    if (model.getMap() == null) return;

    MapProperties prop = model.getMap().getProperties();
    int mapWidth = prop.get("width", Integer.class);
    int mapHeight = prop.get("height", Integer.class);
    int tileWidth = prop.get("tilewidth", Integer.class);
    int tileHeight = prop.get("tileheight", Integer.class);

    mapPixelWidth = mapWidth * tileWidth;
    mapPixelHeight = mapHeight * tileHeight;
  }

  // Draw texture centered on entity position
  private void drawEntity(Texture tex, Vector2 pos, float width, float height) {
    if (tex != null) {
      batch.draw(tex, pos.x - width / 2, pos.y - height / 2, width, height);
    }
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {
    if (mapRenderer != null) mapRenderer.dispose();
    if (debugRenderer != null) debugRenderer.dispose();
    if (hud != null) hud.dispose();
  }
}
