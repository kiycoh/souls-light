package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import io.github.soulslight.debug.commands.*;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.manager.TextureManager;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.enemies.Chaser;
import io.github.soulslight.model.enemies.Oblivion;
import io.github.soulslight.model.enemies.Ranger;
import io.github.soulslight.model.enemies.Shielder;
import io.github.soulslight.model.enemies.SpikedBall;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.entities.Projectile;
import io.github.soulslight.model.room.Portal;
import io.github.soulslight.model.room.PortalRoom;
import java.util.IdentityHashMap;
import java.util.Map;

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

  private float enemyAnimTime = 0f;
  private final Map<AbstractEnemy, Float> enemyAnimOffset = new IdentityHashMap<>();

  // Used to stop animation
  private static final float IDLE_VELOCITY_EPS = 0.05f;

  // Used to limit excessive animation flipping
  private static final float ENEMY_FLIP_EPS = 0.35f;
  private final Map<AbstractEnemy, Boolean> enemyFacingRight = new IdentityHashMap<>();

  private static final float OBLIVION_HEIGHT = 96f * 5f;
  private static final float OBLIVION_WIDTH = 173f * 5f; // may have to be tweaked later

  private static final float OBLIVION_Y_OFFSET = 80f;

  // Debug menu components
  private DebugMenuController debugMenuController;
  private DebugMenuOverlay debugMenuOverlay;

  private PauseMenuOverlay pauseMenuOverlay;
  private final BitmapFont promptFont;

  private Texture blankTexture;

  public GameScreen(SpriteBatch batch, GameModel model, GameController controller) {
    this.batch = batch;
    this.model = model;
    this.controller = controller;

    // Pass GameScreen instance to controller so it can trigger pause UI switch
    this.controller.setGameScreen(this);

    // Camera + viewport: what you see on screen (not map size)
    this.camera = new OrthographicCamera();
    this.viewport = new FitViewport(
        io.github.soulslight.model.Constants.V_WIDTH,
        io.github.soulslight.model.Constants.V_HEIGHT,
        camera);

    // Map renderer
    this.mapRenderer = new OrthogonalTiledMapRenderer(model.getMap(), batch);

    // HUD and Debug
    this.hud = new GameHUD();
    this.debugRenderer = new Box2DDebugRenderer();
    this.promptFont = new BitmapFont();

    // Pause Menu
    this.pauseMenuOverlay = new PauseMenuOverlay(batch, this);

    // Debug Menu Setup (only when DEBUG_MODE is enabled)
    if (GameManager.DEBUG_MODE) {
      initializeDebugMenu();
    }

    // Assets
    TextureManager.load();
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
    Gdx.input.setInputProcessor(controller);
    cacheMapSizeInPixels();
    centerCameraOnPlayer();
  }

  @Override
  public void render(float delta) {
    if (!model.isPaused()) {
      controller.update(delta);
      model.update(delta);
    }

    enemyAnimTime += delta;

    // --- CAMERA CENTERED ON PLAYERS (WITH OOB CLASP) ---
    followPlayersCamera();

    ScreenUtils.clear(0, 0, 0, 1);

    mapRenderer.setView(camera);
    mapRenderer.render();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();

    for (Player player : model.getPlayers()) {
      batch.setColor(player.isDead() ? Color.RED : Color.WHITE);
      String texName = "player";
      drawEntity(TextureManager.get(texName), player.getPosition(), 32, 32);
      batch.setColor(Color.WHITE);
    }

    for (AbstractEnemy enemy : model.getActiveEnemies()) {
      if (enemy.isDead()) {
        enemyAnimOffset.remove(enemy);
        enemyFacingRight.remove(enemy);
        continue;
      }

      boolean flipX = shouldFlipXStable(enemy);

      if (enemy instanceof Oblivion) {
        TextureRegion frame = computeOblivionFrame((Oblivion) enemy);
        if (frame != null) {
          // Oblivion spritesheet needs to be flipped
          boolean flipOblivion = !flipX;
          drawOblivion(frame, enemy.getPosition(), flipOblivion);
          continue;
        }
      }

      if (enemy instanceof Chaser) {
        TextureRegion frame = computeAnimatedFrame(enemy, EnemyAnimType.CHASER);
        if (frame != null) {
          drawEntity(frame, enemy.getPosition(), 32, 46, flipX);
          continue;
        }
      }

      if (enemy instanceof Ranger) {
        TextureRegion frame = computeAnimatedFrame(enemy, EnemyAnimType.RANGER);
        if (frame != null) {
          drawEntity(frame, enemy.getPosition(), 32, 46, flipX);
          continue;
        }
      }

      if (enemy instanceof Shielder) {
        TextureRegion frame = computeAnimatedFrame(enemy, EnemyAnimType.SHIELDER);
        if (frame != null) {
          drawEntity(frame, enemy.getPosition(), 32, 54, flipX);
          continue;
        }
      }

      if (enemy instanceof SpikedBall) {
        SpikedBall sb = (SpikedBall) enemy;
        TextureRegion frame;

        if (sb.isCharging()) {
          float offset = enemyAnimOffset.computeIfAbsent(enemy, e -> MathUtils.random(0f, 10f));
          frame = TextureManager.getSpikedBallChargeFrame(enemyAnimTime + offset);
        } else {
          frame = computeAnimatedFrame(enemy, EnemyAnimType.SPIKEDBALL);
        }

        if (frame != null) {
          drawEntity(frame, enemy.getPosition(), 64, 64, flipX);
          continue;
        }
      }

      Texture tex = TextureManager.getEnemyTexture(enemy);
      float size = (enemy instanceof Oblivion) ? OBLIVION_HEIGHT : 32f; // fallback in case of missing anim
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

    hud.render(batch, model);

    // Draw portal prompt (on HUD layer)
    drawPortalPrompt();

    // Pause Menu Overlay
    // Only show if paused AND Debug Menu is NOT visible
    boolean debugVisible = debugMenuController != null && debugMenuController.isVisible();
    if (model.isPaused() && !debugVisible && pauseMenuOverlay != null) {
      pauseMenuOverlay.render(delta);
    }

    if (GameManager.DEBUG_MODE && GameManager.SHOW_HITBOXES) {
      debugRenderer.render(model.getWorld(), camera.combined);
    }

    // Render debug menu overlay (on top of everything)
    if (debugMenuOverlay != null && GameManager.DEBUG_MODE) {
      debugMenuOverlay.render(batch);
    }

    // Check for level completion
    checkLevelTransition();
  }

  private void drawPortal() {
    if (model.getLevel() == null)
      return;

    Portal portal = null;

    // Check for dungeon-style PortalRoom first
    if (model.getLevel().getRoomManager() != null) {
      PortalRoom portalRoom = model.getLevel().getRoomManager().getPortalRoom();
      if (portalRoom != null && portalRoom.getPortal() != null) {
        portal = portalRoom.getPortal();
      }
    }

    // Fall back to cave-style direct portal
    if (portal == null) {
      portal = model.getLevel().getCavePortal();
    }

    if (portal == null)
      return;

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
    if (model.getLevel() == null)
      return;

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

    if (!playerNearPortal)
      return;

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
    if (!model.isLevelCompleted())
      return;

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

  private enum EnemyAnimType {
    CHASER,
    RANGER,
    SHIELDER,
    SPIKEDBALL
  }

  private TextureRegion computeAnimatedFrame(AbstractEnemy enemy, EnemyAnimType type) {
    boolean isIdle = true;

    if (enemy.getBody() != null) {
      Vector2 vel = enemy.getBody().getLinearVelocity();
      isIdle = vel.len2() < IDLE_VELOCITY_EPS * IDLE_VELOCITY_EPS;
    }

    if (isIdle) {
      return getAnimFrame(type, 0f);
    }

    float offset = enemyAnimOffset.computeIfAbsent(enemy, e -> MathUtils.random(0f, 10f));
    return getAnimFrame(type, enemyAnimTime + offset);
  }

  private TextureRegion getAnimFrame(EnemyAnimType type, float time) {
    switch (type) {
      case CHASER:
        return TextureManager.getChaserWalkFrame(time);
      case RANGER:
        return TextureManager.getRangerWalkFrame(time);
      case SHIELDER:
        return TextureManager.getShielderWalkFrame(time);
      case SPIKEDBALL:
        return TextureManager.getSpikedBallWalkFrame(time);
      default:
        return null;
    }
  }

  private TextureRegion computeOblivionFrame(Oblivion boss) {
    if (boss.isDying()) {
      float t = boss.getDeathAnimTime();
      float duration = Oblivion.getDeathAnimDuration();
      if (t > duration)
        t = duration;
      return TextureManager.getOblivionDeathFrame(t);
    }

    if (boss.isTeleportingOut() || boss.isTeleportingIn()) {
      float t = boss.getTeleportAnimTime();
      float duration = Oblivion.getTeleportAnimDuration();
      if (t > duration)
        t = duration;

      float animTime;
      if (boss.isTeleportingOut()) {
        animTime = Math.max(0f, duration - t);
      } else {
        animTime = t;
      }

      return TextureManager.getOblivionTeleportFrame(animTime);
    }

    float offset = enemyAnimOffset.computeIfAbsent(boss, e -> MathUtils.random(0f, 10f));
    float time = enemyAnimTime + offset;

    if (boss.isMeleeWindup()) {
      return TextureManager.getOblivionMeleeWindupFrame(time);
    }

    if (boss.isMeleeAttacking()) {
      return TextureManager.getOblivionMeleeAttackFrame(time);
    }

    boolean isIdle = true;
    if (boss.getBody() != null) {
      Vector2 vel = boss.getBody().getLinearVelocity();
      isIdle = vel.len2() < IDLE_VELOCITY_EPS * IDLE_VELOCITY_EPS;
    }

    if (isIdle) {
      if (boss.isPhaseTwo()) {
        return TextureManager.getOblivionSpellFrame(time);
      } else {
        return TextureManager.getOblivionIdleFrame(time);
      }
    } else {
      return TextureManager.getOblivionWalkFrame(time);
    }
  }

  private boolean shouldFlipXStable(AbstractEnemy enemy) {
    boolean facingRight = enemyFacingRight.computeIfAbsent(enemy, e -> true);

    if (enemy instanceof Oblivion) {
      Oblivion ob = (Oblivion) enemy;

      // Locks animation direction in set states
      if (ob.isMeleeWindup()
          || ob.isMeleeAttacking()
          || ob.isTeleportingOut()
          || ob.isTeleportingIn()
          || ob.isDying()) {
        return !facingRight;
      }

      // else, flips towards neaest player
      java.util.List<Player> players = model.getPlayers();
      if (!players.isEmpty()) {
        Player nearest = players.get(0);
        float bestDist2 = nearest.getPosition().dst2(ob.getPosition());
        for (int i = 1; i < players.size(); i++) {
          Player p = players.get(i);
          float d2 = p.getPosition().dst2(ob.getPosition());
          if (d2 < bestDist2) {
            bestDist2 = d2;
            nearest = p;
          }
        }

        float dx = nearest.getPosition().x - ob.getPosition().x;
        float EPS_X = 4f;
        if (dx > EPS_X) {
          facingRight = true;
        } else if (dx < -EPS_X) {
          facingRight = false;
        }
        enemyFacingRight.put(enemy, facingRight);
      }

      return !facingRight;
    }

    if (enemy.getBody() == null) {
      return !facingRight;
    }

    float vx = enemy.getBody().getLinearVelocity().x;

    if (vx > ENEMY_FLIP_EPS) {
      facingRight = true;
      enemyFacingRight.put(enemy, true);
    } else if (vx < -ENEMY_FLIP_EPS) {
      facingRight = false;
      enemyFacingRight.put(enemy, false);
    }

    return !facingRight;
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

  // Center draw
  private void drawEntity(Texture tex, Vector2 pos, float width, float height) {
    if (tex != null) {
      batch.draw(tex, pos.x - width / 2, pos.y - height / 2, width, height);
    }
  }

  private void drawEntity(
      TextureRegion region, Vector2 pos, float width, float height, boolean flipX) {
    if (region == null)
      return;

    float x = pos.x - width / 2f;
    float y = pos.y - height / 2f;

    if (!flipX) {
      batch.draw(region, x, y, width, height);
    } else {
      batch.draw(region, x + width, y, -width, height);
    }
  }

  private void drawOblivion(TextureRegion region, Vector2 pos, boolean flipX) {
    if (region == null)
      return;

    float width = OBLIVION_WIDTH;
    float height = OBLIVION_HEIGHT;

    float x = pos.x - width / 2f;
    // Matching sprite to hitbox
    float y = pos.y - height / 2f + OBLIVION_Y_OFFSET;

    if (!flipX) {
      batch.draw(region, x, y, width, height);
    } else {
      batch.draw(region, x + width, y, -width, height);
    }
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);
    if (pauseMenuOverlay != null)
      pauseMenuOverlay.resize(width, height);
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
    if (debugMenuOverlay != null)
      debugMenuOverlay.dispose();
    if (pauseMenuOverlay != null)
      pauseMenuOverlay.dispose();
    if (promptFont != null)
      promptFont.dispose();
    enemyAnimOffset.clear();
    enemyFacingRight.clear();
  }
}
