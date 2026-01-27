package io.github.soulslight.controller.commands;

import io.github.soulslight.model.GameModel;

public class TogglePauseCommand implements Command {
  @Override
  public void execute(GameModel model) {
    model.setPaused(!model.isPaused());
  }
}
