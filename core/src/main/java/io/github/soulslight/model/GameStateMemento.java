package io.github.soulslight.model;

import java.util.ArrayList;
import java.util.List;

public class GameStateMemento {
  // list supports 1, 2 or N players
  public List<PlayerMemento> players = new ArrayList<>();
  public List<EnemyMemento> enemies = new ArrayList<>();
  public List<ProjectileMemento> projectiles = new ArrayList<>();
  public long seed;
  public int currentLevelIndex;

  // No-arg constructor
  public GameStateMemento() {}

  public GameStateMemento(
      List<PlayerMemento> players,
      List<EnemyMemento> enemies,
      List<ProjectileMemento> projectiles,
      long seed,
      int currentLevelIndex) {
    this.players = players;
    this.enemies = enemies;
    this.projectiles = projectiles;
    this.seed = seed;
    this.currentLevelIndex = currentLevelIndex;
  }
}
