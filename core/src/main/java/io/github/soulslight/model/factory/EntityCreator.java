package io.github.soulslight.model.factory;

import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.model.entities.Entity;

/** GoF Pattern: Factory Method (Creator) Defines the interface for creating an Entity. */
public abstract class EntityCreator {
  /**
   * Factory method to create an Entity.
   *
   * @param world The Box2D world.
   * @param x The x-coordinate.
   * @param y The y-coordinate.
   * @param args Additional arguments specific to the entity type.
   * @return The created Entity.
   */
  public abstract Entity createEntity(World world, float x, float y, Object... args);
}
