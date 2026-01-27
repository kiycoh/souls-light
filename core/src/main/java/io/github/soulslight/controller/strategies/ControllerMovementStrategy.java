package io.github.soulslight.controller.strategies;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import io.github.soulslight.model.entities.Player;

public class ControllerMovementStrategy implements InputStrategy {
  private static final float SPEED = 160f;
  private final int controllerIndex;

  public ControllerMovementStrategy(int controllerIndex) {
    this.controllerIndex = controllerIndex;
  }

  @Override
  public void processInput(Player player, float delta) {
    if (player == null || player.isDead()) return;

    float velX = 0;
    float velY = 0;

    if (Controllers.getControllers().size > controllerIndex) {
      Controller controller = Controllers.getControllers().get(controllerIndex);
      float axisX = controller.getAxis(controller.getMapping().axisLeftX);
      float axisY = controller.getAxis(controller.getMapping().axisLeftY);

      if (Math.abs(axisX) > 0.2f) velX = axisX * SPEED;
      if (Math.abs(axisY) > 0.2f) velY = -axisY * SPEED;
    }

    player.move(velX, velY);
  }
}
