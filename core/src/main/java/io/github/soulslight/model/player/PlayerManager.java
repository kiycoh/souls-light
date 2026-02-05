package io.github.soulslight.model.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.manager.SettingsManager;
import io.github.soulslight.model.Constants;
import io.github.soulslight.model.PlayerMemento;
import io.github.soulslight.model.combat.ProjectileListener;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.factory.EntityCreator;
import io.github.soulslight.model.factory.PlayerCreator;
import io.github.soulslight.model.inventory.IPickable;
import io.github.soulslight.model.inventory.InventorySlot;
import io.github.soulslight.model.map.Level;
import io.github.soulslight.model.room.RoomData;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages player lifecycle: spawning, updates, revive logic, and state persistence. Extracted from
 * GameModel to satisfy Single Responsibility Principle.
 */
public class PlayerManager {

  private final List<Player> players;
  private final EntityCreator playerCreator;

  public PlayerManager() {
    this.players = new ArrayList<>();
    this.playerCreator = new PlayerCreator();
  }

  /**
   * Spawns players at the given spawn point.
   *
   * @param world Physics world
   * @param spawn Spawn position
   * @param projectileListener Listener for projectile events
   * @param damageListener Listener for damage events
   */
  public void spawnPlayers(
      World world,
      Vector2 spawn,
      ProjectileListener projectileListener,
      Player.DamageListener damageListener) {

    players.clear();
    GameManager.getInstance().clearPlayers();

    // Player 1
    Player.PlayerClass p1Class = GameManager.getInstance().getPlayerClass(0);
    Gdx.app.log("PlayerManager", "Spawning P1 as: " + p1Class);
    Player p1 = (Player) playerCreator.createEntity(world, spawn.x, spawn.y, p1Class);
    p1.addProjectileListener(projectileListener);
    p1.addDamageListener(damageListener);
    players.add(p1);
    GameManager.getInstance().addPlayer(p1);

    // Player 2 (spawn slightly offset) - Only if NOT single player
    if (!SettingsManager.getInstance().isSinglePlayer()) {
      Player.PlayerClass p2Class = GameManager.getInstance().getPlayerClass(1);
      Gdx.app.log("PlayerManager", "Spawning P2 as: " + p2Class);
      Player p2 = (Player) playerCreator.createEntity(world, spawn.x + 20, spawn.y, p2Class);
      p2.addProjectileListener(projectileListener);
      p2.addDamageListener(damageListener);
      players.add(p2);
      GameManager.getInstance().addPlayer(p2);
    }
  }

  /**
   * Updates all players.
   *
   * @param deltaTime Time since last frame
   */
  public void updatePlayers(float deltaTime) {
    for (Player p : players) {
      if (p != null) {
        p.update(deltaTime);
      }
    }
  }

  /**
   * Handles revive logic between players.
   *
   * @param deltaTime Time since last frame
   */
  public void updateReviveLogic(float deltaTime) {
    for (Player activePlayer : players) {
      if (activePlayer == null || activePlayer.isDead()) continue;

      float velSq =
          activePlayer.getBody() != null ? activePlayer.getBody().getLinearVelocity().len2() : 0f;
      boolean isStill = activePlayer.getBody() != null && velSq < 5.0f;

      boolean revivingSomeone = false;

      if (isStill) {
        for (Player deadPlayer : players) {
          if (deadPlayer == null || !deadPlayer.isDead()) continue;

          float dist = activePlayer.getPosition().dst(deadPlayer.getPosition());

          if (dist < Constants.REVIVE_DISTANCE) {
            revivingSomeone = true;
            activePlayer.setReviveAttemptTimer(activePlayer.getReviveAttemptTimer() + deltaTime);

            if ((int) activePlayer.getReviveAttemptTimer()
                > (int) (activePlayer.getReviveAttemptTimer() - deltaTime)) {
              Gdx.app.log(
                  "ReviveDebug", "Reviving... Timer: " + activePlayer.getReviveAttemptTimer());
            }

            if (activePlayer.getReviveAttemptTimer() >= Constants.REVIVE_TIME) {
              deadPlayer.revive();
              activePlayer.setReviveAttemptTimer(0f);
              Gdx.app.log("PlayerManager", "Player revived!");
            }
            break;
          }
        }
      }

      if (!revivingSomeone) {
        activePlayer.setReviveAttemptTimer(0f);
      }
    }
  }

  /**
   * Creates memento snapshots of all players.
   *
   * @return List of player mementos
   */
  public List<PlayerMemento> createMementos() {
    List<PlayerMemento> playerStates = new ArrayList<>();
    for (Player p : players) {
      List<String> inventoryItems = new ArrayList<>();
      if (p.getInventory() != null) {
        for (InventorySlot slot : p.getInventory().getItemSlots()) {
          if (slot.notEmpty()) {
            for (int i = 0; i < slot.getAmount(); i++) {
              inventoryItems.add(slot.peek().getClass().getName());
            }
          }
        }
      }
      playerStates.add(
          new PlayerMemento(
              p.getType(), p.getHealth(), p.getPosition().x, p.getPosition().y, inventoryItems));
    }
    return playerStates;
  }

