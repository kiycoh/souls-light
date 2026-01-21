package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.soulslight.SoulsLightGame;

public final class SplashScreen implements GameState {

    private static final float FPS = 30f;
    private static final int FRAME_COUNT = 440;

    private static final float VIRTUAL_WIDTH = 1280;
    private static final float VIRTUAL_HEIGHT = 720;

    private final SoulsLightGame game;
    private final SpriteBatch batch;

    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture currentTexture;
    private TextureRegion currentFrameRegion;
    private int currentFrameIndex = -1;

    private float stateTime = 0f;

    // audio
    private Music introMusic;
    private Sound slashSound;
    private boolean slashPlayed = false;

    public SplashScreen(SoulsLightGame game) {
        this.game = game;
        this.batch = game.getBatch();
    }

    @Override
    public void show() {
        // camera + viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
        camera.update();

        // loads first frame
        loadFrame(0);

        // audio
        introMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/intro.mp3"));
        introMusic.setLooping(false);
        introMusic.play();

        slashSound = Gdx.audio.newSound(Gdx.files.internal("audio/slash.mp3"));
    }

    //loads one frame onto disk
    private void loadFrame(int index) {
        if (index < 0) index = 0;
        if (index >= FRAME_COUNT) index = FRAME_COUNT - 1;

        if (currentTexture != null) {
            currentTexture.dispose();
            currentTexture = null;
        }

        String fileName = String.format("intro/intro_%04d.png", index + 1);
        currentTexture = new Texture(Gdx.files.internal(fileName));
        currentFrameRegion = new TextureRegion(currentTexture);
        currentFrameIndex = index;
    }

    @Override
    public void render(float delta) {
        stateTime += delta;

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        int frameIndex = (int) (stateTime * FPS);
        if (frameIndex >= FRAME_COUNT) {
            frameIndex = FRAME_COUNT - 1;
        }

        // loads next png if frame changed
        if (frameIndex != currentFrameIndex) {
            loadFrame(frameIndex);
        }

        batch.begin();
        if (currentFrameRegion != null) {
            batch.draw(currentFrameRegion, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        }
        batch.end();

        // slash sound delay
        if (!slashPlayed && stateTime >= 14f) {
            slashSound.play();
            slashPlayed = true;
        }

        // allow skippung
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            goToMenu();
        }

        if (stateTime >= FRAME_COUNT / FPS) {
            goToMenu();
        }
    }

    private void goToMenu() {
        game.setScreen(new MainMenuScreen(game, batch));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (currentTexture != null) {
            currentTexture.dispose();
            currentTexture = null;
        }

        if (slashSound != null) {
            slashSound.dispose();
        }
        if (introMusic != null) {
            introMusic.dispose();
        }
    }
}
