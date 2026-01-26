package io.github.soulslight.model.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.soulslight.manager.ResourceManager;
import io.github.soulslight.model.inventory.IStackable;

public class HealthPotion extends AbstractItem implements IStackable {
  @Override
  public int getMaximumStackAmount() {
    return 5;
  }

  @Override
  public String getName() {
    return "Health Potion";
  }

  @Override
  public TextureRegion getTexture() {
    return new TextureRegion(
        ResourceManager.getInstance().getItemTexture("inv_objects/health_potion.png"));
  }
}
