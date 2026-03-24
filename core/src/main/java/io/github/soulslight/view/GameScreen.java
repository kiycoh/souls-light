package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.soulslight.controller.GameController;
import io.github.soulslight.debug.DebugMenuController;
import io.github.soulslight.debug.DebugMenuOverlay;
import io.github.soulslight.debug.commands.HealToFullCommand;
import io.github.soulslight.debug.commands.KillNearbyEnemiesCommand;
import io.github.soulslight.debug.commands.RegenerateMapCommand;
import io.github.soulslight.debug.commands.SkipLevelCommand;
import io.github.soulslight.debug.commands.SkipRoomCommand;
import io.github.soulslight.debug.commands.SkipToBossCommand;
import io.github.soulslight.debug.commands.TeleportToPortalCommand;
import io.github.soulslight.debug.commands.ToggleHitboxesCommand;
import io.github.soulslight.debug.commands.ToggleInvincibilityCommand;
import io.github.soulslight.manager.AudioManager;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.manager.TextureManager;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.ItemEntity;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.map.LevelFactory;
import io.github.soulslight.model.observer.Observer;
import io.github.soulslight.view.audio.MusicController;
import io.github.soulslight.view.render.EntityRenderer;

public final class GameScreen implements GameState, Observer {

  private final SpriteBatch batch;
  private final GameModel model;
  private final GameController controller;

  private final GameHUD hud;
  private final OrthographicCamera camera;
  private final Viewport viewport;
  private final OrthogonalTiledMapRenderer mapRenderer;
  private final Box2DDebugRenderer debugRenderer;

  private final MusicController musicController;

  // Map size in pixel (used for camera clamp)
  private float mapPixelWidth = 0f;
  private float mapPixelHeight = 0f;

  // Debug menu components
  private DebugMenuController debugMenuController;
  private DebugMenuOverlay debugMenuOverlay;

  private PauseMenuOverlay pauseMenuOverlay;
  private final BitmapFont promptFont;

  private final LightingRenderer lightingRenderer;
  private final ParticleRenderSystem particleRenderSystem;
  private final EntityRenderer entityRenderer;

  // Outro Overlay
  private OutroOverlay outroOverlay;
  private boolean showingOutro = false;

  public GameScreen(SpriteBatch batch, GameModel model, GameController controller) {
    this.batch = batch;
    this.model = model;
    this.controller = controller;

    // Pass GameScreen instance to controller so it can trigger pause UI switch
    this.controller.setGameScreen(this);

    // Camera + viewport: what you see on screen (not map size)
    this.camera = new OrthographicCamera();
    this.viewport =
        new FitViewport(
            io.github.soulslight.model.Constants.V_WIDTH,
            io.github.soulslight.model.Constants.V_HEIGHT,
            camera);

    // Map renderer
    this.mapRenderer = new OrthogonalTiledMapRenderer(model.getMap(), batch);

    // HUD and Debug
    this.hud = new GameHUD();
    this.debugRenderer = new Box2DDebugRenderer();
    this.lightingRenderer = new LightingRenderer();
    this.particleRenderSystem = new ParticleRenderSystem();
    this.entityRenderer = new EntityRenderer(batch);
    this.promptFont = new BitmapFont();

    // Pause Menu
    this.pauseMenuOverlay = new PauseMenuOverlay(batch, this);

    // Outro Overlay
    this.outroOverlay = new OutroOverlay(batch);

    // Observer Registration
    model.attach(this);

    // Debug Menu Setup (only when DEBUG_MODE is enabled)
    if (GameManager.DEBUG_MODE) {
      initializeDebugMenu();
    }

    // Assets
    TextureManager.getInstance().load();

    // Music Controller
    this.musicController = new MusicController();
  }

  /** Initializes the debug menu with all available commands. */
  private void initializeDebugMenu() {
    this.debugMenuController = new DebugMenuController();
    this.debugMenuOverlay = new DebugMenuOverlay(debugMenuController, model);

    // Register all debug commands
    debugMenuController.registerCommand(new SkipRoomCommand(model));
    debugMenuController.registerCommand(new KillNearbyEnemiesCommand(model));
    debugMenuController.registerCommand(new SkipLevelCommand(model));
    debugMenuController.registerCommand(new SkipToBossCommand(model));
    debugMenuController.registerCommand(new ToggleInvincibilityCommand(model));
    debugMenuController.registerCommand(new HealToFullCommand(model));
    debugMenuController.registerCommand(new TeleportToPortalCommand(model));
    debugMenuController.registerCommand(new ToggleHitboxesCommand());
    debugMenuController.registerCommand(new RegenerateMapCommand(model));

    // Wire to controller
    controller.setDebugMenuController(debugMenuController);
  }

