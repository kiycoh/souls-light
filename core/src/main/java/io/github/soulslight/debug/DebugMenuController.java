package io.github.soulslight.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pattern: Command (Invoker) Manages debug commands registration, selection, and execution. Acts as
 * the central controller for the debug menu system.
 */
public class DebugMenuController {

  private final List<DebugCommand> commands = new ArrayList<>();
  private int selectedIndex = 0;
  private boolean menuVisible = false;

  /**
   * Registers a new debug command to the menu.
   *
   * @param command The command to register
   */
  public void registerCommand(DebugCommand command) {
    if (command != null) {
      commands.add(command);
    }
  }

  /** Toggles the debug menu visibility. */
  public void toggleMenu() {
    menuVisible = !menuVisible;
  }

  /**
   * Checks if the debug menu is currently visible.
   *
   * @return True if menu is visible
   */
  public boolean isVisible() {
    return menuVisible;
  }

  /** Selects the next command in the list (wraps around). */
  public void selectNext() {
    if (!commands.isEmpty()) {
      selectedIndex = (selectedIndex + 1) % commands.size();
    }
  }

  /** Selects the previous command in the list (wraps around). */
  public void selectPrevious() {
    if (!commands.isEmpty()) {
      selectedIndex = (selectedIndex - 1 + commands.size()) % commands.size();
    }
  }

  /** Executes the currently selected command. */
  public void executeSelected() {
    if (!commands.isEmpty() && selectedIndex >= 0 && selectedIndex < commands.size()) {
      commands.get(selectedIndex).execute();
    }
  }

  /**
   * Returns an unmodifiable view of all registered commands.
   *
   * @return List of registered commands
   */
  public List<DebugCommand> getCommands() {
    return Collections.unmodifiableList(commands);
  }

  /**
   * Gets the currently selected command index.
   *
   * @return The selected index
   */
  public int getSelectedIndex() {
    return selectedIndex;
  }

  /** Closes the debug menu. */
  public void closeMenu() {
    menuVisible = false;
  }

  /** Opens the debug menu. */
  public void openMenu() {
    menuVisible = true;
  }
}
