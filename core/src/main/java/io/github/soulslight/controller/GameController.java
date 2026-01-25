package io.github.soulslight.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import io.github.soulslight.debug.DebugMenuController;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.manager.SaveManager;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.room.PortalRoom;
import java.util.List;

public class GameController extends InputAdapter implements ControllerListener {

  private final GameModel model;
  private final SaveManager saveManager;
  private DebugMenuController debugMenuController;
  private static final float SPEED = 160f;

  public GameController(GameModel model) {
    this.model = model;
    this.saveManager = new SaveManager();
    Controllers.addListener(this); // Register for controller events
  }

  @Override
  public boolean keyDown(int keycode) {
    // --- DEBUG MENU CONTROLS (when menu is visible) ---
    if (debugMenuController != null && debugMenuController.isVisible()) {
      switch (keycode) {
        case Input.Keys.UP:
          debugMenuController.selectPrevious();
          return true;
        case Input.Keys.DOWN:
          debugMenuController.selectNext();
          return true;
        case Input.Keys.ENTER:
          debugMenuController.executeSelected();
          return true;
        case Input.Keys.F1:
        case Input.Keys.ESCAPE:
          debugMenuController.closeMenu();
          return true;
        default:
          return true; // Consume all other keys when menu is open
      }
    }

    // --- DEBUG MENU TOGGLE ---
    if (keycode == Input.Keys.F1 && GameManager.DEBUG_MODE) {
      if (debugMenuController != null) {
        debugMenuController.toggleMenu();
      }
      return true;
    }

    List<Player> players = model.getPlayers();
    if (players.isEmpty()) return false;

    Player p1 = players.get(0);
    Player p2 = players.size() > 1 ? players.get(1) : null;

    switch (keycode) {
      // --- PLAYER 1 (Keyboard) ---
      case Input.Keys.SPACE:
        if (p1 != null && !p1.isDead()) p1.attack(model.getActiveEnemies());
        return true;
      case Input.Keys.O:
        if (p1 != null) {
          p1.performSpecialAttack(model.getActiveEnemies());
        }
        return true;
      case Input.Keys.P:
        if (p1 != null) p1.doAnAttack();
        return true;

      // --- PLAYER 2 (Keyboard) ---
      case Input.Keys.ENTER:
      case Input.Keys.NUMPAD_0:
        if (p2 != null && !p2.isDead()) p2.attack(model.getActiveEnemies());
        return true;

      // --- SYSTEM ---
      case Input.Keys.F5:
        saveManager.saveGame(model);
        return true;
      case Input.Keys.F9:
        if (saveManager.hasSaveFile()) {
          saveManager.loadGame(model);
        } else {
          Gdx.app.log("Controller", "Nessun file di salvataggio trovato (savegame.json)!");
        }
        return true;
      case Input.Keys.NUM_0: // Debug toggles with 0 (top row)
        GameManager.DEBUG_MODE = !GameManager.DEBUG_MODE;
        Gdx.app.log("Controller", "Debug Mode: " + GameManager.DEBUG_MODE);
        return true;

      case Input.Keys.E: // Portal activation
        if (model.getLevel() != null) {
          boolean activated = false;

          // Try dungeon-style PortalRoom first
          if (model.getLevel().getRoomManager() != null) {
            PortalRoom portalRoom = model.getLevel().getRoomManager().getPortalRoom();
            if (portalRoom != null && portalRoom.tryActivatePortal()) {
              activated = true;
            }
          }

          // Try cave-style direct portal
          if (!activated
              && model.getLevel().getCavePortal() != null
              && model.getLevel().getCavePortal().tryActivate()) {
            activated = true;
          }

          if (activated) {
            Gdx.app.log("Controller", "Portal activated! Advancing to next level.");
            model.setLevelCompleted(true);
          }
        }
        return true;

      default:
        return false;
    }
  }

