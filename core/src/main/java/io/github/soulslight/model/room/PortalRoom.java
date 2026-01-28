package io.github.soulslight.model.room;

import com.badlogic.gdx.physics.box2d.World;

/**
 * A special room variant that contains a portal for level completion. Does not lock doors when
 * player enters. Spawns a Portal entity that requires player interaction.
 */
public class PortalRoom extends Room {

  private Portal portal;
  private World world;

  /**
   * Creates a portal room with the specified bounds.
   *
   * @param id Unique identifier for this room
   * @param x X position of the room in world units
   * @param y Y position of the room in world units
   * @param width Width of the room in world units
   * @param height Height of the room in world units
   */
  public PortalRoom(String id, float x, float y, float width, float height) {
    super(id, x, y, width, height);
    // Immediately transition to cleared state - portal rooms don't lock
    transitionTo(ClearedState.INSTANCE);
  }

  /**
   * Initializes the portal room with a physics world and creates the portal.
   *
   * @param world The Box2D physics world
   */
  public void initializePortal(World world) {
    this.world = world;

    // Create portal at room center
    float centerX = getBounds().x + getBounds().width / 2f;
    float centerY = getBounds().y + getBounds().height / 2f;

    portal = new Portal(centerX, centerY);
    portal.createBody(world);
  }

  /** Override: Portal rooms do not trigger lockdown. */
  @Override
  public void onPlayerEntered() {
    // Do nothing - portal rooms stay open
  }

  /**
   * Attempts to activate the portal. Requires player interaction.
   *
   * @return True if the portal was activated (level complete)
   */
  public boolean tryActivatePortal() {
    return portal != null && portal.tryActivate();
  }

  /**
   * Checks if the player is in range of the portal.
   *
   * @return True if player can interact with portal
   */
  public boolean isPlayerNearPortal() {
    return portal != null && portal.isPlayerInRange();
  }

  /**
   * Checks if the portal has been activated.
   *
   * @return True if level is complete
   */
  public boolean isPortalActivated() {
    return portal != null && portal.isActivated();
  }

  /**
   * Gets the portal entity.
   *
   * @return The portal, or null if not initialized
   */
  public Portal getPortal() {
    return portal;
  }

  @Override
  public void update(float dt) {
    super.update(dt);
    if (portal != null) {
      portal.update(dt);
    }
  }

  /** Cleans up the portal. */
  public void dispose() {
    if (portal != null && world != null) {
      portal.destroyBody(world);
      portal.dispose();
      portal = null;
    }
  }
}
