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

  /**
   * Constructs a DungeonGraph from a TiledMapTileLayer by creating a grid of DungeonNode instances
   * and connecting walkable neighbors.
   *
   * <p>Behavior: - Reads layer width, height and tile size (tile width is used; square-tile
   * assumption). - Allocates a row-major nodes array of size width * height (index increments with
   * x then y). - For every tile position (x,y) a DungeonNode is created with its x, y, index and a
   * walkability flag determined by isWall(layer, x, y). - Wall nodes are still created but are not
   * connected to neighbors.
   *
   * <p>Neighbor connectivity: - Orthogonal neighbors (up, down, left, right) are considered and
   * connected when valid. - Diagonal neighbors are considered as well, but a diagonal connection is
   * only added if both adjacent orthogonal neighbors are walkable. This prevents corner-cutting
   * through diagonal gaps between walls. - The actual addition of edges is delegated to
   * addConnectionIfExists(...) and coordinate validity/occupancy checks are performed via
   * isValid(...) and isWall(...).
   *
   * <p>Assumptions and notes: - The provided layer must be non-null and have meaningful
   * width/height/tile width values. - Tile size is taken from layer.getTileWidth(); if tiles are
   * non-square an alternate approach may be required. - The nodes array is filled in row-major
   * order: index == y * width + x. - Complexity is O(width * height) for node creation and neighbor
   * evaluation. - Diagonal movement is enabled only when it does not allow moving through corners
   * of walls.
   *
   * @param layer the TiledMapTileLayer used to build the graph; its tiles determine node positions
   *     and wall occupancy
   * @throws NullPointerException if layer is null
   */
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

    // Connect Neighbors (All 8 directions for better movement? Or 4? Let's start
    // with 8 for
    // smoothness)
    // Actually, 8 directions might cause clipping corners. Let's stick to 4 first,
    // or 8 with checks.
    // The plan suggested 4 (Up, Down, Left, Right). Let's stick to 4.
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
