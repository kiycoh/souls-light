package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.soulslight.SoulsLightGame;

public final class SplashScreen implements GameState {

  private final SoulsLightGame game;
  private Stage stage;
  private Texture logoTexture;
  private Image logoImage;

  private Sound splashSfx;

  public SplashScreen(SoulsLightGame game) {
    this.game = game;
  }

  @Override
  public void show() {
    stage = new Stage(new ScreenViewport());
    Gdx.input.setInputProcessor(stage);

    // loads team logo
    logoTexture = new Texture(Gdx.files.internal("ui/team14.png"));
    logoImage = new Image(logoTexture);
    logoImage.setSize(572, 303);
    logoImage.setPosition(
        (stage.getWidth() - logoImage.getWidth()) / 2f,
        (stage.getHeight() - logoImage.getHeight()) / 2f);
    logoImage.getColor().a = 0f; // invisible

    stage.addActor(logoImage);

    // loads and plays the sound effect
    splashSfx = Gdx.audio.newSound(Gdx.files.internal("audio/shine7.mp3"));
    splashSfx.play(1.0f); // volume 1.0

    // manages fade in, fade out e and transition to MainMenuScreen
    logoImage.addAction(
        Actions.sequence(
            Actions.fadeIn(1f), // 1 second of fade in
            Actions.delay(1f), // 1 second of visibility
            Actions.fadeOut(1f), // 1 second of fade out
            Actions.run(
                () -> {
                  game.setScreen(new MainMenuScreen(game, game.getBatch()));
                })));
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(delta);
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {
    stage.dispose();
    logoTexture.dispose();

    if (splashSfx != null) splashSfx.dispose();
  }
}