  @Override
  public void show() {
    AudioManager.getInstance().stopMusic(); // Ensure menu music stops
    Gdx.input.setInputProcessor(controller);
    cacheMapSizeInPixels();
    centerCameraOnPlayer();

    boolean isBossLevel =
        GameManager.getInstance().getCurrentLevelIndex() == LevelFactory.getStoryModeLevelCount();

    if (isBossLevel && !musicController.isBossCrossfadeStarted()) {
      musicController.startBossCrossfade();
    }

    musicController.playExplorationMusic();
    musicController.playBossMusic();
    musicController.updateVolume();
  }

  @Override
  public void render(float delta) {
    if (!model.isPaused() && !showingOutro) {
      controller.update(delta);
      model.update(delta);
    }

    musicController.updateBossCrossfade(delta);

    // Ensure volume stays synced if settings changed (e.g. returning from settings)
    musicController.updateVolume();

    if (showingOutro) {
      musicController.updateFadeOut(delta);
    }

    // --- CAMERA CENTERED ON PLAYERS (WITH OOB CLASP) ---
    followPlayersCamera();

    ScreenUtils.clear(0, 0, 0, 1);

    mapRenderer.setView(camera);
    mapRenderer.render();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();

    // Draw particles for locked doors
    particleRenderSystem.renderDoorParticles(batch, model, delta);

    // Update global particles
    io.github.soulslight.manager.ParticleManager.getInstance().update(delta);

    // Update animation timers in EntityRenderer
    entityRenderer.updateAnimationTimers(delta);

    // Render Players
    int playerIndex = 0;
    for (Player player : model.getPlayers()) {
      batch.setColor(player.isDead() ? Color.RED : Color.WHITE);
      entityRenderer.renderPlayer(player, playerIndex);
      batch.setColor(Color.WHITE);
      playerIndex++;
    }

    // Render Enemies
    java.util.List<Player> players = model.getPlayers();
    for (AbstractEnemy enemy : model.getActiveEnemies()) {
      if (enemy.isDead() || !enemy.isSpawned()) {
        entityRenderer.removeEnemyState(enemy);
        continue;
      }
      entityRenderer.renderEnemy(enemy, players);
    }

    // ITEM RENDERING
    if (model.getLevel() != null) {
      for (ItemEntity item : model.getLevel().getItems()) {
        if (item.getBody() != null
            && item.getItem() instanceof io.github.soulslight.model.items.IRenderableItem) {
          TextureRegion reg =
              ((io.github.soulslight.model.items.IRenderableItem) item.getItem()).getTexture();
          if (reg != null) {
            entityRenderer.drawEntity(reg, item.getPosition(), 24f, 24f, false);
          }
        }
      }
    }

    // Draw Projectiles (Sprites or Particles)
    particleRenderSystem.renderProjectiles(batch, model, delta);

    // Draw portal
    particleRenderSystem.renderPortal(batch, model);

    // Render global particles
    io.github.soulslight.manager.ParticleManager.getInstance().render(batch);

    batch.end();

    // Draw Lighting Overlay (over sprites, under HUD)
    lightingRenderer.render(model.getLightingSystem(), model.getMap(), camera.combined);

    hud.render(batch, model);

    // Draw portal prompt (on HUD layer)
    drawPortalPrompt();

    // Pause Menu Overlay
    // Only show if paused AND Debug Menu is NOT visible
    boolean debugVisible = debugMenuController != null && debugMenuController.isVisible();
    if (model.isPaused() && !debugVisible && pauseMenuOverlay != null && !showingOutro) {
      pauseMenuOverlay.render(delta);
    }

    if (GameManager.DEBUG_MODE && GameManager.SHOW_HITBOXES) {
      debugRenderer.render(model.getWorld(), camera.combined);
    }

    // Render debug menu overlay (on top of everything)
    if (debugMenuOverlay != null && GameManager.DEBUG_MODE) {
      debugMenuOverlay.render(batch);
    }

    // Outro Overlay
    if (showingOutro) {
      boolean outroDone = outroOverlay.render(delta);
      if (outroDone) {
        returnToMainMenu();
      }
    } else {
      // Only check for level completion if NOT already showing outro
    }
  }

  private void drawPortalPrompt() {
    if (model.getLevel() == null) return;

    boolean playerNearPortal = false;

    // Check dungeon-style PortalRoom first
    if (model.getLevel().getRoomManager() != null
        && model.getLevel().getRoomManager().isPortalReady()) {
      playerNearPortal = true;
    }

    // Check cave-style direct portal
    if (!playerNearPortal
        && model.getLevel().getCavePortal() != null
        && model.getLevel().getCavePortal().isPlayerInRange()) {
      playerNearPortal = true;
    }

    if (!playerNearPortal) return;

    // Simple text prompt at top-center of screen
    batch.begin();
    promptFont.setColor(Color.YELLOW);
    promptFont.draw(
        batch,
        "[E] Enter Portal",
        viewport.getWorldWidth() / 2 - 60,
        viewport.getWorldHeight() - 20);
    batch.end();
  }

