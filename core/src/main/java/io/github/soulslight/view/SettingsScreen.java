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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.soulslight.SoulsLightGame;
import io.github.soulslight.manager.SettingsManager;

public final class SettingsScreen implements GameState {

  private final SoulsLightGame game;
  private final SpriteBatch batch;

  private final Stage stage;
  private final BitmapFont font;

  private Texture backgroundTexture;
  private Image backgroundImage;

  private Texture toggleOnTexture;
  private Texture toggleOffTexture;

  private Music optionsMusic;

  public SettingsScreen(SoulsLightGame game, SpriteBatch batch) {
    this.game = game;
    this.batch = batch;

    this.stage = new Stage(new FitViewport(1280, 720), batch);
    this.font = new BitmapFont();
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(stage);

    optionsMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/saffron.mp3"));
    optionsMusic.setLooping(true);
    optionsMusic.setVolume(SettingsManager.getInstance().getMusicVolume());
    optionsMusic.play();

    setupBackground();
    setupUI();
  }

  private void setupBackground() {
    backgroundTexture = new Texture(Gdx.files.internal("ui/options.png"));
    backgroundImage = new Image(backgroundTexture);
    backgroundImage.setScaling(Scaling.stretch);
    backgroundImage.setFillParent(true);
    stage.addActor(backgroundImage);
  }

  private void setupUI() {
    toggleOnTexture = new Texture(Gdx.files.internal("ui/ToggleOn.png"));
    toggleOffTexture = new Texture(Gdx.files.internal("ui/ToggleOff.png"));
    toggleOnTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    toggleOffTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

    TextureRegionDrawable toggleOn = new TextureRegionDrawable(toggleOnTexture);
    TextureRegionDrawable toggleOff = new TextureRegionDrawable(toggleOffTexture);

    ImageButton.ImageButtonStyle toggleStyle = new ImageButton.ImageButtonStyle();
    toggleStyle.up = toggleOff;
    toggleStyle.checked = toggleOn;
    toggleStyle.down = toggleOn;

    font.getData().setScale(1.4f);
    Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

    TextButtonStyle smallBtnStyle = new TextButtonStyle();
    smallBtnStyle.font = font;
    smallBtnStyle.fontColor = Color.WHITE;
    smallBtnStyle.downFontColor = Color.GRAY;
    smallBtnStyle.overFontColor = Color.LIGHT_GRAY;

    Table root = new Table();
    root.setFillParent(true);
    stage.addActor(root);

    Table table = new Table();
    root.add(table).expand().center();

    // --- AUTO AIM ---
    Label autoAimLabel = new Label("Auto Aim", labelStyle);
    final ImageButton autoAimToggle = new ImageButton(toggleStyle);
    autoAimToggle.setChecked(SettingsManager.getInstance().isAutoAimEnabled());
    autoAimToggle.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            SettingsManager.getInstance().setAutoAimEnabled(autoAimToggle.isChecked());
          }
        });

    autoAimToggle.setTransform(true);
    autoAimToggle.setScale(0.85f);

    // --- FULLSCREEN ---
    Label fullscreenLabel = new Label("Fullscreen", labelStyle);
    final ImageButton fullscreenToggle = new ImageButton(toggleStyle);
    fullscreenToggle.setChecked(SettingsManager.getInstance().isFullscreen());
    fullscreenToggle.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            SettingsManager.getInstance().setFullscreen(fullscreenToggle.isChecked());
          }
        });

    fullscreenToggle.setTransform(true);
    fullscreenToggle.setScale(0.85f);

    // --- VOLUME ---
    final Label volumeLabel = new Label(getVolumeText(), labelStyle);

    TextButton volMinusBtn = new TextButton("-", smallBtnStyle);
    TextButton volPlusBtn = new TextButton("+", smallBtnStyle);

    volMinusBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            var s = SettingsManager.getInstance();
            s.setMusicVolume(Math.max(0f, s.getMusicVolume() - 0.1f));
            volumeLabel.setText(getVolumeText());
            if (optionsMusic != null) optionsMusic.setVolume(s.getMusicVolume());
          }
        });

    volPlusBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            var s = SettingsManager.getInstance();
            s.setMusicVolume(Math.min(1f, s.getMusicVolume() + 0.1f));
            volumeLabel.setText(getVolumeText());
            if (optionsMusic != null) optionsMusic.setVolume(s.getMusicVolume());
          }
        });

    // --- BACK ---
    TextButton backButton = new TextButton("Back", smallBtnStyle);
    backButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            game.setScreen(new MainMenuScreen(game, batch));
          }
        });

    final float toggleW = 96f;
    final float toggleH = 40f;
    final float rowPad = 20f;

    table.add(autoAimLabel).left().padRight(30f).padBottom(rowPad);
    table.add(autoAimToggle).width(toggleW).height(toggleH).padBottom(rowPad).row();

    table.add(fullscreenLabel).left().padRight(30f).padBottom(rowPad);
    table.add(fullscreenToggle).width(toggleW).height(toggleH).padBottom(rowPad).row();

    Table volRow = new Table();
    volRow.add(volumeLabel).padRight(16f);
    volRow.add(volMinusBtn).width(48f).height(40f).padRight(8f);
    volRow.add(volPlusBtn).width(48f).height(40f);

    table.add(volRow).colspan(2).padTop(10f).row();

    table.add(backButton).width(200f).height(54f).colspan(2).padTop(40f);
  }

  private String getVolumeText() {
    int val = (int) (SettingsManager.getInstance().getMusicVolume() * 100);
    return "Music: " + val + "%";
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
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

    if (backgroundTexture != null) backgroundTexture.dispose();
    if (toggleOnTexture != null) toggleOnTexture.dispose();
    if (toggleOffTexture != null) toggleOffTexture.dispose();

    if (optionsMusic != null) {
      optionsMusic.stop();
      optionsMusic.dispose();
    }
  }
}
