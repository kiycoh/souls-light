package io.github.soulslight.manager;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import io.github.soulslight.model.pathfinding.DungeonGraph;
import io.github.soulslight.model.pathfinding.DungeonNode;
import io.github.soulslight.model.pathfinding.ManhattanHeuristic;

public class PathfindingManager {
  private DungeonGraph graph;
  private IndexedAStarPathFinder<DungeonNode> pathFinder;
  private ManhattanHeuristic heuristic;

  public PathfindingManager(TiledMapTileLayer layer) {
    this.graph = new DungeonGraph(layer);
    this.pathFinder = new IndexedAStarPathFinder<>(graph);
    this.heuristic = new ManhattanHeuristic();
  }

  public DefaultGraphPath<DungeonNode> findPath(
      float startX, float startY, float endX, float endY) {
    DungeonNode startNode = graph.getNodeAtWorldPos(startX, startY);
    DungeonNode endNode = graph.getNodeAtWorldPos(endX, endY);

    if (startNode == null || endNode == null || endNode.isWall) return null;

    DefaultGraphPath<DungeonNode> path = new DefaultGraphPath<>();
    boolean found = pathFinder.searchNodePath(startNode, endNode, heuristic, path);
    return found ? path : null;
  }

  public boolean isWalkable(float x, float y) {
    DungeonNode node = graph.getNodeAtWorldPos(x, y);
    return node != null && !node.isWall;
  }
}
