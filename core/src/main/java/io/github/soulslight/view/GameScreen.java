package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.soulslight.controller.GameController;
import io.github.soulslight.model.GameModel;

public class GameScreen implements Screen {

    // --- MVC DEPENDENCIES ---
    private final SpriteBatch batch;
    private final GameModel model;
    private final GameController controller;

    // --- PIXEL ART RENDERING ---
    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 270;

    private final OrthographicCamera camera;
    private final Viewport viewport;

    public GameScreen(SpriteBatch batch, GameModel model, GameController controller) {
        this.batch = batch;
        this.model = model;
        this.controller = controller;

        // Setup Camera e Viewport for Pixel Art
        this.camera = new OrthographicCamera();

        // FitViewport maintains aspect ratio while scaling to fit the screen.
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
    }

    @Override
    public void show() {
        // Activate controller as input processor
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        // MVC Update Loop
        controller.update(delta);
        model.update(delta);

        // Rendering
        ScreenUtils.clear(0, 0, 0, 1); // (black background)

        // Update camera and apply batch
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        // to be added:
        // viewRenderer.render(batch, model);
        // tests
        batch.end();

        // Debug Renderer for Box2D (to be added)
    }

    @Override
    public void resize(int width, int height) {
        // 5. IMPORTANT: Window resizing handling true pixel
        viewport.update(width, height, true); // true = centra la camera
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        // NOTE: No batch.dispose() because batch is in SoulsLightGame
    }
}