  /**
   * Restores players from memento data.
   *
   * @param mementos Player state snapshots
   * @param world Physics world
   * @param level Current level (for safe spawn calculation)
   * @param projectileListener Projectile event listener
   * @param damageListener Damage event listener
   */
  public void restorePlayers(
      List<PlayerMemento> mementos,
      World world,
      Level level,
      ProjectileListener projectileListener,
      Player.DamageListener damageListener) {

    players.clear();
    GameManager.getInstance().clearPlayers();

    for (PlayerMemento pm : mementos) {
      Vector2 savedPos = new Vector2(pm.x, pm.y);
      Vector2 safePos = getSafeSpawnPosition(savedPos, level);

      Player newPlayer = (Player) playerCreator.createEntity(world, safePos.x, safePos.y, pm.type);
      newPlayer.addProjectileListener(projectileListener);
      newPlayer.addDamageListener(damageListener);
      newPlayer.setHealth(pm.health);

      // Restore Inventory
      if (pm.inventoryItems != null) {
        for (String className : pm.inventoryItems) {
          try {
            Class<?> clazz = Class.forName(className);
            IPickable item = (IPickable) clazz.getDeclaredConstructor().newInstance();
            newPlayer.getInventory().addItem(item);
          } catch (Exception e) {
            Gdx.app.error("PlayerManager", "Failed to restore item: " + className, e);
          }
        }
      }

      // Sync GameManager class selection for future levels
      GameManager.getInstance().setPlayerClass(players.size(), pm.type);

      players.add(newPlayer);
      GameManager.getInstance().addPlayer(newPlayer);
    }
  }

  /** Finds a valid spawn point prioritizing start rooms over portal rooms. */
  public Vector2 findFirstFloorSpawn(TiledMap map, List<RoomData> roomData) {
    if (roomData != null && !roomData.isEmpty()) {
      for (RoomData room : roomData) {
        if (!room.isPortalRoom()) {
          Vector2 spawn = findSpawnInRoom(map, room);
          if (spawn != null) return spawn;
        }
      }
    }

    // Fallback: Naive bottom-up scan
    if (map == null || map.getLayers().getCount() == 0) {
      return new Vector2(17, 17);
    }

    TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
    int w = layer.getWidth();
    int h = layer.getHeight();
    float tileSize = layer.getTileWidth();

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        if (cell == null || cell.getTile() == null) continue;

        var props = cell.getTile().getProperties();
        boolean isFloor;
        if (props.containsKey("type")) {
          isFloor = "floor".equals(props.get("type", String.class));
        } else {
          isFloor = !props.get("isWall", false, Boolean.class);
        }

        if (isFloor) {
          float px = x * tileSize + tileSize / 2f;
          float py = y * tileSize + tileSize / 2f;
          return new Vector2(px, py);
        }
      }
    }

    return new Vector2(17, 17);
  }

  private Vector2 findSpawnInRoom(TiledMap map, RoomData room) {
    TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
    float tileSize = layer.getTileWidth();

    int startX = (int) (room.bounds().x / tileSize);
    int startY = (int) (room.bounds().y / tileSize);
    int endX = (int) ((room.bounds().x + room.bounds().width) / tileSize);
    int endY = (int) ((room.bounds().y + room.bounds().height) / tileSize);

    for (int y = startY; y < endY; y++) {
      for (int x = startX; x < endX; x++) {
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        if (cell != null && cell.getTile() != null) {
          String type = cell.getTile().getProperties().get("type", String.class);
          if ("floor".equals(type)) {
            float px = x * tileSize + tileSize / 2f;
            float py = y * tileSize + tileSize / 2f;
            return new Vector2(px, py);
          }
        }
      }
    }
    return null;
  }

  private Vector2 getSafeSpawnPosition(Vector2 pos, Level level) {
    if (isFloorTile(pos, level)) return pos;

    int searchRadius = 3;

    if (level == null || level.getMap() == null) return pos;
    TiledMapTileLayer layer = (TiledMapTileLayer) level.getMap().getLayers().get(0);
    float tileSize = layer.getTileWidth();
    int cx = (int) (pos.x / tileSize);
    int cy = (int) (pos.y / tileSize);

    for (int r = 1; r <= searchRadius; r++) {
      for (int y = cy - r; y <= cy + r; y++) {
        for (int x = cx - r; x <= cx + r; x++) {
          Vector2 candidate =
              new Vector2(x * tileSize + tileSize / 2f, y * tileSize + tileSize / 2f);
          if (isFloorTile(candidate, level)) {
            Gdx.app.log("SafeSpawn", "Moved player from " + pos + " to " + candidate);
            return candidate;
          }
        }
      }
    }

    return pos;
  }

  private boolean isFloorTile(Vector2 pos, Level level) {
    if (level == null || level.getMap() == null) return true;
    TiledMapTileLayer layer = (TiledMapTileLayer) level.getMap().getLayers().get(0);
    if (layer == null) return true;

    int x = (int) (pos.x / layer.getTileWidth());
    int y = (int) (pos.y / layer.getTileHeight());

    if (x < 0 || x >= layer.getWidth() || y < 0 || y >= layer.getHeight()) return false;

    TiledMapTileLayer.Cell cell = layer.getCell(x, y);
    if (cell == null || cell.getTile() == null) return false;

    var props = cell.getTile().getProperties();
    if (props.containsKey("type")) {
      return "floor".equals(props.get("type", String.class));
    }
    return !props.get("isWall", false, Boolean.class);
  }

  public List<Player> getPlayers() {
    return players;
  }

  public Player getPlayer() {
    return players.isEmpty() ? null : players.get(0);
  }

  public void clearPlayers() {
    players.clear();
    GameManager.getInstance().clearPlayers();
  }
}
