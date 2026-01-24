package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.soulslight.SoulsLightGame;
import io.github.soulslight.controller.GameController;
import io.github.soulslight.model.GameModel;

public final class IntroScreen implements GameState {

    private static final float CHARS_PER_SECOND = 40f;

    private final SoulsLightGame game;
    private final SpriteBatch batch;
    private final GameModel model;
    private final GameController controller;

    private final BitmapFont font;
    private final OrthographicCamera camera;

    private final String fullText;

    private float elapsedTime = 0f;
    private int visibleChars = 0;
    private boolean finished = false;

    public IntroScreen(
        SoulsLightGame game, SpriteBatch batch, GameModel model, GameController controller) {
        this.game = game;
        this.batch = batch;
        this.model = model;
        this.controller = controller;

        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(1.1f);

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.fullText =
            "Dopo la morte non esistono né paradiso né inferno.\n\n"
                + "Le anime, contrariamente alle credenze umane, vengono trascinate nel Mu,\n"
                + "una dimensione vuota e instabile.\n\n"
                + "Nel Mu non è possibile morire di nuovo.\n"
                + "Condannate a un’esistenza eterna, molte anime perdono sé stesse\n"
                + "e si trasformano in creature prive di senno.\n\n"
                + "Tra queste, due anime scelgono di scendere nel punto più oscuro del Mu,\n"
                + "alla ricerca di una via di fuga...";
    }

    private void goToGame() {
        game.setScreen(new GameScreen(batch, model, controller));
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        if (!finished) {
            elapsedTime += delta;
            visibleChars = (int) (elapsedTime * CHARS_PER_SECOND);
            if (visibleChars >= fullText.length()) {
                visibleChars = fullText.length();
                finished = true;
            }
        }

        // Intro skip
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.justTouched()) {
            if (!finished) {
                visibleChars = fullText.length();
                finished = true;
            } else {
                // Start game once full text is on screen
                goToGame();
            }
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        String toDraw = fullText.substring(0, visibleChars);

        float marginX = 60f;
        float startY = Gdx.graphics.getHeight() - 80f;

        font.draw(batch, toDraw, marginX, startY);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        font.dispose();
    }
}
