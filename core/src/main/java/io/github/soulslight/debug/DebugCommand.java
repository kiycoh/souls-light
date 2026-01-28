package io.github.soulslight.debug;

/**
 * Pattern: Command Encapsulates a debug action as an object for deferred execution. All debug menu
 * actions implement this interface.
 */
public interface DebugCommand {

  /** Executes the debug action. */
  void execute();

  /** Returns a short display name for the menu. */
  String getName();

  /** Returns a description of what this command does. */
  String getDescription();
}
