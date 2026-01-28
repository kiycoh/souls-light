package io.github.soulslight.controller.strategies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.soulslight.model.entities.Player;

public class KeyboardMovementStrategy implements InputStrategy {
  private static final float SPEED = 160f;

  @Override
  public void processInput(Player player, float delta) {
    if (player == null || player.isDead()) return;

    float velX = 0;
    float velY = 0;

    // Keyboard WASD
    if (Gdx.input.isKeyPressed(Input.Keys.W)) velY = SPEED;
    if (Gdx.input.isKeyPressed(Input.Keys.S)) velY = -SPEED;
    if (Gdx.input.isKeyPressed(Input.Keys.A)) velX = -SPEED;
    if (Gdx.input.isKeyPressed(Input.Keys.D)) velX = SPEED;

    player.move(velX, velY);
  }
}
