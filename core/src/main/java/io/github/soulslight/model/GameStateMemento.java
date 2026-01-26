package io.github.soulslight.model;

import java.util.ArrayList;
import java.util.List;

public class GameStateMemento {
  // list supports 1, 2 or N players
  public List<PlayerMemento> players = new ArrayList<>();
  public List<EnemyMemento> enemies = new ArrayList<>();
  public List<ProjectileMemento> projectiles = new ArrayList<>();
  public List<RoomMemento> rooms = new ArrayList<>();
  public List<DoorMemento> doors = new ArrayList<>();
  public List<PortalMemento> portals = new ArrayList<>();
  public long seed;
  public int currentLevelIndex;

  // No-arg constructor
  public GameStateMemento() {}

  public GameStateMemento(
      List<PlayerMemento> players,
      List<EnemyMemento> enemies,
      List<ProjectileMemento> projectiles,
      List<RoomMemento> rooms,
      List<DoorMemento> doors,
      List<PortalMemento> portals,
      long seed,
      int currentLevelIndex) {
    this.players = players;
    this.enemies = enemies;
    this.projectiles = projectiles;
    this.rooms = rooms;
    this.doors = doors;
    this.portals = portals;
    this.seed = seed;
    this.currentLevelIndex = currentLevelIndex;
  }
}
