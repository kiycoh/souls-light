package io.github.soulslight.controller.strategies;

import io.github.soulslight.model.entities.Player;

/**
 * GoF Pattern: Strategy (Strategy Interface) Encapsulates the input processing algorithm for
 * movement.
 */
public interface InputStrategy {
  void processInput(Player player, float delta);
}
