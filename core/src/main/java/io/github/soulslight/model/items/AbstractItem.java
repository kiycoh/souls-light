package io.github.soulslight.model.items;

import io.github.soulslight.model.inventory.IPickable;

public abstract class AbstractItem implements IPickable, IRenderableItem {
  public abstract String getName();
}
