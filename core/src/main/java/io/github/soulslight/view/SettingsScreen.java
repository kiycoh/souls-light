package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.Styles;
import com.github.tommyettinger.textra.TextraButton;

import io.github.soulslight.SoulsLightGame;

public class SettingsScreen implements Screen {

    private final SoulsLightGame game;
    private final SpriteBatch batch;
    private Stage stage;
    private BitmapFont font;

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

        font.getData().setScale(2.0f);
        Styles.TextButtonStyle style = new Styles.TextButtonStyle();
        style.font = new Font(font);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, null);
        Label titleLabel = new Label("SETTINGS", labelStyle);
        // Make title bigger
        titleLabel.setFontScale(2.0f);

        // --- CONTROLS ---

        // Auto-Aim
        final TextraButton autoAimBtn = new TextraButton(getAutoAimText(), style);
        autoAimBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean newState = !io.github.soulslight.manager.SettingsManager.getInstance().isAutoAimEnabled();
                io.github.soulslight.manager.SettingsManager.getInstance().setAutoAimEnabled(newState);
                autoAimBtn.setText(getAutoAimText());
            }
        });

        // Fullscreen
        final TextraButton fullscreenBtn = new TextraButton(getFullscreenText(), style);
        fullscreenBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean newState = !io.github.soulslight.manager.SettingsManager.getInstance().isFullscreen();
                io.github.soulslight.manager.SettingsManager.getInstance().setFullscreen(newState);
                fullscreenBtn.setText(getFullscreenText());
            }
        });

        // Music Volume
        // Simple implementation: [Vol -]  [Value]  [Vol +]
        final Label volumeValueLabel = new Label(getVolumeText(), labelStyle);
        TextraButton volMinusBtn = new TextraButton("-", style);
        TextraButton volPlusBtn = new TextraButton("+", style);

        volMinusBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                float v = io.github.soulslight.manager.SettingsManager.getInstance().getMusicVolume();
                io.github.soulslight.manager.SettingsManager.getInstance().setMusicVolume(v - 0.1f);
                volumeValueLabel.setText(getVolumeText());
            }
        });

        volPlusBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                float v = io.github.soulslight.manager.SettingsManager.getInstance().getMusicVolume();
                io.github.soulslight.manager.SettingsManager.getInstance().setMusicVolume(v + 0.1f);
                volumeValueLabel.setText(getVolumeText());
            }
        });


        // Back Button
        TextraButton backButton = new TextraButton("Back", style);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                 game.setScreen(new MainMenuScreen(game, batch));
            }
        });

        // --- LAYOUT ---
        table.add(titleLabel).padBottom(40f).colspan(3).row();

        // Auto Aim Row
        table.add(autoAimBtn).width(300f).height(50f).colspan(3).padBottom(10f).row();
        
        // Fullscreen Row
        table.add(fullscreenBtn).width(300f).height(50f).colspan(3).padBottom(10f).row();

        // Volume Row
        // [Label] [ - ] [ Value ] [ + ]
        // Simpler: Label centered, buttons below or same row.
        // Let's do: "Music: 50%"  [-]  [+]
        table.add(volumeValueLabel).width(150f).center();
        table.add(volMinusBtn).width(50f).padRight(10f);
        table.add(volPlusBtn).width(50f).row();

        table.add(backButton).width(200f).height(50f).colspan(3).padTop(40f);
    }

    private String getAutoAimText() {
        return "Auto-Aim: " + (io.github.soulslight.manager.SettingsManager.getInstance().isAutoAimEnabled() ? "ON" : "OFF");
    }

    private String getFullscreenText() {
        return "Fullscreen: " + (io.github.soulslight.manager.SettingsManager.getInstance().isFullscreen() ? "ON" : "OFF");
    }

    private String getVolumeText() {
        int val = (int)(io.github.soulslight.manager.SettingsManager.getInstance().getMusicVolume() * 100);
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
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
    }
}