  public void update(float delta) {
    List<Player> players = model.getPlayers();
    if (players.isEmpty()) return;

    // --- PLAYER 1 MOVEMENT ---
    Player p1 = players.get(0);
    if (p1 != null && !p1.isDead()) {
      float velX = 0;
      float velY = 0;

      // Keyboard
      if (Gdx.input.isKeyPressed(Input.Keys.W)) velY = SPEED;
      if (Gdx.input.isKeyPressed(Input.Keys.S)) velY = -SPEED;
      if (Gdx.input.isKeyPressed(Input.Keys.A)) velX = -SPEED;
      if (Gdx.input.isKeyPressed(Input.Keys.D)) velX = SPEED;

      // Controller 0 Override
      if (Controllers.getControllers().size > 0) {
        Controller c1 = Controllers.getControllers().get(0);
        float axisX = c1.getAxis(c1.getMapping().axisLeftX);
        float axisY = c1.getAxis(c1.getMapping().axisLeftY);
        if (Math.abs(axisX) > 0.2f) velX = axisX * SPEED;
        if (Math.abs(axisY) > 0.2f) velY = -axisY * SPEED; // Y is usually inverted on controllers
      }

      p1.move(velX, velY);
    } else if (p1 != null && p1.getBody() != null) {
      p1.getBody().setLinearVelocity(0, 0);
    }

    // --- PLAYER 2 MOVEMENT ---
    if (players.size() > 1) {
      Player p2 = players.get(1);
      if (p2 != null && !p2.isDead()) {
        float velX = 0;
        float velY = 0;

        // Keyboard
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) velY = SPEED;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) velY = -SPEED;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) velX = -SPEED;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) velX = SPEED;

        // Controller 1 Override
        if (Controllers.getControllers().size > 1) {
          Controller c2 = Controllers.getControllers().get(1);
          float axisX = c2.getAxis(c2.getMapping().axisLeftX);
          float axisY = c2.getAxis(c2.getMapping().axisLeftY);
          if (Math.abs(axisX) > 0.2f) velX = axisX * SPEED;
          if (Math.abs(axisY) > 0.2f) velY = -axisY * SPEED;
        }

        p2.move(velX, velY);
      } else if (p2 != null && p2.getBody() != null) {
        p2.getBody().setLinearVelocity(0, 0);
      }
    }
  }

  // --- ControllerListener Implementation ---

  @Override
  public boolean buttonDown(Controller controller, int buttonCode) {
    List<Player> players = model.getPlayers();
    int controllerIndex = Controllers.getControllers().indexOf(controller, true);

    // Check mapping. Usually 0 is A (Xbox).
    // Let's assume button 0 (A) is attack.
    // Use controller.getMapping().buttonA if available (gdx-controllers 2.x)

    int attackBtn = controller.getMapping().buttonA;

    if (buttonCode == attackBtn) {
      if (controllerIndex == 0 && !players.isEmpty()) {
        Player p1 = players.get(0);
        if (p1 != null && !p1.isDead()) p1.attack(model.getActiveEnemies());
      } else if (controllerIndex == 1 && players.size() > 1) {
        Player p2 = players.get(1);
        if (p2 != null && !p2.isDead()) p2.attack(model.getActiveEnemies());
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean buttonUp(Controller controller, int buttonCode) {
    return false;
  }

  @Override
  public boolean axisMoved(Controller controller, int axisCode, float value) {
    return false; // Handled in update()
  }

  @Override
  public void connected(Controller controller) {
    Gdx.app.log("Controller", "Controller connected: " + controller.getName());
  }

  @Override
  public void disconnected(Controller controller) {
    Gdx.app.log("Controller", "Controller disconnected: " + controller.getName());
  }

  /**
   * Sets the debug menu controller for this controller.
   *
   * @param controller The debug menu controller
   */
  public void setDebugMenuController(DebugMenuController controller) {
    this.debugMenuController = controller;
  }

  /**
   * Gets the debug menu controller.
   *
   * @return The debug menu controller, or null if not set
   */
  public DebugMenuController getDebugMenuController() {
    return debugMenuController;
  }
}
