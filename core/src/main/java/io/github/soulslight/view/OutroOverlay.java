package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import io.github.soulslight.manager.AudioManager;

/**
 * A UI component that renders the Outro sequence as an overlay. Reuses the logic from IntroScreen
 * but adapted for overlay rendering.
 */
public class OutroOverlay implements Disposable {

  private static final float INTRO_DELAY = 1.0f;
  private static final float LINE_FADE_DURATION = 2.5f;
  private static final float LINE_DELAY = 2.0f;
  private static final float TRANSITION_DURATION = 4f;

  private final SpriteBatch batch;
  private final ShapeRenderer shapeRenderer; // For the dark background
  private final BitmapFont font;
  private final GlyphLayout layout;

  private final String[] lines;
  private final float totalDuration;
  private final float lineSpacing;

  private float elapsedTime = 0f;
  private boolean finished = false;
  private boolean transitioning = false; // Transitioning OUT of the outro (to menu)
  private float transitionTime = 0f;

  // private final Music transitionMusic; // Removed in favor of AudioManager

  private OrthographicCamera camera; // Use own camera for UI to be independent of game zoom

  public OutroOverlay(SpriteBatch batch) {
    this.batch = batch;
    this.shapeRenderer = new ShapeRenderer();

    this.font = new BitmapFont();
    this.font.setColor(Color.WHITE);
    this.font.getData().setScale(2.1f);

    this.layout = new GlyphLayout();

    this.camera = new OrthographicCamera();
    this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    this.lines =
        new String[] {
          "The two souls have reached the depths of the Mu",
          "and defeated its sorrowful ruler,",
          "",
          "the entity that shaped the dimension itself: Oblivion.",
          "is now gone.",
          "",
          "Yet one fundamental law remains unchanged:",
          "there is no return from death.",
          "",
          "The Mu, however, is no longer without a ruler.",
          "It now has two new kings.",
          "",
          "Click to continue..."
        };

    this.lineSpacing = font.getLineHeight() * 1.2f;
    this.totalDuration = INTRO_DELAY + (lines.length - 1) * LINE_DELAY + LINE_FADE_DURATION + 3f;
  }

  public void start() {
    AudioManager.getInstance().playMusic("audio/end_reverbered.mp3", false);
  }

  /**
   * Renders the outro overlay.
   *
   * @param delta Time delta
   * @return true if the outro is finished and we should exit to menu
   */
  public boolean render(float delta) {
    // Update logic
    if (transitioning) {
      transitionTime += delta;
      if (transitionTime >= TRANSITION_DURATION) {
        return true; // Done
      }
    } else {
      if (!finished) {
        elapsedTime += delta;
        if (elapsedTime >= totalDuration) {
          elapsedTime = totalDuration;
          finished = true;
        }
      }

      // Skip input
      if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
          || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
          || Gdx.input.justTouched()) {
        if (!finished) {
          elapsedTime = totalDuration;
          finished = true;
        } else {
          startTransition();
        }
      }
    }

    // Render Logic
    camera.update();

    // 1. Draw Semi-transparent background
    Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
    Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);

    shapeRenderer.setProjectionMatrix(camera.combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

    // Fade in background
    float alpha = Math.min(0.85f, elapsedTime / 2f);
    if (transitioning) alpha = Math.max(0f, 0.85f * (1 - transitionTime / TRANSITION_DURATION));

    shapeRenderer.setColor(0, 0, 0, alpha);
    shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    shapeRenderer.end();
    Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

    // 2. Draw Text
    batch.setProjectionMatrix(camera.combined);
    batch.begin();

    float screenWidth = Gdx.graphics.getWidth();
    float screenHeight = Gdx.graphics.getHeight();
    float totalHeight = lineSpacing * lines.length;
    float startY = (screenHeight + totalHeight) / 2f;

    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      if (line.isEmpty()) continue;

      float lineStartTime = INTRO_DELAY + i * LINE_DELAY;
      float lineTime = elapsedTime - lineStartTime;

      if (lineTime <= 0f) continue;

      float textAlpha;
      if (transitioning) {
        textAlpha = Math.max(0f, 1f - (transitionTime / 1f)); // Fade out text quickly
      } else if (finished) {
        textAlpha = 1f;
      } else {
        textAlpha = Math.min(1f, lineTime / LINE_FADE_DURATION);
      }

      font.setColor(1f, 1f, 1f, textAlpha); // Pure White to match Intro
      layout.setText(font, line);

      float x = (screenWidth - layout.width) / 2f;
      float y = startY - i * lineSpacing;

      font.draw(batch, layout, x, y);
    }

    batch.end();

    return false;
  }

  private void startTransition() {
    if (!transitioning) {
      transitioning = true;
      transitionTime = 0f;
      finished = true;
    }
  }

  public void resize(int width, int height) {
    camera.setToOrtho(false, width, height);
  }

  @Override
  public void dispose() {
    font.dispose();
    // AudioManager handles music lifecycle
  }
}
