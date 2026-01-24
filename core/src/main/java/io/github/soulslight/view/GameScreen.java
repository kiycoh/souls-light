package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.enemies.Oblivion;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.entities.Projectile;
import io.github.soulslight.model.room.Portal;
import io.github.soulslight.model.room.PortalRoom;

public final class GameScreen implements GameState {

  private final SpriteBatch batch;
  private final GameModel model;
  private final GameController controller;

  private final GameHUD hud;
  private final OrthographicCamera camera;
  private final Viewport viewport;
  private final OrthogonalTiledMapRenderer mapRenderer;
  private final Box2DDebugRenderer debugRenderer;

  // Map size in pixel (used for camera clamp)
  private float mapPixelWidth = 0f;
  private float mapPixelHeight = 0f;

  public GameScreen(SpriteBatch batch, GameModel model, GameController controller) {
    this.batch = batch;
    this.model = model;
    this.controller = controller;

    // Camera + viewport: what you see on screen (not map size)
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
    centerCameraOnPlayer(); // posizione iniziale sensata
  }

  @Override
  public void render(float delta) {
    if (!model.isPaused()) {
      controller.update(delta);
      model.update(delta);
    }

    // --- CAMERA CENTERED ON PLAYERS (WITH OOB CLASP) ---
    followPlayersCamera();

    ScreenUtils.clear(0, 0, 0, 1);

    mapRenderer.setView(camera);
    mapRenderer.render();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();

    for (Player player : model.getPlayers()) {
      if (player.isDead()) {
        batch.setColor(Color.RED);
      } else {
        batch.setColor(Color.WHITE);
      }

      String texName = "player";
      switch (player.getType()) {
        case ARCHER:
          texName = "archer";
          break;
        case WARRIOR:
        default:
          texName = "player";
          break;
      }

      drawEntity(TextureManager.get(texName), player.getPosition(), 32, 32);
      batch.setColor(Color.WHITE);
    }

    for (AbstractEnemy enemy : model.getActiveEnemies()) {
      if (enemy.isDead())
        continue;

      Texture tex = TextureManager.getEnemyTexture(enemy);
      float size = (enemy instanceof Oblivion) ? 64 : 32;
      drawEntity(tex, enemy.getPosition(), size, size);
    }

    Texture tArrow = TextureManager.get("arrow");
    if (tArrow == null)
      tArrow = TextureManager.get("player");

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

    // Draw portal
    drawPortal();

    batch.end();

    hud.render(batch, model.getPlayers(), model.getActiveEnemies());

    // Draw portal prompt (on HUD layer)
    drawPortalPrompt();

    if (GameManager.DEBUG_MODE) {
      debugRenderer.render(model.getWorld(), camera.combined);
    }

    // Check for level completion
    checkLevelTransition();
  }

  private void drawPortal() {
    if (model.getLevel() == null || model.getLevel().getRoomManager() == null)
      return;
    PortalRoom portalRoom = model.getLevel().getRoomManager().getPortalRoom();
    if (portalRoom == null || portalRoom.getPortal() == null)
      return;

    Portal portal = portalRoom.getPortal();
    Vector2 pos = portal.getPosition();

    // Use a colored circle as mockup (will be replaced by artist)
    Texture tex = TextureManager.get("player"); // Fallback texture
    if (portal.isPlayerInRange()) {
      batch.setColor(Color.CYAN); // Highlight when player is nearby
    } else {
      batch.setColor(Color.PURPLE); // Normal portal color
    }
    drawEntity(tex, pos, 48, 48);
    batch.setColor(Color.WHITE);
  }

  private void drawPortalPrompt() {
    if (model.getLevel() == null || model.getLevel().getRoomManager() == null)
      return;
    if (!model.getLevel().getRoomManager().isPortalReady())
      return;

    // Simple text prompt at top-center of screen
    batch.begin();
    BitmapFont font = new BitmapFont();
    font.setColor(Color.YELLOW);
    font.draw(batch, "[E] Enter Portal", viewport.getWorldWidth() / 2 - 60, viewport.getWorldHeight() - 20);
    batch.end();
    font.dispose();
  }

