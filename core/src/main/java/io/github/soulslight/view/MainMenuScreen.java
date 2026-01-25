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
import com.badlogic.gdx.scenes.scene2d.Touchable;
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
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.manager.GameMode;
import io.github.soulslight.manager.SaveManager;
import io.github.soulslight.manager.SettingsManager;
import io.github.soulslight.model.GameModel;

public final class MainMenuScreen implements GameState {

  private final SoulsLightGame game;
  private final SpriteBatch batch;
  private final Stage stage;
  private final BitmapFont font;
  private Texture backgroundTexture;
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

    menuMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/no escape.mp3"));
    menuMusic.setLooping(true);
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
    table.bottom().center().padBottom(-220f);
    stage.addActor(table);

    font.getData().setScale(1.3f);

    TextButtonStyle standardStyle = new TextButtonStyle();
    standardStyle.font = font;
    standardStyle.fontColor = Color.WHITE;
    standardStyle.downFontColor = Color.GRAY;
    standardStyle.overFontColor = Color.LIGHT_GRAY;
    // Define a "disabled" color for visual feedback
    standardStyle.disabledFontColor = new Color(0.3f, 0.3f, 0.3f, 1f);

    TextButton newGameButton = new TextButton("New Game", standardStyle);
    TextButton continueButton = new TextButton("Continue", standardStyle);
    TextButton optionsButton = new TextButton("Options", standardStyle);
    TextButton exitButton = new TextButton("Exit", standardStyle);

    // --- CHECK SAVE FILE ---
    SaveManager saveManager = new SaveManager();
    boolean hasSave = saveManager.hasSaveFile();

    // Disable continue button if no save exists
    continueButton.setDisabled(!hasSave);

    // Setup fade-in animations
    newGameButton.getColor().a = 0f;
    continueButton.getColor().a = 0f;
    optionsButton.getColor().a = 0f;
    exitButton.getColor().a = 0f;

    newGameButton.addAction(Actions.fadeIn(1f));
    continueButton.addAction(Actions.sequence(Actions.delay(0.2f), Actions.fadeIn(1f)));
    optionsButton.addAction(Actions.sequence(Actions.delay(0.4f), Actions.fadeIn(1f)));
    exitButton.addAction(Actions.sequence(Actions.delay(0.6f), Actions.fadeIn(1f)));

    // --- LISTENERS ---
    newGameButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            showModeSelectionDialog(standardStyle);
          }
        });

    // LISTENER FOR CONTINUE
    continueButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            if (continueButton.isDisabled()) return; // Ignore click if disabled

            GameModel model = new GameModel();
            SaveManager sm = new SaveManager();

            // Try loading. If successful, switch screens.
            // Note: Error handling inside SaveManager will delete corrupt files,
            // so we might technically load an empty state if corruption occurs,
            // but the Model defaults to a fresh state anyway.
            sm.loadGame(model);

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
    final float pad = 8f;

    table.add(newGameButton).width(btnWidth).height(btnHeight).pad(pad).row();
    table.add(continueButton).width(btnWidth).height(btnHeight).pad(pad).row();
    table.add(optionsButton).width(btnWidth).height(btnHeight).pad(pad).row();
    table.add(exitButton).width(btnWidth).height(btnHeight).pad(pad).row();
  }

  /**
   * Shows a dialog for selecting between Story Mode and Custom Mode. Integrates with
   * GameManager.startCampaign() to initialize the campaign.
   */
  private void showModeSelectionDialog(TextButtonStyle buttonStyle) {
    // Create a semi-transparent overlay table
    Table dialogTable = new Table();
    dialogTable.setFillParent(true);
    dialogTable.center();
    // Make the dialog modal:
    // 1. Enable touch events so the table itself (background) can be hit.
    // 2. Add an event listener to consume all clicks, interfering with buttons
    // below.
    dialogTable.setTouchable(Touchable.enabled);
    dialogTable.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            // Consumes the event
          }
        });

    // Dialog background styling
    dialogTable.setBackground(
        new com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable() {
          @Override
          public void draw(
              com.badlogic.gdx.graphics.g2d.Batch batch,
              float x,
              float y,
              float width,
              float height) {
            batch.setColor(0f, 0f, 0f, 0.7f);
            batch.draw(backgroundTexture, x, y, width, height);
            batch.setColor(Color.WHITE);
          }
        });

    // Title label
    com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle labelStyle =
        new com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle(font, Color.WHITE);
    com.badlogic.gdx.scenes.scene2d.ui.Label titleLabel =
        new com.badlogic.gdx.scenes.scene2d.ui.Label("Select Game Mode", labelStyle);
    titleLabel.setFontScale(1.5f);

    // Mode buttons
    TextButton storyButton = new TextButton("Story Mode", buttonStyle);
    TextButton customButton = new TextButton("Custom Mode", buttonStyle);
    TextButton backButton = new TextButton("Back", buttonStyle);

    // Story Mode listener
    storyButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            GameManager.getInstance().startCampaign(GameMode.STORY);
            dialogTable.remove();
            game.setScreen(new ClassSelectionScreen(game, batch));
          }
        });

    // Custom Mode listener
    customButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            GameManager.getInstance().startCampaign(GameMode.CUSTOM);
            startGame();
            dialogTable.remove();
          }
        });

    // Back button listener
    backButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            dialogTable.remove();
          }
        });

    final float btnWidth = 250f;
    final float btnHeight = 50f;
    final float pad = 12f;

    dialogTable.add(titleLabel).padBottom(30f).row();
    dialogTable.add(storyButton).width(btnWidth).height(btnHeight).pad(pad).row();
    dialogTable.add(customButton).width(btnWidth).height(btnHeight).pad(pad).row();
    dialogTable.add(backButton).width(btnWidth).height(btnHeight).pad(pad).padTop(20f).row();

    // Add fade-in animation
    dialogTable.getColor().a = 0f;
    dialogTable.addAction(Actions.fadeIn(0.3f));

    stage.addActor(dialogTable);
  }

  /** Starts the game with the current GameManager configuration. */
  private void startGame() {
    GameModel model = new GameModel();
    GameController controller = new GameController(model);
    game.setScreen(new GameScreen(batch, model, controller));
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
