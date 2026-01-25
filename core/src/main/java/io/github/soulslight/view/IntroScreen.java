package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.soulslight.SoulsLightGame;
import io.github.soulslight.controller.GameController;
import io.github.soulslight.manager.AudioManager;
import io.github.soulslight.model.GameModel;

public final class IntroScreen implements GameState {

  private static final float INTRO_DELAY = 1.2f;
  private static final float LINE_FADE_DURATION = 2.5f;
  private static final float LINE_DELAY = 2.1f;
  private static final float TRANSITION_DURATION = 7.2f;

  private final SoulsLightGame game;
  private final SpriteBatch batch;
  private final GameModel model;
  private final GameController controller;

  private final BitmapFont font;
  private final OrthographicCamera camera;
  private final GlyphLayout layout;

  private final String[] lines;
  private final float totalDuration;
  private final float lineSpacing;

  private float elapsedTime = 0f;
  private boolean finished = false;

  private boolean transitioning = false;
  private float transitionTime = 0f;

  // private Music transitionMusic; // Removed in favor of AudioManager

  public IntroScreen(
      SoulsLightGame game, SpriteBatch batch, GameModel model, GameController controller) {
    this.game = game;
    this.batch = batch;
    this.model = model;
    this.controller = controller;

    this.font = new BitmapFont();
    this.font.setColor(Color.WHITE);
    this.font.getData().setScale(2.1f);

    this.layout = new GlyphLayout();

    this.camera = new OrthographicCamera();
    this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    this.lines =
        new String[] {
          "After death, there is neither heaven nor hell.",
          "",
          "Contrary to human belief, souls are drawn into the Mu,",
          "an empty and unstable dimension.",
          "",
          "In the Mu, death is no longer possible.",
          "",
          "Condemned to an eternal existence, many souls lose themselves",
          "and become mindless creatures.",
          "",
          "Among them, two souls choose to descend into the darkest depths of the Mu,",
          "searching for a way to escape...",
          "",
          "",
          "Click to continue..."
        };

    this.lineSpacing = font.getLineHeight() * 1.2f;

    this.totalDuration = INTRO_DELAY + (lines.length - 1) * LINE_DELAY + LINE_FADE_DURATION;
  }

  private void goToGame() {
    AudioManager.getInstance().setNextFadeDuration(6.0f);
    game.setScreen(new GameScreen(batch, model, controller));
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(null);
  }

  @Override
  public void render(float delta) {

    if (transitioning) {
      transitionTime += delta;
      if (transitionTime >= TRANSITION_DURATION) {
        goToGame();
        return;
      }
    } else {
      if (!finished) {
        elapsedTime += delta;
        if (elapsedTime >= totalDuration) {
          elapsedTime = totalDuration;
          finished = true;
        }
      }

      // Intro skip
      if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
          || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
          || Gdx.input.justTouched()) {
        if (!finished) {
          elapsedTime = totalDuration;
          finished = true;
        } else {
          // Start game once full text is on screen
          transitioning = true;
          transitionTime = 0f;
          AudioManager.getInstance().playMusic("audio/toExploration_reverbered.mp3", false);
        }
      }
    }

    float bg = transitioning ? Math.min(1f, transitionTime / TRANSITION_DURATION) : 0f;

    Gdx.gl.glClearColor(bg, bg, bg, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    camera.update();
    batch.setProjectionMatrix(camera.combined);

    batch.begin();

    float screenWidth = Gdx.graphics.getWidth();
    float screenHeight = Gdx.graphics.getHeight();

    float totalHeight = lineSpacing * lines.length;
    float startY = (screenHeight + totalHeight) / 2f;

    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];

      float lineStartTime = INTRO_DELAY + i * LINE_DELAY;
      float lineTime = elapsedTime - lineStartTime;

      if (lineTime <= 0f) {
        continue;
      }

      float alpha;
      if (finished || transitioning) {
        alpha = 1f;
      } else {
        alpha = Math.min(1f, lineTime / LINE_FADE_DURATION);
      }

      if (line.isEmpty()) {
        continue;
      }

      font.setColor(1f, 1f, 1f, alpha);
      layout.setText(font, line);

      float x = (screenWidth - layout.width) / 2f;
      float y = startY - i * lineSpacing;

      font.draw(batch, layout, x, y);
    }

    font.setColor(1f, 1f, 1f, 1f);
    batch.end();
  }

  @Override
  public void resize(int width, int height) {
    camera.setToOrtho(false, width, height);
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {
    font.dispose();
    // AudioManager.getInstance().stopMusic(); // Optional cleanup if needed
  }
}
