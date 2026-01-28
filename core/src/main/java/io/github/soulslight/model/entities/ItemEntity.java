package io.github.soulslight.model.entities;

import com.badlogic.gdx.physics.box2d.*;
import io.github.soulslight.model.Constants;
import io.github.soulslight.model.items.AbstractItem;
import io.github.soulslight.model.items.IRenderableItem;

public class ItemEntity extends Entity {

  private final AbstractItem item;
  private boolean markedForRemoval = false;

  public ItemEntity(AbstractItem item, World world, float x, float y) {
    super();
    this.item = item; // Inventory item logic
    this.position.set(x, y);

    // Set visual texture if available
    if (item instanceof IRenderableItem) {
      this.textureRegion = ((IRenderableItem) item).getTexture();
    }

    createBody(world, x, y);
  }

  private void createBody(World world, float x, float y) {
    BodyDef bdef = new BodyDef();
    bdef.position.set(x, y);
    bdef.type = BodyDef.BodyType.StaticBody; // Items don't move
    this.body = world.createBody(bdef);

    FixtureDef fdef = new FixtureDef();
    CircleShape shape = new CircleShape();
    shape.setRadius(8f); // Small pickup radius
    fdef.shape = shape;
    fdef.isSensor = true; // Walk through it
    fdef.filter.categoryBits = Constants.BIT_ITEM;
    fdef.filter.maskBits = Constants.BIT_PLAYER;

    this.body.createFixture(fdef);
    this.body.setUserData(this);
    shape.dispose();
  }

  public AbstractItem getItem() {
    return item;
  }

  public void kill() {
    this.markedForRemoval = true;
  }

  public boolean isMarkedForRemoval() {
    return markedForRemoval;
  }
}
