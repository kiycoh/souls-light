package io.github.soulslight.model.room;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.soulslight.model.Constants;

/**
 * A portal entity that allows level completion when player interacts. Uses a Box2D sensor to detect
 * player proximity.
 *
 * <p>GoF Pattern: State (Context)
 */
public class Portal {

  private static final int FRAME_WIDTH = 64;
  private static final int FRAME_HEIGHT = 64;
  private static final float FRAME_DURATION = 0.1f;

  private final Vector2 position;
  private Body sensorBody;
  private boolean playerInRange;

  // --- State Pattern ---
  private PortalState currentState;
  private final PortalState openingState;
  private final PortalState activeState;
  private final PortalState closingState;
  private final PortalState removedState;

  // --- Assets ---
  private Texture texture;
  private Animation<TextureRegion> openingAnim;
  private Animation<TextureRegion> activeAnim;
  private Animation<TextureRegion> closingAnim;

  private float stateTime;

  /**
   * Creates a portal at the specified position.
   *
   * @param x X position in world units
   * @param y Y position in world units
   */
  public Portal(float x, float y) {
    this.position = new Vector2(x, y);
    this.playerInRange = false;
    this.stateTime = 0f;

    loadAssets();

    // Initialize States
    this.openingState = new OpeningState();
    this.activeState = new ActiveState();
    this.closingState = new ClosingState();
    this.removedState = new RemovedState();

    // Start in Opening State
    changeState(openingState);
  }

  private void loadAssets() {
    // Note: in a larger project, use AssetManager.
    // Assuming the file is at assets/images/Purple Portal.png
    try {
      texture = new Texture(Gdx.files.internal("images/Purple Portal.png"));
      TextureRegion[][] tmp = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);

      // Row 0: Active Loop
      activeAnim = new Animation<>(FRAME_DURATION, tmp[0]);
      activeAnim.setPlayMode(Animation.PlayMode.LOOP);

      // Row 1: Initialization (Opening)
      openingAnim = new Animation<>(FRAME_DURATION, tmp[1]);
      openingAnim.setPlayMode(Animation.PlayMode.NORMAL);

      // Row 2: Removing (Closing)
      closingAnim = new Animation<>(FRAME_DURATION, tmp[2]);
      closingAnim.setPlayMode(Animation.PlayMode.NORMAL);
    } catch (Exception e) {
      Gdx.app.error("Portal", "Failed to load sprite sheet", e);
      // Fallback or crash gracefully? For now, we assume success or let it crash.
    }
  }

  /**
   * Creates the portal's sensor body in the physics world.
   *
   * @param world The Box2D physics world
   */
  public void createBody(World world) {
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.StaticBody;
    bodyDef.position.set(position);

    sensorBody = world.createBody(bodyDef);

    CircleShape shape = new CircleShape();
    shape.setRadius(
        32f
            / Constants
                .PPM); // Interaction radius (adjusted for PPM if needed, assuming usage matches
    // existing code)

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = shape;
    fixtureDef.isSensor = true;
    fixtureDef.filter.categoryBits = Constants.BIT_SENSOR;
    fixtureDef.filter.maskBits = Constants.BIT_PLAYER;

    sensorBody.createFixture(fixtureDef);
    sensorBody.setUserData(this);

    shape.dispose();
  }

  /**
   * Destroys the portal's body from the physics world.
   *
   * @param world The Box2D physics world
   */
  public void destroyBody(World world) {
    if (sensorBody != null) {
      world.destroyBody(sensorBody);
      sensorBody = null;
    }
  }

  /** Disposes of the texture resources. MUST be called when the level/screen is destroyed. */
  public void dispose() {
    if (texture != null) {
      texture.dispose();
    }
  }

  /** Called when player enters the portal's sensor range. */
  public void onPlayerEnter() {
    playerInRange = true;
  }

  /** Called when player exits the portal's sensor range. */
  public void onPlayerExit() {
    playerInRange = false;
  }

  /**
   * Updates the portal logic and animation.
   *
   * @param dt Delta time
   */
  public void update(float dt) {
    stateTime += dt;
    currentState.update(dt);
  }

  /**
   * Attempts to activate the portal.
   *
   * @return True if activation was successful (state changed to Closing)
   */
  public boolean tryActivate() {
    return currentState.tryActivate();
  }

  /**
   * Gets the current texture frame to render.
   *
   * @return TextureRegion for the current state and time.
   */
  public TextureRegion getFrame() {
    // If texture failed to load, return null or handle it.
    if (texture == null) return null;
    return currentState.getFrame();
  }

  private void changeState(PortalState newState) {
    this.currentState = newState;
    this.stateTime = 0f;
  }

  // --- Getters ---

  public Vector2 getPosition() {
    return position.cpy();
  }

  public boolean isPlayerInRange() {
    return playerInRange;
  }

  public Body getBody() {
    return sensorBody;
  }

  // Expose state status for GameController if needed
  public boolean isRemoved() {
    return currentState == removedState;
  }

  public boolean isActivated() {
    return currentState == closingState || currentState == removedState;
  }

  // --- State Pattern Implementation ---

  /** GoF Pattern: State (State Interface) */
  private interface PortalState {
    void update(float dt);

    boolean tryActivate();

    TextureRegion getFrame();
  }

  /** GoF Pattern: State (ConcreteState) The portal is appearing/opening up. */
  private class OpeningState implements PortalState {
    @Override
    public void update(float dt) {
      if (openingAnim.isAnimationFinished(stateTime)) {
        changeState(activeState);
      }
    }

    @Override
    public boolean tryActivate() {
      return false; // Cannot activate while opening
    }

    @Override
    public TextureRegion getFrame() {
      return openingAnim.getKeyFrame(stateTime, false);
    }
  }

  /** GoF Pattern: State (ConcreteState) The portal is open and idling. */
  private class ActiveState implements PortalState {
    @Override
    public void update(float dt) {
      // Loop animation, stay in this state until interaction
    }

    @Override
    public boolean tryActivate() {
      if (playerInRange) {
        changeState(closingState);
        return true;
      }
      return false;
    }

    @Override
    public TextureRegion getFrame() {
      return activeAnim.getKeyFrame(stateTime, true);
    }
  }

  /** GoF Pattern: State (ConcreteState) The portal is shrinking/closing after activation. */
  private class ClosingState implements PortalState {
    @Override
    public void update(float dt) {
      if (closingAnim.isAnimationFinished(stateTime)) {
        changeState(removedState);
      }
    }

    @Override
    public boolean tryActivate() {
      return false; // Already activated/closing
    }

    @Override
    public TextureRegion getFrame() {
      return closingAnim.getKeyFrame(stateTime, false);
    }
  }

  /** GoF Pattern: State (ConcreteState) The portal is gone. */
  private class RemovedState implements PortalState {
    @Override
    public void update(float dt) {
      // Do nothing
    }

    @Override
    public boolean tryActivate() {
      return false;
    }

    @Override
    public TextureRegion getFrame() {
      return null; // Nothing to draw
    }
  }
}
