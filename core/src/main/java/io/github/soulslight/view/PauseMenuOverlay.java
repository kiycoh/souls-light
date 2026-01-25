package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.soulslight.SoulsLightGame;

public class PauseMenuOverlay {

  private final Stage stage;
  private final BitmapFont font;
  private final GameScreen gameScreen;
  private Texture backgroundTexture; // Optional for simpler black overlay logic

  private final java.util.List<TextButton> buttons = new java.util.ArrayList<>();
  private int selectedIndex = 0;

  public PauseMenuOverlay(SpriteBatch batch, GameScreen gameScreen) {
    this.gameScreen = gameScreen;
    // Use the same virtual viewport logic
    this.stage =
        new Stage(
            new FitViewport(
                io.github.soulslight.model.Constants.V_WIDTH,
                io.github.soulslight.model.Constants.V_HEIGHT),
            batch);
    this.font = new BitmapFont();
    // this.font.getData().setScale(2); // Adjust scale as needed for standard
    // resolution

    setupUI();
    setupInput();
  }

  private void setupInput() {
    stage.addListener(
        new com.badlogic.gdx.scenes.scene2d.InputListener() {
          @Override
          public boolean keyDown(InputEvent event, int keycode) {
            if (keycode == com.badlogic.gdx.Input.Keys.UP) {
              selectedIndex--;
              if (selectedIndex < 0) selectedIndex = buttons.size() - 1;
              updateSelectionVisuals();
              return true;
            }
            if (keycode == com.badlogic.gdx.Input.Keys.DOWN) {
              selectedIndex++;
              if (selectedIndex >= buttons.size()) selectedIndex = 0;
              updateSelectionVisuals();
              return true;
            }
            if (keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {
              gameScreen.resumeGame();
              return true;
            }
            if (keycode == com.badlogic.gdx.Input.Keys.ENTER) {
              if (selectedIndex >= 0 && selectedIndex < buttons.size()) {
                // Trigger click behavior
                com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent changeEvent =
                    new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent();
                buttons.get(selectedIndex).fire(changeEvent);
                // Note: ClickListener fires on touchUp usually, ChangeListener is better for
                com.badlogic.gdx.scenes.scene2d.utils.ClickListener cl =
                    (com.badlogic.gdx.scenes.scene2d.utils.ClickListener)
                        buttons.get(selectedIndex).getListeners().get(0);
                cl.clicked(null, 0, 0);
              }
              return true;
            }
            return false;
          }
        });
  }

  private void updateSelectionVisuals() {
    for (int i = 0; i < buttons.size(); i++) {
      TextButton btn = buttons.get(i);
      if (i == selectedIndex) {
        btn.getLabel().setColor(Color.GOLD); // Highlight
        btn.getLabel().setFontScale(1.2f); // Pop
      } else {
        btn.getLabel().setColor(Color.WHITE);
        btn.getLabel().setFontScale(1.0f);
      }
    }
  }

  private void setupUI() {
    Table table = new Table();
    table.setFillParent(true);
    table.center();

    // Background (Semi-transparent black)
    table.setBackground(
        new com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable() {
          @Override
          public void draw(
              com.badlogic.gdx.graphics.g2d.Batch batch,
              float x,
              float y,
              float width,
              float height) {
            batch.setColor(0f, 0f, 0f, 0.7f);
            // Creating a 1x1 Pixmap on the fly for background is safe and standard
            if (backgroundTexture == null) {
              com.badlogic.gdx.graphics.Pixmap pixmap =
                  new com.badlogic.gdx.graphics.Pixmap(
                      1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
              pixmap.setColor(Color.WHITE);
              pixmap.fill();
              backgroundTexture = new Texture(pixmap);
              pixmap.dispose();
            }
            batch.draw(backgroundTexture, x, y, width, height);
            batch.setColor(Color.WHITE);
          }
        });

    stage.addActor(table);

    TextButtonStyle style = new TextButtonStyle();
    style.font = font;
    style.fontColor = Color.WHITE;
    style.overFontColor = Color.GOLD;
    style.downFontColor = Color.GRAY;

    TextButton resumeBtn = new TextButton("Resume", style);
    TextButton optionsBtn = new TextButton("Options", style);
    TextButton menuBtn = new TextButton("Main Menu", style);
    TextButton exitBtn = new TextButton("Exit Desktop", style);

    buttons.add(resumeBtn);
    buttons.add(optionsBtn);
    buttons.add(menuBtn);
    buttons.add(exitBtn);

    // Listeners
    resumeBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            gameScreen.resumeGame();
          }
        });

    optionsBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            // gameScreen.openSettings(); // If implemented
            Gdx.app.log("PauseMenu", "Options clicked (Placeholder)");
            // Ideally switch to SettingsScreen, passing "this" as parent?
            // For simple pause, maybe just log for now or overlay settings.
            SoulsLightGame game = (SoulsLightGame) Gdx.app.getApplicationListener();
            game.setScreen(
                new SettingsScreen(game, gameScreen.getBatch())); // Note: return path might be lost
            // unless handled
          }
        });

    menuBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            SoulsLightGame game = (SoulsLightGame) Gdx.app.getApplicationListener();
            game.setScreen(new MainMenuScreen(game, gameScreen.getBatch()));
          }
        });

    exitBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            Gdx.app.exit();
          }
        });

    float pad = 10f;
    float w = 200f;

    table
        .add(
            new com.badlogic.gdx.scenes.scene2d.ui.Label(
                "PAUSED",
                new com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle(font, Color.WHITE)))
        .padBottom(30)
        .row();
    table.add(resumeBtn).width(w).pad(pad).row();
    table.add(optionsBtn).width(w).pad(pad).row();
    table.add(menuBtn).width(w).pad(pad).row();
    table.add(exitBtn).width(w).pad(pad).row();

    updateSelectionVisuals(); // Initial state
  }

  public void render(float delta) {
    stage.act(delta);
    stage.draw();
  }

  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  public Stage getStage() {
    return stage;
  }

  public void dispose() {
    stage.dispose();
    font.dispose();
    if (backgroundTexture != null) backgroundTexture.dispose();
  }
}
