package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.Styles;
import com.github.tommyettinger.textra.TextraButton;

import io.github.soulslight.SoulsLightGame;
import io.github.soulslight.controller.GameController;
import io.github.soulslight.manager.EventManager;
import io.github.soulslight.model.GameModel;

public class MainMenuScreen implements Screen {

    private final SoulsLightGame game;
    private final SpriteBatch batch;

    private Stage stage;
    private BitmapFont font;

    private Texture logoTexture;
    private Texture backgroundTexture;
    private Image backgroundImage;
    // music
    private Music menuMusic;

    public MainMenuScreen(SoulsLightGame game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        this.stage = new Stage(new ScreenViewport(), batch);
        this.font = new BitmapFont(); // Use default font for now
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        //loads bg music
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/no escape.mp3"));
        menuMusic.setLooping(true);
        menuMusic.setVolume(0.5f); // volume 50%
        menuMusic.play();

        //loads bg image
        backgroundTexture = new Texture(Gdx.files.internal("ui/menubg.jpg"));
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setScaling(Scaling.stretch);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);


        Table table = new Table();
        table.setFillParent(true);
        table.top();
        table.left();
        stage.addActor(table);

        //loads game logo
        logoTexture = new Texture(Gdx.files.internal("ui/logo.png"));
        Image logoImage = new Image(logoTexture);

        logoImage.setScaling(Scaling.fit);
        logoImage.setScale(1.4f);

        table.add(logoImage)
            .width(500f)  // locking the width
            .padTop(40f)
            .padBottom(40f)
            .padLeft(-80f)
            .left()
            .row();


        font.getData().setScale(1.3f);
        Styles.TextButtonStyle style = new Styles.TextButtonStyle();
        style.font = new Font(font);


        TextraButton newGameButton = new TextraButton("New Game", style);
        TextraButton continueButton = new TextraButton("Continue", style);
        TextraButton optionsButton = new TextraButton("Options", style);
        TextraButton exitButton = new TextraButton("Exit", style);

        newGameButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    EventManager.getInstance().notifyNewGame();
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
                    EventManager.getInstance().notifyExit();
                    Gdx.app.exit();
                }
            });

        table.add(newGameButton).width(300f).height(60f).pad(0f, 0f, 0f, 0f).left().row();
        table.add(continueButton).width(300f).height(60f).pad(0f, 0f, 0f, 0f).left().row();
        table.add(optionsButton).width(300f).height(60f).pad(0f, 0f, 0f, 0f).left().row();
        table.add(exitButton).width(300f).height(60f).pad(0f, 0f, 100f, 0f).left().row();

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

        logoTexture.dispose();
        backgroundTexture.dispose();


        if (menuMusic != null) {
            menuMusic.stop();
            menuMusic.dispose();
        }
    }
}
