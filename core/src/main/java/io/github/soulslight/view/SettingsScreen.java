package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.soulslight.SoulsLightGame;
import io.github.soulslight.manager.SettingsManager;

public class SettingsScreen implements Screen {

    private final SoulsLightGame game;
    private final SpriteBatch batch;
    private final Stage stage;
    private final BitmapFont font;

    public SettingsScreen(SoulsLightGame game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        this.stage = new Stage(new ScreenViewport(), batch);
        this.font = new BitmapFont();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Scale font for visibility
        font.getData().setScale(2.0f);

        // ---------------------------------------------------------
        // STYLE DEFINITIONS (Flyweight pattern candidate)
        // ---------------------------------------------------------
        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.GRAY;
        buttonStyle.overFontColor = Color.LIGHT_GRAY;

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        // Title
        Label titleLabel = new Label("SETTINGS", labelStyle);
        // Note: setFontScale on Label works relative to the BitmapFont's scale
        titleLabel.setFontScale(1.5f);

        // ---------------------------------------------------------
        // CONTROLS
        // ---------------------------------------------------------

        // Auto-Aim Button
        final TextButton autoAimBtn = new TextButton(getAutoAimText(), buttonStyle);
        autoAimBtn.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    var settings = SettingsManager.getInstance();
                    settings.setAutoAimEnabled(!settings.isAutoAimEnabled());
                    autoAimBtn.setText(getAutoAimText());
                }
            });

        // Fullscreen Button
        final TextButton fullscreenBtn = new TextButton(getFullscreenText(), buttonStyle);
        fullscreenBtn.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    var settings = SettingsManager.getInstance();
                    settings.setFullscreen(!settings.isFullscreen());
                    fullscreenBtn.setText(getFullscreenText());
                    // Note: Actual fullscreen switching logic should be handled by a GraphicsManager
                }
            });

        // Volume Controls
        final Label volumeValueLabel = new Label(getVolumeText(), labelStyle);
        TextButton volMinusBtn = new TextButton("-", buttonStyle);
        TextButton volPlusBtn = new TextButton("+", buttonStyle);

        volMinusBtn.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    var settings = SettingsManager.getInstance();
                    settings.setMusicVolume(Math.max(0f, settings.getMusicVolume() - 0.1f));
                    volumeValueLabel.setText(getVolumeText());
                }
            });

        volPlusBtn.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    var settings = SettingsManager.getInstance();
                    settings.setMusicVolume(Math.min(1f, settings.getMusicVolume() + 0.1f));
                    volumeValueLabel.setText(getVolumeText());
                }
            });

        // Back Button
        TextButton backButton = new TextButton("Back", buttonStyle);
        backButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(new MainMenuScreen(game, batch));
                }
            });

        // ---------------------------------------------------------
        // LAYOUT
        // ---------------------------------------------------------
        final float btnWidth = 300f;
        final float btnHeight = 50f;

        table.add(titleLabel).padBottom(40f).colspan(3).row();

        // Auto Aim Row
        table.add(autoAimBtn).width(btnWidth).height(btnHeight).colspan(3).padBottom(10f).row();

        // Fullscreen Row
        table.add(fullscreenBtn).width(btnWidth).height(btnHeight).colspan(3).padBottom(10f).row();

        // Volume Row
        // [Label 150px] [ - 50px ] [ + 50px ]
        table.add(volumeValueLabel).width(150f).center();
        table.add(volMinusBtn).width(50f).padRight(10f);
        table.add(volPlusBtn).width(50f).row();

        // Back Row
        table.add(backButton).width(200f).height(btnHeight).colspan(3).padTop(40f);
    }

    private String getAutoAimText() {
        return "Auto-Aim: "
            + (io.github.soulslight.manager.SettingsManager.getInstance().isAutoAimEnabled()
            ? "ON"
            : "OFF");
    }

    private String getFullscreenText() {
        return "Fullscreen: "
            + (io.github.soulslight.manager.SettingsManager.getInstance().isFullscreen()
            ? "ON"
            : "OFF");
    }

    private String getVolumeText() {
        int val =
            (int) (io.github.soulslight.manager.SettingsManager.getInstance().getMusicVolume() * 100);
        return "Music: " + val + "%";
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
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
    }
}
