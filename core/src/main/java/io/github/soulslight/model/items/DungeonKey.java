package io.github.soulslight.model.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.soulslight.manager.ResourceManager;

public class DungeonKey extends AbstractItem {
  @Override
  public String getName() {
    return "Dungeon Key";
  }

  @Override
  public TextureRegion getTexture() {
    return new TextureRegion(
        ResourceManager.getInstance().getItemTexture("item_key")); // Placeholder support
  }
}
