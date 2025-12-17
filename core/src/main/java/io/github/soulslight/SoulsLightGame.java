package io.github.soulslight;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.soulslight.controller.GameController;
import io.github.soulslight.manager.ResourceManager;
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
        this.setScreen(new io.github.soulslight.view.MainMenuScreen(this, batch));
    }

    @Override
    public void dispose() {
        batch.dispose();
        ResourceManager.getInstance().dispose();
        super.dispose();
    }
}
