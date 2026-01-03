package io.github.soulslight.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import io.github.soulslight.model.FireDamageDecorator;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.Player;

/** Pattern: Observer The GameController acts as an observer of input events (via InputAdapter). */
public class GameController extends InputAdapter {
  private final GameModel model;
  private static final float SPEED = 100f; // Pixels per second
  private GameModel.GameStateMemento quickSave;

  public GameController(GameModel model) {
    this.model = model;
  }

  @Override
  public boolean keyDown(int keycode) {
    Player player = model.getPlayer();
    if (player == null) return false;

    switch (keycode) {
      case Input.Keys.O:
        // Pattern: Decorator (Usage)
        // Apply Fire Damage to current attack
        player.setAttackStrategy(new FireDamageDecorator(player.getAttackStrategy()));
        System.out.println("Fire Damage Power-up Activated!");
        return true;
      case Input.Keys.F5:
        // Pattern: Memento (Usage - Save)
        quickSave = model.createMemento();
        System.out.println("Game Quick Saved!");
        return true;
      case Input.Keys.F9:
        // Pattern: Memento (Usage - Load)
        if (quickSave != null) {
          model.restoreMemento(quickSave);
          System.out.println("Game Quick Loaded!");
        } else {
          System.out.println("No Quick Save found!");
        }
        return true;
      case Input.Keys.P:
        // Attack
        player.doAnAttack();
        return true;
    }
    return false;
  }

  public void update(float delta) {
    Player player = model.getPlayer();
    if (player == null) return;

    float moveX = 0;
    float moveY = 0;

    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
      moveY += SPEED * delta;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.S)) {
      moveY -= SPEED * delta;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      moveX -= SPEED * delta;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      moveX += SPEED * delta;
    }

    if (moveX != 0 || moveY != 0) {
      player.move(moveX, moveY);
    }
  }
}