  private void checkLevelTransition() {
    if (!model.isLevelCompleted()) return;

    // Reset flag immediately to prevent multiple triggers
    model.setLevelCompleted(false);

    // Use postRunnable to defer transition until after render cycle completes
    // safely
    Gdx.app.postRunnable(
        () -> {
          if (GameManager.getInstance().advanceToNextLevel()) {
            Gdx.app.log(
                "GameScreen",
                "Transitioning to level " + GameManager.getInstance().getCurrentLevelIndex());

            // CRITICAL FIX: Dispose current screen resources to prevent leaks and
            // controller conflicts
            dispose();

            // Create new model and controller for next level
            GameModel newModel = new GameModel();
            GameController newController = new GameController(newModel);
            // Get the Game instance through Gdx.app to switch screens
            if (Gdx.app.getApplicationListener() instanceof com.badlogic.gdx.Game game) {
              game.setScreen(new GameScreen(batch, newModel, newController));
            }
          } else {
            Gdx.app.log("GameScreen", "Campaign complete! Triggering Outro.");
            // Campaign Finished -> Trigger Outro Overlay
            model.setPaused(true); // Stop game logic
            outroOverlay.start();
            showingOutro = true;
            // Feature: Stop music immediately on victory
            musicController.pause();
          }
        });
  }

  public void returnToMainMenu() {
    if (Gdx.app.getApplicationListener() instanceof io.github.soulslight.SoulsLightGame game) {

      // Stop and dispose music
      musicController.dispose();

      // Dispose of the current screen to clean up controller listeners and resources
      dispose();

      game.setScreen(new MainMenuScreen(game, batch));
    }
  }

  private void followPlayersCamera() {
    java.util.List<Player> players = model.getPlayers();
    if (players.isEmpty()) {
      camera.update();
      return;
    }

    float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
    float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;

    for (Player p : players) {
      // Option: Follow dead players too? Usually yes until game over.
      Vector2 pos = p.getPosition();
      minX = Math.min(minX, pos.x);
      minY = Math.min(minY, pos.y);
      maxX = Math.max(maxX, pos.x);
      maxY = Math.max(maxY, pos.y);
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
    if (players.isEmpty()) return;

    // Just center on first player for initial spawn or calculate average
    Player p = players.get(0);
    camera.position.set(p.getPosition().x, p.getPosition().y, 0);
    camera.update();
  }

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

  public void resumeGame() {
    model.setPaused(false);
    Gdx.input.setInputProcessor(controller);
  }

  public SpriteBatch getBatch() {
    return batch;
  }

  public void updateInputMode() {
    if (model.isPaused()) {
      Gdx.input.setInputProcessor(pauseMenuOverlay.getStage());
    } else {
      Gdx.input.setInputProcessor(controller);
    }
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);
    if (pauseMenuOverlay != null) pauseMenuOverlay.resize(width, height);
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {
    // CRITICAL FIX: Do NOT call dispose() here.
    // hide() is called when switching to Settings/Pause, but we want to keep the
    // game state alive.
    musicController.pause();
  }

  @Override
  public void dispose() {
    // Dispose controller to unregister listener!
    if (controller != null) controller.dispose();

    if (model != null) model.dispose();

    if (mapRenderer != null) mapRenderer.dispose();
    if (debugRenderer != null) debugRenderer.dispose();
    if (hud != null) hud.dispose();
    if (lightingRenderer != null) lightingRenderer.dispose();
    if (debugMenuOverlay != null) debugMenuOverlay.dispose();
    if (pauseMenuOverlay != null) pauseMenuOverlay.dispose();
    if (promptFont != null) promptFont.dispose();

    if (musicController != null) musicController.dispose();

    io.github.soulslight.manager.ParticleManager.getInstance().clear();
  }

  @Override
  public void update(String eventType, Object data) {
    if ("LEVEL_COMPLETE".equals(eventType)) {
      checkLevelTransition();
    } else if ("LEVEL_RESTORED".equals(eventType)) {
      // Clear legacy visual effects from previous state
      if (particleRenderSystem != null) {
        particleRenderSystem.dispose();
      }

      // Update Map Renderer with new TiledMap
      if (data instanceof io.github.soulslight.model.map.Level) {
        io.github.soulslight.model.map.Level restoredLevel =
            (io.github.soulslight.model.map.Level) data;
        if (mapRenderer != null && restoredLevel.getMap() != null) {
          mapRenderer.setMap(restoredLevel.getMap());
        }
      }

    } else if ("PLAYER_HIT".equals(eventType)
        && data instanceof io.github.soulslight.model.entities.Player) {
      io.github.soulslight.model.entities.Player p =
          (io.github.soulslight.model.entities.Player) data;
      io.github.soulslight.manager.ParticleManager.getInstance()
          .spawn(io.github.soulslight.model.particles.ParticleType.BLOOD, p.getPosition());
    }
  }
}
