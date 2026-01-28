package io.github.soulslight.model.factory;

import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.model.entities.Entity;
import io.github.soulslight.model.entities.Player;

/** GoF Pattern: Factory Method (ConcreteCreator) Creates Player entities. */
public class PlayerCreator extends EntityCreator {

  @Override
  public Entity createEntity(World world, float x, float y, Object... args) {
    if (args.length < 1 || !(args[0] instanceof Player.PlayerClass)) {
      throw new IllegalArgumentException("PlayerCreator requires PlayerClass argument");
    }
    Player.PlayerClass playerClass = (Player.PlayerClass) args[0];
    return new Player(playerClass, world, x, y);
  }
}
