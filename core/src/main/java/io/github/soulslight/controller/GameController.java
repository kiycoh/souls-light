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
  private static final float SPEED = 4.0f; // Meters per second
  private GameModel.GameStateMemento quickSave;

  public GameController(GameModel model) {
    this.model = model;
  }

  @Override
  public boolean keyDown(int keycode) {
    Player player = model.getPlayer();
    if (player == null) return false;

    return switch (keycode) {
      case Input.Keys.O -> {
        // Pattern: Decorator (Usage)
        // Apply Fire Damage to current attack
        player.setAttackStrategy(new FireDamageDecorator(player.getAttackStrategy()));
        Gdx.app.log("GameController", "Fire Damage Power-up Activated!");
        yield true;
      }
      case Input.Keys.F5 -> {
        // Pattern: Memento (Usage - Save)
        quickSave = model.createMemento();
        Gdx.app.log("GameController", "Game Quick Saved!");
        yield true;
      }
      case Input.Keys.F9 -> {
        // Pattern: Memento (Usage - Load)
        if (quickSave != null) {
          model.restoreMemento(quickSave);
          Gdx.app.log("GameController", "Game Quick Loaded!");
        } else {
          Gdx.app.log("GameController", "No Quick Save found!");
        }
        yield true;
      }
      case Input.Keys.P -> {
        // Attack
        player.doAnAttack();
        yield true;
      }
      default -> false;
    };
  }

  public void update(float delta) {
    Player player = model.getPlayer();
    if (player == null) return;

    float velX = 0;
    float velY = 0;

    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
      velY = SPEED;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.S)) {
      velY = -SPEED;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      velX = -SPEED;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      velX = SPEED;
    }

    // Always call move to update velocity (even to 0)
    player.move(velX, velY);
  }
}
