package io.github.soulslight.controller.commands;

import io.github.soulslight.model.GameModel;

/**
 * GoF Pattern: Command (Command Interface) Encapsulates a request as an object, letting you
 * parameterize clients with different requests.
 */
public interface Command {
  void execute(GameModel model);
}
