package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.soulslight.SoulsLightGame;
import io.github.soulslight.controller.GameController;
import io.github.soulslight.manager.SettingsManager;
import io.github.soulslight.model.GameModel;

public final class MainMenuScreen implements GameState {

  private final SoulsLightGame game;
  private final SpriteBatch batch;

  private final Stage stage;
  private final BitmapFont font;

  private Texture backgroundTexture;

  // music
  private Music menuMusic;

  public MainMenuScreen(SoulsLightGame game, SpriteBatch batch) {
    this.game = game;
    this.batch = batch;

    this.stage = new Stage(new FitViewport(1280, 720), batch);
    this.font = new BitmapFont();
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(stage);

    // Audio initialization
    menuMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/no escape.mp3"));
    menuMusic.setLooping(true);
    // uses global settings volume
    menuMusic.setVolume(SettingsManager.getInstance().getMusicVolume());
    menuMusic.play();

    setupBackground();
    setupUI();
  }

  private void setupBackground() {
    backgroundTexture = new Texture(Gdx.files.internal("ui/menubg.png"));
    Image backgroundImage = new Image(backgroundTexture);
    backgroundImage.setScaling(Scaling.stretch);
    backgroundImage.setFillParent(true);
    stage.addActor(backgroundImage);
  }

  private void setupUI() {
    Table table = new Table();
    table.setFillParent(true);

    table.bottom();
    table.center();
    table.padBottom(-220f);

    stage.addActor(table);

    font.getData().setScale(1.3f);

    TextButtonStyle standardStyle = new TextButtonStyle();
    standardStyle.font = font;
    standardStyle.fontColor = Color.WHITE;
    standardStyle.downFontColor = Color.GRAY;
    standardStyle.overFontColor = Color.LIGHT_GRAY;

    TextButton newGameButton = new TextButton("New Game", standardStyle);
    TextButton continueButton = new TextButton("Continue", standardStyle);
    TextButton optionsButton = new TextButton("Options", standardStyle);
    TextButton exitButton = new TextButton("Exit", standardStyle);

    // setup for fade in
    newGameButton.getColor().a = 0f;
    continueButton.getColor().a = 0f;
    optionsButton.getColor().a = 0f;
    exitButton.getColor().a = 0f;

    // fade in
    newGameButton.addAction(Actions.fadeIn(1f));
    continueButton.addAction(Actions.sequence(Actions.delay(0.2f), Actions.fadeIn(1f)));
    optionsButton.addAction(Actions.sequence(Actions.delay(0.4f), Actions.fadeIn(1f)));
    exitButton.addAction(Actions.sequence(Actions.delay(0.6f), Actions.fadeIn(1f)));

    newGameButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            GameModel model = new GameModel();
            GameController controller = new GameController(model);
            game.setScreen(new GameScreen(batch, model, controller));
          }
        });

    optionsButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            game.setScreen(new SettingsScreen(game, batch));
          }
        });

    exitButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            Gdx.app.exit();
          }
        });

    final float btnWidth = 300f;
    final float btnHeight = 60f;

    table.add(newGameButton).width(btnWidth).height(btnHeight).pad(8f).row();
    table.add(continueButton).width(btnWidth).height(btnHeight).pad(8f).row();
    table.add(optionsButton).width(btnWidth).height(btnHeight).pad(8f).row();
    table.add(exitButton).width(btnWidth).height(btnHeight).pad(8f).row();
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
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
    font.dispose();

    backgroundTexture.dispose();

    if (menuMusic != null) {
      menuMusic.stop();
      menuMusic.dispose();
    }
  }
}