  private void checkLevelTransition() {
    if (!model.isLevelCompleted())
      return;

    // Reset flag immediately to prevent multiple triggers
    model.setLevelCompleted(false);

    // Use postRunnable to defer transition until after render cycle completes
    // safely
    Gdx.app.postRunnable(() -> {
      if (GameManager.getInstance().advanceToNextLevel()) {
        Gdx.app.log("GameScreen", "Transitioning to level " + GameManager.getInstance().getCurrentLevelIndex());
        // Create new model and controller for next level
        GameModel newModel = new GameModel();
        GameController newController = new GameController(newModel);
        // Get the Game instance through Gdx.app to switch screens
        if (Gdx.app.getApplicationListener() instanceof com.badlogic.gdx.Game game) {
          game.setScreen(new GameScreen(batch, newModel, newController));
        }
      } else {
        Gdx.app.log("GameScreen", "Campaign complete! All levels finished.");
        // Return to main menu on victory
        if (Gdx.app.getApplicationListener() instanceof io.github.soulslight.SoulsLightGame game) {
          game.setScreen(new MainMenuScreen(game, batch));
        }
      }
    });
  }

  private void followPlayersCamera() {
    java.util.List<Player> players = model.getPlayers();
    if (players.isEmpty()) {
      camera.update();
      return;
    }

    float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
    float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
    int aliveCount = 0;

    for (Player p : players) {
      // Option: Follow dead players too? Usually yes until game over.
      Vector2 pos = p.getPosition();
      if (pos.x < minX)
        minX = pos.x;
      if (pos.y < minY)
        minY = pos.y;
      if (pos.x > maxX)
        maxX = pos.x;
      if (pos.y > maxY)
        maxY = pos.y;
      aliveCount++;
    }

    if (aliveCount == 0) {
      camera.update();
      return;
    }

    float targetX = (minX + maxX) / 2f;
    float targetY = (minY + maxY) / 2f;

    // half viewport (takes in consideration zoom)
    float halfW = (camera.viewportWidth * camera.zoom) / 2f;
    float halfH = (camera.viewportHeight * camera.zoom) / 2f;

    // clamp: prevents camera from going out of bounds
    if (mapPixelWidth > 0 && mapPixelHeight > 0) {
      targetX = MathUtils.clamp(targetX, halfW, Math.max(halfW, mapPixelWidth - halfW));
      targetY = MathUtils.clamp(targetY, halfH, Math.max(halfH, mapPixelHeight - halfH));
    }

    // Smooth camera could be added here (lerp), but instant is fine for now
    camera.position.set(targetX, targetY, 0);
    camera.update();
  }

  private void centerCameraOnPlayer() {
    java.util.List<Player> players = model.getPlayers();
    if (players.isEmpty())
      return;

    // Just center on first player for initial spawn or calculate average
    Player p = players.get(0);
    camera.position.set(p.getPosition().x, p.getPosition().y, 0);
    camera.update();
  }

  private void cacheMapSizeInPixels() {
    if (model.getMap() == null)
      return;

    MapProperties prop = model.getMap().getProperties();
    int mapWidth = prop.get("width", Integer.class);
    int mapHeight = prop.get("height", Integer.class);
    int tileWidth = prop.get("tilewidth", Integer.class);
    int tileHeight = prop.get("tileheight", Integer.class);

    mapPixelWidth = mapWidth * tileWidth;
    mapPixelHeight = mapHeight * tileHeight;
  }

  // Center draw
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
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void hide() {
  }

  @Override
  public void dispose() {
    if (mapRenderer != null)
      mapRenderer.dispose();
    if (debugRenderer != null)
      debugRenderer.dispose();
    if (hud != null)
      hud.dispose();
  }
}
