package io.github.soulslight.controller;

import com.badlogic.gdx.InputAdapter;
import io.github.soulslight.model.GameModel;

public class GameController extends InputAdapter{
    private final GameModel model;

    public GameController(GameModel model) {
        this.model = model;
    }

    //@Override
    //public boolean KeyDown(int keycode) {
    //    return super.keyDown(keycode);
    //}

    public void update(float delta) {

    }
}
