package io.github.soulslight.model.factory;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.model.entities.Entity;
import io.github.soulslight.model.entities.Projectile;

/** GoF Pattern: Factory Method (ConcreteCreator) Creates Projectile entities. */
public class ProjectileCreator extends EntityCreator {

  @Override
  public Entity createEntity(World world, float x, float y, Object... args) {
    // Expected args: Vector2 target [0], Boolean isPlayerSource [1], Object
    // sourceEntity [2] (optional), Float speed [3], Float damage [4]

    if (args.length < 5) {}

    Vector2 target = (Vector2) args[0];
    boolean isPlayerSource = (Boolean) args[1];
    Entity sourceEntity = (Entity) args[2]; // Can be null
    float speed = (Float) args[3];
    float damage = (Float) args[4];
    String type = (args.length > 5) ? (String) args[5] : "arrow";

    return new Projectile(world, x, y, target, isPlayerSource, sourceEntity, speed, damage, type);
  }
}
