package io.github.soulslight.model.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;

public class ManhattanHeuristic implements Heuristic<DungeonNode> {
  @Override
  public float estimate(DungeonNode node, DungeonNode endNode) {
    return Math.abs(endNode.x - node.x) + Math.abs(endNode.y - node.y);
  }
}
