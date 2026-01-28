package io.github.soulslight.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Disposable;
import io.github.soulslight.controller.commands.AttackCommand;
import io.github.soulslight.controller.commands.Command;
import io.github.soulslight.controller.commands.ConsumeItemCommand;
import io.github.soulslight.controller.commands.InteractCommand;
import io.github.soulslight.controller.commands.SpecialAttackCommand;
import io.github.soulslight.controller.strategies.ControllerMovementStrategy;
import io.github.soulslight.controller.strategies.InputStrategy;
import io.github.soulslight.controller.strategies.KeyboardMovementStrategy;
import io.github.soulslight.debug.DebugMenuController;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.manager.SaveManager;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.entities.Player;
import java.util.List;

public class GameController extends InputAdapter implements ControllerListener, Disposable {

  private final GameModel model;
  private final SaveManager saveManager;
  private DebugMenuController debugMenuController;
  private io.github.soulslight.view.GameScreen gameScreen;

  public GameController(GameModel model) {
    this.model = model;
    this.saveManager = new SaveManager();
    Controllers.addListener(this); // Register for controller events
  }

  @Override
  public boolean keyDown(int keycode) {
    // --- DEBUG MENU CONTROLS (when menu is visible) ---
    if (debugMenuController != null && debugMenuController.isVisible()) {
      return handleDebugMenuInput(keycode);
    }

    // --- DEBUG MENU TOGGLE ---
    if (keycode == Input.Keys.F1 && GameManager.DEBUG_MODE) {
      handleDebugToggle();
      return true;
    }

    // --- PAUSE MENU TOGGLE ---
    if (keycode == Input.Keys.ESCAPE) {
      handlePauseToggle();
      return true;
    }

    // --- GAMEPLAY COMMANDS ---
    return handleGameplayInput(keycode);
  }

  private boolean handleDebugMenuInput(int keycode) {
    switch (keycode) {
      case Input.Keys.UP -> {
        debugMenuController.selectPrevious();
        return true;
      }
      case Input.Keys.DOWN -> {
        debugMenuController.selectNext();
        return true;
      }
      case Input.Keys.ENTER -> {
        debugMenuController.executeSelected();
        return true;
      }
      case Input.Keys.F1, Input.Keys.ESCAPE -> {
        debugMenuController.closeMenu();
        model.setPaused(false);
        return true;
      }
      default -> {
        return true; // Consume all other keys when menu is open
      }
    }
  }

  private void handleDebugToggle() {
    if (debugMenuController != null) {
      debugMenuController.toggleMenu();
      // Pause game when debug menu is open (as per user request)
      if (debugMenuController.isVisible()) {
        model.setPaused(true);
      } else {
        model.setPaused(false);
      }
    }
  }

  private void handlePauseToggle() {
    // If debug menu is open, ESC closes it (handled in handleDebugMenuInput)
    boolean newState = !model.isPaused();
    model.setPaused(newState);

    if (gameScreen != null) {
      gameScreen.updateInputMode(); // Notify screen to switch processors
    }
  }

  private boolean handleGameplayInput(int keycode) {
    Command command = null;
    List<Player> players = model.getPlayers();

    switch (keycode) {
      case Input.Keys.SPACE -> command = new AttackCommand(0); // Player 0
      case Input.Keys.O -> command = new SpecialAttackCommand(0);
      case Input.Keys.P -> {
        if (!players.isEmpty()) players.get(0).doAnAttack();
      }
      case Input.Keys.NUM_1 -> command = new ConsumeItemCommand(0, 0);
      case Input.Keys.NUM_2 -> command = new ConsumeItemCommand(0, 1);
      case Input.Keys.NUM_3 -> command = new ConsumeItemCommand(0, 2);
      case Input.Keys.F5 -> {
        // RESTORE (Load)
        if (saveManager.hasSaveFile()) {
          saveManager.loadGame(model);
        } else {
          Gdx.app.log("Controller", "No save file found!");
        }
      }
      case Input.Keys.F6 -> {
        // SAVE
        saveManager.saveGame(model);
        Gdx.app.log("Controller", "Game Saved (F6)");
      }
      case Input.Keys.NUM_0 -> {
        // Debug toggles with 0 (top row)
        GameManager.DEBUG_MODE = !GameManager.DEBUG_MODE;
        Gdx.app.log("Controller", "Debug Mode: " + GameManager.DEBUG_MODE);
      }

      case Input.Keys.E -> // Portal activation
          command = new InteractCommand();
    }

    if (command != null) {
      command.execute(model);
      return true;
    }

    return false;
  }

  public void update(float delta) {

    List<Player> players = model.getPlayers();

    if (players.isEmpty()) return;

    // --- STRATEGY PATTERN FOR MOVEMENT ---

    // Player 1: Keyboard
    if (!players.isEmpty() && players.get(0) != null) {
      InputStrategy p1Strategy = new KeyboardMovementStrategy();
      p1Strategy.processInput(players.get(0), delta);
    }

    // Player 2: Controller
    if (players.size() > 1 && players.get(1) != null) {
      InputStrategy p2Strategy = new ControllerMovementStrategy(0);
      p2Strategy.processInput(players.get(1), delta);
    }
  }

  // --- ControllerListener Implementation ---

  @Override
  public boolean buttonDown(Controller controller, int buttonCode) {
    int controllerIndex = Controllers.getControllers().indexOf(controller, true);

    // Check mapping
    int attackBtn = controller.getMapping().buttonA;
    int specialBtn = controller.getMapping().buttonY;

    // --- COMMAND MAPPING FOR CONTROLLER ---
    Command command = null;

    // Mapping: Controller 0 -> Player 2 (Index 1)
    if (controllerIndex == 0) {
      if (buttonCode == attackBtn) {
        command = new AttackCommand(1);
      } else if (buttonCode == specialBtn) {
        command = new SpecialAttackCommand(1);
      }
    }

    if (command != null) {
      command.execute(model);
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

  public void setGameScreen(io.github.soulslight.view.GameScreen gameScreen) {
    this.gameScreen = gameScreen;
  }

  @Override
  public void dispose() {
    Controllers.removeListener(this);
    Gdx.app.log("GameController", "Controller listener removed.");
  }
}
