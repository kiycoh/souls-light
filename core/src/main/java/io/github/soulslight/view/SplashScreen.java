package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.soulslight.SoulsLightGame;

public final class SplashScreen implements GameState {

  private static final float FPS = 30f;
  private static final int FRAME_COUNT = 440;

  private static final float VIRTUAL_WIDTH = 1280;
  private static final float VIRTUAL_HEIGHT = 720;

  private final SoulsLightGame game;
  private final SpriteBatch batch;

  private OrthographicCamera camera;
  private Viewport viewport;

  private Texture[] textures;
  private Animation<TextureRegion> animation;
  private float stateTime = 0f;

  // audio
  private Music introMusic;
  private Sound slashSound;
  private boolean slashPlayed = false;

  public SplashScreen(SoulsLightGame game) {
    this.game = game;
    this.batch = game.getBatch();
  }

  @Override
  public void show() {

    // camera + viewport
    camera = new OrthographicCamera();
    viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
    viewport.apply();
    camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
    camera.update();

    // loading intro frames
    textures = new Texture[FRAME_COUNT];
    TextureRegion[] regions = new TextureRegion[FRAME_COUNT];

    for (int i = 0; i < FRAME_COUNT; i++) {
      String fileName = String.format("intro/intro_%04d.png", i + 1);
      textures[i] = new Texture(Gdx.files.internal(fileName));
      regions[i] = new TextureRegion(textures[i]);
    }

    animation = new Animation<>(1f / FPS, regions);

    // loading audio assets
    introMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/intro.mp3"));
    introMusic.setLooping(false);
    introMusic.play();

    slashSound = Gdx.audio.newSound(Gdx.files.internal("audio/slash.mp3"));
  }

  @Override
  public void render(float delta) {
    stateTime += delta;

    Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    camera.update();
    batch.setProjectionMatrix(camera.combined);

    TextureRegion frame = animation.getKeyFrame(stateTime, false);

    batch.begin();
    batch.draw(frame, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
    batch.end();

    // "slash" sound delay
    if (!slashPlayed && stateTime >= 14f) {
      slashSound.play();
      slashPlayed = true;
    }

    // sends straight to menu if pressing: SPACE, ENTER or ESC
    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
        || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
        || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      goToMenu();
    }

    // sends to menu after intro animation
    if (animation.isAnimationFinished(stateTime)) {
      goToMenu();
    }
  }

  private void goToMenu() {
    game.setScreen(new MainMenuScreen(game, batch));
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
    for (Texture t : textures) {
      t.dispose();
    }

    slashSound.dispose();
    introMusic.dispose();
  }
}
