package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
          drawEntity(frame, enemy.getPosition(), 32, 54, flipX); // 27px -> 54px
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

    hud.render(batch, model.getPlayers(), model.getActiveEnemies());

    if (GameManager.DEBUG_MODE) {
      debugRenderer.render(model.getWorld(), camera.combined);
    }
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

  private boolean shouldFlipXStable(AbstractEnemy enemy) {
    boolean facingRight = enemyFacingRight.computeIfAbsent(enemy, e -> true);

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

  // Center draw
  private void drawEntity(Texture tex, Vector2 pos, float width, float height) {
    if (tex != null) {
      batch.draw(tex, pos.x - width / 2, pos.y - height / 2, width, height);
    }
  }

  private void drawEntity(
      TextureRegion region, Vector2 pos, float width, float height, boolean flipX) {
    if (region == null) return;

    float x = pos.x - width / 2f;
    float y = pos.y - height / 2f;

    if (!flipX) {
      batch.draw(region, x, y, width, height);
    } else {
      batch.draw(region, x + width, y, -width, height);
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
    enemyAnimOffset.clear();
    enemyFacingRight.clear();
  }
}
