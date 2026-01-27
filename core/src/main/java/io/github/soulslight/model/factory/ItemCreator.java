package io.github.soulslight.model.factory;

import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.model.entities.Entity;
import io.github.soulslight.model.entities.ItemEntity;
import io.github.soulslight.model.items.AbstractItem;

/** GoF Pattern: Factory Method (ConcreteCreator) Creates Item entities. */
public class ItemCreator extends EntityCreator {

  @Override
  public Entity createEntity(World world, float x, float y, Object... args) {
    if (args.length < 1 || !(args[0] instanceof AbstractItem)) {
      throw new IllegalArgumentException("ItemCreator requires AbstractItem argument");
    }
    AbstractItem item = (AbstractItem) args[0];
    return new ItemEntity(item, world, x, y);
  }
}
