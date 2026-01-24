package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.soulslight.SoulsLightGame;
import io.github.soulslight.controller.GameController;
import io.github.soulslight.model.GameModel;

public final class IntroScreen implements GameState {


    private static final float INTRO_DELAY = 1.2f;
    private static final float LINE_FADE_DURATION = 2.5f;
    private static final float LINE_DELAY = 2.8f;

    private final SoulsLightGame game;
    private final SpriteBatch batch;
    private final GameModel model;
    private final GameController controller;

    private final BitmapFont font;
    private final OrthographicCamera camera;
    private final GlyphLayout layout;

    private final String[] lines;
    private final float totalDuration;
    private final float lineSpacing;

    private float elapsedTime = 0f;
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

        this.layout = new GlyphLayout();

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.lines =
            new String[] {
                "Dopo la morte non esistono né paradiso né inferno.",
                "",
                "Le anime, contrariamente alle credenze umane, vengono trascinate nel Mu,",
                "una dimensione vuota e instabile.",
                "",
                "Nel Mu non è possibile morire di nuovo.",
                "Condannate a una esistenza eterna, molte anime perdono sé stesse",
                "e si trasformano in creature prive di senno.",
                "",
                "Tra queste, due anime scelgono di scendere nel punto più oscuro del Mu,",
                "alla ricerca di una via di fuga..."
            };

        this.lineSpacing = font.getLineHeight() * 1.2f;

        this.totalDuration =
            INTRO_DELAY + (lines.length - 1) * LINE_DELAY + LINE_FADE_DURATION;
    }

    private void goToGame() {
        game.setScreen(new GameScreen(batch, model, controller));
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        if (!finished) {
            elapsedTime += delta;
            if (elapsedTime >= totalDuration) {
                elapsedTime = totalDuration;
                finished = true;
            }
        }

        // Intro skip
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.justTouched()) {
            if (!finished) {
                elapsedTime = totalDuration;
                finished = true;
            } else {
                // Start game once full text is on screen
                goToGame();
            }
        }

        // clear schermo
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // calcolo altezza totale del blocco di testo per centrarlo in verticale
        float totalHeight = lineSpacing * lines.length;
        float startY = (screenHeight + totalHeight) / 2f;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            float lineStartTime = INTRO_DELAY + i * LINE_DELAY;
            float lineTime = elapsedTime - lineStartTime;

            if (lineTime <= 0f) {
                continue;
            }

            float alpha;
            if (finished) {
                alpha = 1f;
            } else {
                alpha = Math.min(1f, lineTime / LINE_FADE_DURATION);
            }

            // righe vuote: solo spazio, ma manteniamo la cascata temporale
            if (line.isEmpty()) {
                continue;
            }

            font.setColor(1f, 1f, 1f, alpha);
            layout.setText(font, line);

            float x = (screenWidth - layout.width) / 2f;
            float y = startY - i * lineSpacing;

            font.draw(batch, layout, x, y);
        }

        // reset alpha a 1
        font.setColor(1f, 1f, 1f, 1f);

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
