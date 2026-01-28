package io.github.soulslight.model.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;

public class DungeonGraph implements IndexedGraph<DungeonNode> {
  private DungeonNode[] nodes;
  private int width;
  private int height;
  private final float tileSize;

  public DungeonGraph(TiledMapTileLayer layer) {
    this.width = layer.getWidth();
    this.height = layer.getHeight();
    this.tileSize = layer.getTileWidth();
    this.nodes = new DungeonNode[width * height];

    // Create Nodes
    int index = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        boolean isWall = isWall(layer, x, y);
        nodes[index] = new DungeonNode(x, y, index, isWall);
        index++;
      }
    }

    // Connect Neighbors
    int[] dx = {0, 0, 1, -1};
    int[] dy = {1, -1, 0, 0};

    // Diagonal support
    int[] diagX = {1, 1, -1, -1};
    int[] diagY = {1, -1, 1, -1};

    for (DungeonNode node : nodes) {
      if (node.isWall) continue;

      // Orthogonal
      for (int i = 0; i < 4; i++) {
        int nx = node.x + dx[i];
        int ny = node.y + dy[i];
        addConnectionIfExists(node, nx, ny);
      }

      // Diagonal (Optional, enables smoother movement)
      for (int i = 0; i < 4; i++) {
        int nx = node.x + diagX[i];
        int ny = node.y + diagY[i];

        // Only add diagonal if both adjacent orthogonal tiles are walkable (prevent
        // cutting corners through walls)
        if (isValid(nx, ny) && !isWall(nx, ny)) {
          if (!isWall(node.x + diagX[i], node.y) && !isWall(node.x, node.y + diagY[i])) {
            addConnectionIfExists(node, nx, ny);
          }
        }
      }
    }
  }

  private void addConnectionIfExists(DungeonNode node, int nx, int ny) {
    if (isValid(nx, ny)) {
      DungeonNode neighbor = getNode(nx, ny);
      if (!neighbor.isWall) {
        node.addConnection(neighbor);
      }
    }
  }

  public DungeonNode getNode(int x, int y) {
    if (!isValid(x, y)) return null;
    return nodes[y * width + x];
  }

  public DungeonNode getNodeAtWorldPos(float worldX, float worldY) {
    int x = (int) (worldX / tileSize);
    int y = (int) (worldY / tileSize);
    return getNode(x, y);
  }

  private boolean isValid(int x, int y) {
    return x >= 0 && x < width && y >= 0 && y < height;
  }

  private boolean isWall(int x, int y) {
    if (!isValid(x, y)) return true;
    return nodes[y * width + x].isWall;
  }

  // Helper to parse from layer
  private boolean isWall(TiledMapTileLayer layer, int x, int y) {
    TiledMapTileLayer.Cell cell = layer.getCell(x, y);
    if (cell == null || cell.getTile() == null) return true; // Treat void as wall

    // Check property "type" == "wall" OR "isWall" boolean
    Object typeObj = cell.getTile().getProperties().get("type");
    if ("wall".equals(typeObj)) return true;

    Object isWallObj = cell.getTile().getProperties().get("isWall");
    if (Boolean.TRUE.equals(isWallObj)) return true;

    return false;
  }

  @Override
  public int getIndex(DungeonNode node) {
    return node.getIndex();
  }

  @Override
  public int getNodeCount() {
    return nodes.length;
  }

  @Override
  public Array<Connection<DungeonNode>> getConnections(DungeonNode fromNode) {
    return fromNode.getConnections();
  }
}
