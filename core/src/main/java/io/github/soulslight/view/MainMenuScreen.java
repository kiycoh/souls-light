package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.soulslight.SoulsLightGame;
import io.github.soulslight.controller.GameController;
import io.github.soulslight.manager.EventManager;
import io.github.soulslight.model.GameModel;

public class MainMenuScreen implements Screen {

    private final SoulsLightGame game;
    private final SpriteBatch batch;
    private Stage stage;
    private BitmapFont font;

    public MainMenuScreen(SoulsLightGame game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        this.stage = new Stage(new ScreenViewport(), batch);
        this.font = new BitmapFont(); // Use default font for now
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;

        TextButton newGameButton = new TextButton("New Game", style);
        TextButton continueButton = new TextButton("Continue", style);
        TextButton optionsButton = new TextButton("Options", style);
        TextButton exitButton = new TextButton("Exit", style);

        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EventManager.getInstance().notifyNewGame();
                GameModel model = new GameModel();
                GameController controller = new GameController(model);
                game.setScreen(new GameScreen(batch, model, controller));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EventManager.getInstance().notifyExit();
                Gdx.app.exit();
            }
        });

        table.add(newGameButton).pad(10).row();
        table.add(continueButton).pad(10).row();
        table.add(optionsButton).pad(10).row();
        table.add(exitButton).pad(10).row();
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
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
    }
}
