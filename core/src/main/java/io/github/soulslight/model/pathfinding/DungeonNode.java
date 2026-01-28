package io.github.soulslight.model.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.utils.Array;

public class DungeonNode {
  public final int x;
  public final int y;
  public final int index;
  public final boolean isWall;
  private final Array<Connection<DungeonNode>> connections = new Array<>();

  public DungeonNode(int x, int y, int index, boolean isWall) {
    this.x = x;
    this.y = y;
    this.index = index;
    this.isWall = isWall;
  }

  public int getIndex() {
    return index;
  }

  public Array<Connection<DungeonNode>> getConnections() {
    return connections;
  }

  public void addConnection(DungeonNode toNode) {
    connections.add(new DefaultConnection<>(this, toNode));
  }
}
