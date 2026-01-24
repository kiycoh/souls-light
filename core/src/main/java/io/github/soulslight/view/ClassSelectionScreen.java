package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.soulslight.SoulsLightGame;
import io.github.soulslight.controller.GameController;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.entities.Player.PlayerClass;

/**
 * Pattern: State Class selection screen shown after Story Mode is selected. Allows the player to
 * choose a character class before starting gameplay.
 */
public final class ClassSelectionScreen implements GameState {

  private final SoulsLightGame game;
  private final SpriteBatch batch;
  private final Stage stage;
  private final BitmapFont font;

  // UI elements
  private Label classNameLabel;
  private Label hpLabel;
  private Label willLabel;
  private Label abilityLabel;
  private Image backgroundImage;
  private Texture defaultBackground;
  private Texture[] classBackgrounds;

  // Current hovered class (null if none)
  private PlayerClass hoveredClass;

  public ClassSelectionScreen(SoulsLightGame game, SpriteBatch batch) {
    this.game = game;
    this.batch = batch;
    this.stage = new Stage(new FitViewport(1280, 720), batch);
    this.font = new BitmapFont();
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(stage);
    createMockupTextures();
    setupUI();
  }

  /** Creates placeholder textures for mockup purposes. Will be replaced by artist assets. */
  private void createMockupTextures() {
    // Default dark background
    defaultBackground = createSolidTexture(new Color(0.1f, 0.1f, 0.15f, 1f));

    // Class-specific backgrounds (mockup colors for now)
    classBackgrounds = new Texture[PlayerClass.values().length];
    classBackgrounds[PlayerClass.WARRIOR.ordinal()] =
        createSolidTexture(new Color(0.3f, 0.15f, 0.1f, 1f)); // Reddish
    classBackgrounds[PlayerClass.MAGE.ordinal()] =
        createSolidTexture(new Color(0.1f, 0.15f, 0.3f, 1f)); // Bluish
    classBackgrounds[PlayerClass.THIEF.ordinal()] =
        createSolidTexture(new Color(0.15f, 0.15f, 0.15f, 1f)); // Dark
    // gray
    classBackgrounds[PlayerClass.ARCHER.ordinal()] =
        createSolidTexture(new Color(0.1f, 0.25f, 0.15f, 1f)); // Greenish
  }

  private Texture createSolidTexture(Color color) {
    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    pixmap.setColor(color);
    pixmap.fill();
    Texture texture = new Texture(pixmap);
    pixmap.dispose();
    return texture;
  }

  private void setupUI() {
    // Background image (fills screen)
    backgroundImage = new Image(defaultBackground);
    backgroundImage.setFillParent(true);
    stage.addActor(backgroundImage);

    // Left panel for class stats (top-left corner)
    Table statsPanel = createStatsPanel();
    statsPanel.setPosition(100, 620);
    stage.addActor(statsPanel);

    // Bottom panel with class selection boxes
    Table classSelectionPanel = createClassSelectionPanel();
    stage.addActor(classSelectionPanel);

    // Back button
    TextButtonStyle buttonStyle = createButtonStyle();
    TextButton backButton = new TextButton("Back", buttonStyle);
    backButton.setPosition(50, 50);
    backButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            game.setScreen(new MainMenuScreen(game, batch));
          }
        });
    stage.addActor(backButton);
  }

  private Table createStatsPanel() {
    Table panel = new Table();
    panel.defaults().left().padBottom(10);

    Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.GOLD);
    Label.LabelStyle statStyle = new Label.LabelStyle(font, Color.WHITE);

    font.getData().setScale(2.0f);
    classNameLabel = new Label("Select a Class", titleStyle);
    classNameLabel.setFontScale(2.0f);
    panel.add(classNameLabel).row();

    font.getData().setScale(1.3f);
    hpLabel = new Label("HP: ---", statStyle);
    hpLabel.setFontScale(1.3f);
    panel.add(hpLabel).row();

    willLabel = new Label("Will: ---", statStyle);
    willLabel.setFontScale(1.3f);
    panel.add(willLabel).row();

    abilityLabel = new Label("Special: ---", statStyle);
    abilityLabel.setFontScale(1.3f);
    panel.add(abilityLabel).row();

    return panel;
  }

  private Table createClassSelectionPanel() {
    Table panel = new Table();
    panel.setFillParent(true);
    panel.bottom().padBottom(80);

    TextButtonStyle buttonStyle = createButtonStyle();

    float boxWidth = 200f;
    float boxHeight = 100f;
    float pad = 20f;

    for (PlayerClass playerClass : PlayerClass.values()) {
      TextButton classButton = new TextButton(playerClass.name(), buttonStyle);
      classButton.addListener(createClassButtonListener(playerClass));
      panel.add(classButton).width(boxWidth).height(boxHeight).pad(pad);
    }

    return panel;
  }

  private TextButtonStyle createButtonStyle() {
    TextButtonStyle style = new TextButtonStyle();
    style.font = font;
    style.fontColor = Color.WHITE;
    style.downFontColor = Color.GRAY;
    style.overFontColor = Color.GOLD;
    return style;
  }

  private ClickListener createClassButtonListener(PlayerClass playerClass) {
    return new ClickListener() {
      @Override
      public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        super.enter(event, x, y, pointer, fromActor);
        onClassHover(playerClass);
      }

      @Override
      public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        super.exit(event, x, y, pointer, toActor);
        onClassUnhover();
      }

      @Override
      public void clicked(InputEvent event, float x, float y) {
        onClassSelected(playerClass);
      }
    };
  }

  private void onClassHover(PlayerClass playerClass) {
    hoveredClass = playerClass;

    // Update stats panel
    classNameLabel.setText(playerClass.name());
    hpLabel.setText("HP: " + playerClass.getBaseHP());
    willLabel.setText("Will: " + playerClass.getBaseWill());
    abilityLabel.setText("Special: " + playerClass.getSpecialAbility());

    // Update background
    backgroundImage.setDrawable(new TextureRegionDrawable(classBackgrounds[playerClass.ordinal()]));
  }

  private void onClassUnhover() {
    hoveredClass = null;

    // Reset stats panel
    classNameLabel.setText("Select a Class");
    hpLabel.setText("HP: ---");
    willLabel.setText("Will: ---");
    abilityLabel.setText("Special: ---");

    // Reset background
    backgroundImage.setDrawable(new TextureRegionDrawable(defaultBackground));
  }

  private void onClassSelected(PlayerClass playerClass) {
    // Store selected class in GameManager
    GameManager.getInstance().setSelectedPlayerClass(playerClass);

    // Start the game
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
    if (defaultBackground != null) defaultBackground.dispose();
    if (classBackgrounds != null) {
      for (Texture tex : classBackgrounds) {
        if (tex != null) tex.dispose();
      }
    }
  }
}
