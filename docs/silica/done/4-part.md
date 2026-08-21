---
source_file: "/home/kiycoh/Documents/dev/souls-light/docs/silica/Inbox/Soul_s Light Docs.docx"
type: Note
---

package io.github.soulslight.model; import com.badlogic.gdx.math.Vector2; import
com.badlogic.gdx.physics.box2d.World; import com.badlogic.gdx.utils.Disposable; public class GameModel
implements Disposable { // --- CONSTANTS --- // "Will" is a shared value public static final float MAX_WILL =
100f; // --- Box2D PHYSICS --- private final World physicsWorld; // --- GAME STATE --- private float
currentWill; private boolean isPaused; // --- ENTITIES --- // private PlayerEntity nox; // private PlayerEntity
lux; public GameModel() { // Physics initialization this.physicsWorld = new World(new Vector2(0, 0), true);
// Game state initialization this.currentWill = MAX_WILL / 2; this.isPaused = false; // Entity initialization //
createEnemies(); } /** * Update physics and game logic. * @param deltaTime Time elapsed since the last
update in seconds. */ public void update(float deltaTime) { if (isPaused) return; // Box2D physics update
physicsWorld.step(1/60f, 6, 2); //nox.update(deltaTime); //lux.update(deltaTime); } // --- VIEW &
CONTROLLER GETTERS --- public World getPhysicsWorld() { return physicsWorld; } public float
getCurrentWill() { return currentWill; } public void setCurrentWill(float currentWill) { this.currentWill =
currentWill; } public boolean isPaused() { return isPaused; } public void setPaused(boolean paused) {} // --DISPOSABLE --- @Override public void dispose() { physicsWorld.dispose(); } } // --- Box2D PHYSICS --- private
final World physicsWorld; // --- GAME STATE --- private float currentWill; private boolean isPaused; // --ENTITIES --- // private PlayerEntity nox; // private PlayerEntity lux; public GameModel() { // Physics
initialization this.physicsWorld = new World(new Vector2(0, 0), true); // Game state initialization
this.currentWill = MAX_WILL / 2; this.isPaused = false; // Entity initialization // createEnemies(); } /** *
Update physics and game logic. * @param deltaTime Time elapsed since the last update in seconds. */ public
void update(float deltaTime) { if (isPaused) return; // Box2D physics update physicsWorld.step(1/60f, 6, 2); //
nox.update(deltaTime); //lux.update(deltaTime); } // --- VIEW & CONTROLLER GETTERS --- public World
getPhysicsWorld() { return physicsWorld; } public float getCurrentWill() { return currentWill; } public void
setCurrentWill(float currentWill) { this.currentWill = currentWill; } public boolean isPaused() { return
isPaused; } public void setPaused(boolean paused) {} // --- DISPOSABLE --- @Override public void dispose() {
physicsWorld.dispose(); } }


**GameScreen**


package io.github.soulslight.view; import com.badlogic.gdx.Gdx; import com.badlogic.gdx.Screen; import
com.badlogic.gdx.graphics.OrthographicCamera; import com.badlogic.gdx.graphics.g2d.SpriteBatch; import
com.badlogic.gdx.utils.ScreenUtils; import com.badlogic.gdx.utils.viewport.FitViewport; import
com.badlogic.gdx.utils.viewport.Viewport; import io.github.soulslight.controller.GameController; import
io.github.soulslight.model.GameModel; public class GameScreen implements Screen { // --- MVC
DEPENDENCIES --- private final SpriteBatch batch; private final GameModel model; private final
GameController controller; // --- PIXEL ART RENDERING --- private static final float WORLD_WIDTH = 480;
private static final float WORLD_HEIGHT = 270; private final OrthographicCamera camera; private final
Viewport viewport; public GameScreen(SpriteBatch batch, GameModel model, GameController controller) {
this.batch = batch; this.model = model; this.controller = controller; // Setup Camera e Viewport for Pixel Art
this.camera = new OrthographicCamera(); // FitViewport maintains aspect ratio while scaling to fit the
screen. this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera); } @Override public void
show() { // Activate controller as input processor Gdx.input.setInputProcessor(controller); } @Override public
void render(float delta) { // MVC Update Loop controller.update(delta); model.update(delta); // Rendering
ScreenUtils.clear(0, 0, 0, 1); // (black background) // Update camera and apply batch camera.update();
batch.setProjectionMatrix(camera.combined); batch.begin(); // to be added: // viewRenderer.render(batch,
model); // tests batch.end(); // Debug Renderer for Box2D (to be added) } @Override public void resize(int
width, int height) { // 5. IMPORTANT: Window resizing handling true pixel viewport.update(width, height,
true); // true = centra la camera } @Override public void pause() { } @Override public void resume() { }
@Override public void hide() { } @Override public void dispose() { // NOTE: No batch.dispose() because
batch is in SoulsLightGame } }


