package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.soulslight.controller.GameController;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.manager.TextureManager;
import io.github.soulslight.model.AbstractEnemy;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.Player;
import io.github.soulslight.model.Projectile;

public class GameScreen implements Screen {

    private final SpriteBatch batch;
    private final GameModel model;
    private final GameController controller;

    private final GameHUD hud;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Box2DDebugRenderer debugRenderer;

    public GameScreen(SpriteBatch batch, GameModel model, GameController controller) {
        this.batch = batch;
        this.model = model;
        this.controller = controller;

        //Setup Camera
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(720, 480, camera);

        //Setup Renderer Mappa
        this.mapRenderer = new OrthogonalTiledMapRenderer(model.getMap(), batch);

        // Setup HUD e Debug
        this.hud = new GameHUD();
        this.debugRenderer = new Box2DDebugRenderer();

        //Caricamento Assets
        TextureManager.load();
    }

    @Override
    public void show() {
        //Attiva il controller per ricevere gli input
        Gdx.input.setInputProcessor(controller);
        adjustCameraToMap();
    }

    @Override
    public void render(float delta) {
        if (!model.isPaused()) {
            controller.update(delta); // Gestione input continuo (WASD)
            model.update(delta);      // Fisica e logica di gioco
        }

        camera.update();
        ScreenUtils.clear(0, 0, 0, 1);

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        Player player = model.getPlayer();
        if (player != null) {
            if (player.isDead()) batch.setColor(Color.RED);
            drawEntity(TextureManager.get("player"), player.getPosition(), 32, 32);
            batch.setColor(Color.WHITE);
        }

        for (AbstractEnemy enemy : model.getActiveEnemies()) {
            if (enemy.isDead()) continue;

            Texture tex = TextureManager.getEnemyTexture(enemy);
            float size = (enemy instanceof io.github.soulslight.model.Oblivion) ? 64 : 32;

            drawEntity(tex, enemy.getPosition(), size, size);
        }

        Texture tArrow = TextureManager.get("arrow");
        if (tArrow == null) tArrow = TextureManager.get("player");

        for (Projectile p : model.getProjectiles()) {
            batch.draw(tArrow,
                p.getPosition().x - 16, p.getPosition().y - 4,
                16, 4, 32, 8, 1, 1, p.getRotation(),
                0, 0, tArrow.getWidth(), tArrow.getHeight(), false, false
            );
        }
        batch.end();

        hud.render(batch, player, model.getActiveEnemies());

        if (GameManager.DEBUG_MODE) {
            debugRenderer.render(model.getWorld(), camera.combined);
        }
    }

    // Metodo helper per disegnare centrato
    private void drawEntity(Texture tex, Vector2 pos, float width, float height) {
        if (tex != null) {
            batch.draw(tex, pos.x - width / 2, pos.y - height / 2, width, height);
        }
    }

    private void adjustCameraToMap() {
        if (model.getMap() == null) return;

        MapProperties prop = model.getMap().getProperties();
        int mapWidth = prop.get("width", Integer.class);
        int mapHeight = prop.get("height", Integer.class);
        int tileWidth = prop.get("tilewidth", Integer.class);
        int tileHeight = prop.get("tileheight", Integer.class);

        float totalMapWidth = mapWidth * tileWidth;
        float totalMapHeight = mapHeight * tileHeight;

        viewport.setWorldSize(totalMapWidth, totalMapHeight);
        viewport.apply();
        camera.position.set(totalMapWidth / 2f, totalMapHeight / 2f, 0);
        camera.update();
    }

   //come per restartGame() al momento non funziona
   /* public SpriteBatch getBatch() {
        return this.batch;
    }*/

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        if (mapRenderer != null) mapRenderer.dispose();
        if (debugRenderer != null) debugRenderer.dispose();
        if (hud != null) hud.dispose();
    }
}
