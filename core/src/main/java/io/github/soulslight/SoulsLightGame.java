package io.github.soulslight;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.soulslight.controller.GameController;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.view.GameScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class SoulsLightGame extends Game {
    private SpriteBatch batch;
    private GameModel model;
    private GameController controller;

    @Override
    public void create() {

        batch = new SpriteBatch();
        model = new GameModel();
        controller = new GameController(model);
        this.setScreen(new GameScreen(batch, model, controller));
    }

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }
}
