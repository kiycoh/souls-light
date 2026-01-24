package io.github.soulslight.model.map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.enemies.EnemyFactory;
import io.github.soulslight.model.enemies.Oblivion;
import io.github.soulslight.model.enemies.ai.RoomIdleState;
import io.github.soulslight.model.room.Door;
import io.github.soulslight.model.room.DoorPosition;
import io.github.soulslight.model.room.PortalRoom;
import io.github.soulslight.model.room.Room;
import io.github.soulslight.model.room.RoomData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LevelBuilder {
  private Level level;

  public LevelBuilder() {
    this.level = new Level();
  }

  public LevelBuilder buildMap(TiledMap map) {
    level.setMap(map);
    return this;
  }

  // --- 1A. Enemy generation from tmx file (legacy method from InGameEnemies
  // branch) ---
  public LevelBuilder spawnFromTiled(EnemyFactory factory, World world) {

    // Boss room size calculations
    MapProperties prop = level.getMap().getProperties();
    int mapW = prop.get("width", Integer.class);
    int mapH = prop.get("height", Integer.class);
    int tileW = prop.get("tilewidth", Integer.class);
    int tileH = prop.get("tileheight", Integer.class);

    float totalMapWidth = mapW * tileW;
    float totalMapHeight = mapH * tileH;

    MapLayer layer = level.getMap().getLayers().get("Livello di oggetti 1");

    if (layer == null) {
      System.out.println("WARNING: Layer 'Livello di oggetti 1' non trovato!");
      return this;
    }

    for (MapObject object : layer.getObjects()) {
      float x = 0;
      float y = 0;

      if (object instanceof RectangleMapObject) {
        Rectangle rect = ((RectangleMapObject) object).getRectangle();
        x = rect.x;
        y = rect.y;
      } else {
        x = object.getProperties().get("x", Float.class);
        y = object.getProperties().get("y", Float.class);
      }

      String type = object.getProperties().get("enemyType", "melee", String.class);
      AbstractEnemy enemy = null;

      switch (type.toLowerCase()) {
        case "ranger":
        case "archer":
          enemy = factory.createRanged();
          break;
        case "tank":
        case "shielder":
          enemy = factory.createTank();
          break;
        case "ball":
        case "trap":
          enemy = factory.createBall();
          break;
        case "boss":
        case "oblivion":
          enemy = factory.createBoss();
          break;
        case "melee":
        case "chaser":
        default:
          enemy = factory.createMelee();
          break;
      }

      if (enemy != null) {
        enemy.createBody(world, x, y);
        enemy.setSpawnPoint(x, y);

        if (enemy instanceof Oblivion) {
          ((Oblivion) enemy).setMapBounds(totalMapWidth, totalMapHeight);
        }
        level.addEnemy(enemy);
      }
    }
    return this;
  }

  // --- 1B. Random enemy generation on generated map ---
  public LevelBuilder spawnRandom(
      EnemyFactory factory,
      World world,
      int meleeCount,
      int rangedCount,
      int tankCount,
      int ballCount,
      boolean spawnBoss) {
    TiledMap map = level.getMap();
    if (map == null || map.getLayers().getCount() == 0) {
      System.out.println("WARNING: Map is null or has no layers. Cannot spawnRandom.");
      return this;
    }

    MapProperties prop = map.getProperties();
    int mapW = prop.get("width", Integer.class);
    int mapH = prop.get("height", Integer.class);
    int tileW = prop.get("tilewidth", Integer.class);
    int tileH = prop.get("tileheight", Integer.class);

    float totalMapWidth = mapW * tileW;
    float totalMapHeight = mapH * tileH;

    TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
    float tileSize = layer.getTileWidth();

    // Collects all "floor" type tiles
    List<Vector2> spawnPoints = new ArrayList<>();
    for (int x = 0; x < layer.getWidth(); x++) {
      for (int y = 0; y < layer.getHeight(); y++) {
        if (isFloor(layer, x, y)) {
          float px = x * tileSize + tileSize / 2f;
          float py = y * tileSize + tileSize / 2f;
          spawnPoints.add(new Vector2(px, py));
        }
      }
    }

    if (spawnPoints.isEmpty()) {
      System.out.println("WARNING: No floor tiles found for random spawn.");
      return this;
    }

    // Shuffles spawn points
    Collections.shuffle(spawnPoints);

    int index = 0;

    // --- MELEE ---
    for (int i = 0; i < meleeCount && index < spawnPoints.size(); i++) {
      AbstractEnemy e = factory.createMelee();
      spawnEnemy(e, spawnPoints.get(index++), world, totalMapWidth, totalMapHeight);
    }

    // --- RANGED ---
    for (int i = 0; i < rangedCount && index < spawnPoints.size(); i++) {
      AbstractEnemy e = factory.createRanged();
      spawnEnemy(e, spawnPoints.get(index++), world, totalMapWidth, totalMapHeight);
    }

    // --- TANK ---
    for (int i = 0; i < tankCount && index < spawnPoints.size(); i++) {
      AbstractEnemy e = factory.createTank();
      spawnEnemy(e, spawnPoints.get(index++), world, totalMapWidth, totalMapHeight);
    }

    // --- BALL ---
    for (int i = 0; i < ballCount && index < spawnPoints.size(); i++) {
      AbstractEnemy e = factory.createBall();
      spawnEnemy(e, spawnPoints.get(index++), world, totalMapWidth, totalMapHeight);
    }

    // --- BOSS ---
    if (spawnBoss && index < spawnPoints.size()) {
      AbstractEnemy boss = factory.createBoss();
      spawnEnemy(boss, spawnPoints.get(index), world, totalMapWidth, totalMapHeight);
    }

    return this;
  }

  // Centralized helper for single spawn
  private void spawnEnemy(
      AbstractEnemy enemy, Vector2 pos, World world, float totalMapWidth, float totalMapHeight) {
    if (enemy == null) return;

    enemy.createBody(world, pos.x, pos.y);
    enemy.setSpawnPoint(pos.x, pos.y);

    if (enemy instanceof Oblivion) {
      ((Oblivion) enemy).setMapBounds(totalMapWidth, totalMapHeight);
    }

    level.addEnemy(enemy);

    // Assign enemy to containing room for lock/unlock mechanics
    Room containingRoom = level.getRoomManager().findRoomContaining(pos);
    if (containingRoom != null) {
      containingRoom.addEnemy(enemy);
    }
  }

  // --- 1C. Room-based enemy spawning (4-7 enemies per room) ---
  /**
   * Spawns enemies in each room. Each room gets 4-7 random enemies. Enemies start in RoomIdleState
   * and activate when player enters.
   *
   * @param factory Enemy factory for creating enemies
   * @param world Box2D physics world
   * @return this builder for chaining
   */
  public LevelBuilder spawnEnemiesInRooms(EnemyFactory factory, World world) {
    TiledMap map = level.getMap();
    if (map == null || map.getLayers().getCount() == 0) {
      System.out.println("WARNING: Map is null. Cannot spawnEnemiesInRooms.");
      return this;
    }

    MapProperties prop = map.getProperties();
    int tileW = prop.get("tilewidth", Integer.class);
    int tileH = prop.get("tileheight", Integer.class);
    float totalMapWidth = prop.get("width", Integer.class) * tileW;
    float totalMapHeight = prop.get("height", Integer.class) * tileH;

    TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
    Random rng = new Random();

    for (Room room : level.getRoomManager().getRooms()) {
      // Skip portal rooms (last room = no enemies, just portal)
      if (room instanceof PortalRoom) {
        continue;
      }

      // Spawn 4-7 enemies per room
      int enemyCount = 4 + rng.nextInt(4); // 4 to 7 inclusive

      // Find floor spawn points within this room
      List<Vector2> roomSpawns = new ArrayList<>();
      com.badlogic.gdx.math.Rectangle bounds = room.getBounds();
      float tileSize = layer.getTileWidth();

      int startX = (int) (bounds.x / tileSize);
      int startY = (int) (bounds.y / tileSize);
      int endX = (int) ((bounds.x + bounds.width) / tileSize);
      int endY = (int) ((bounds.y + bounds.height) / tileSize);

      for (int tx = startX; tx < endX; tx++) {
        for (int ty = startY; ty < endY; ty++) {
          if (isFloor(layer, tx, ty)) {
            float px = tx * tileSize + tileSize / 2f;
            float py = ty * tileSize + tileSize / 2f;
            roomSpawns.add(new Vector2(px, py));
          }
        }
      }

      if (roomSpawns.isEmpty()) {
        System.out.println("WARNING: No floor tiles in room " + room.getId());
        continue;
      }

      Collections.shuffle(roomSpawns, rng);

      for (int i = 0; i < enemyCount && i < roomSpawns.size(); i++) {
        // Create random enemy type
        AbstractEnemy enemy =
            switch (rng.nextInt(4)) {
              case 0 -> factory.createMelee();
              case 1 -> factory.createRanged();
              case 2 -> factory.createTank();
              default -> factory.createMelee();
            };

        Vector2 pos = roomSpawns.get(i);
        spawnEnemyInRoom(enemy, pos, world, room, totalMapWidth, totalMapHeight);
      }
    }
    return this;
  }

  // Helper for spawning enemy in a specific room with RoomIdleState
  private void spawnEnemyInRoom(
      AbstractEnemy enemy,
      Vector2 pos,
      World world,
      Room room,
      float totalMapWidth,
      float totalMapHeight) {
    if (enemy == null) return;

    enemy.createBody(world, pos.x, pos.y);
    enemy.setSpawnPoint(pos.x, pos.y);
    enemy.setAIState(new RoomIdleState()); // Start idle

    if (enemy instanceof Oblivion) {
      ((Oblivion) enemy).setMapBounds(totalMapWidth, totalMapHeight);
    }

    level.addEnemy(enemy);
    room.addEnemy(enemy);
  }

  // --- 2. Wall physics generation ---
  public LevelBuilder buildPhysicsFromMap(World world) {
    createCollisionFromProperties(world);
    return this;
  }

  private void createCollisionFromProperties(World world) {
    TiledMapTileLayer layer = (TiledMapTileLayer) level.getMap().getLayers().get(0);
    float tileSize = layer.getTileWidth();

    for (int x = 0; x < layer.getWidth(); x++) {
      for (int y = 0; y < layer.getHeight(); y++) {
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);

        if (cell != null && cell.getTile() != null) {

          boolean isWall = false;

          // in case of tmx file: boolean "isWall"
          if (cell.getTile().getProperties().containsKey("isWall")) {
            isWall = cell.getTile().getProperties().get("isWall", false, Boolean.class);
          }
          // in case of generated maps: "type" = "wall" property
          else if (cell.getTile().getProperties().containsKey("type")) {
            isWall = "wall".equals(cell.getTile().getProperties().get("type", String.class));
          }

          if (isWall) {
            createWallBody(world, x * tileSize, y * tileSize, tileSize);
          }
        }
      }
    }
  }

  private void createWallBody(World world, float x, float y, float size) {
    BodyDef bdef = new BodyDef();
    bdef.position.set(x + size / 2, y + size / 2);
    bdef.type = BodyDef.BodyType.StaticBody;

    Body body = world.createBody(bdef);
    PolygonShape shape = new PolygonShape();
    shape.setAsBox(size / 2, size / 2);

    FixtureDef fdef = new FixtureDef();
    fdef.shape = shape;
    fdef.friction = 0f;

    body.createFixture(fdef);
    shape.dispose();
  }

  // --- 3. ambient settings ---
  public LevelBuilder setEnvironment(String musicTrack, float lightLevel) {
    level.setMusicTrack(musicTrack);
    level.setAmbientLight(lightLevel);
    return this;
  }

  // --- 4. Room building from generation data ---
  /**
   * Builds Room objects from room metadata.
   *
   * @param roomDataList List of room metadata from map generation
   * @return this builder for chaining
   */
  public LevelBuilder buildRooms(List<RoomData> roomDataList) {
    if (roomDataList == null) return this;

    for (RoomData data : roomDataList) {
      Room room;
      if (data.isPortalRoom()) {
        room =
            new PortalRoom(
                data.id(),
                data.bounds().x,
                data.bounds().y,
                data.bounds().width,
                data.bounds().height);
      } else {
        room =
            new Room(
                data.id(),
                data.bounds().x,
                data.bounds().y,
                data.bounds().width,
                data.bounds().height);
      }

      // Create doors at actual corridor connection points (from map generation)
      float doorThickness = 32f; // Corridor width blocked
      float doorLength = 128f; // Door spans corridor opening (corridor_width + 1 tile)

      for (DoorPosition doorPos : data.doorPositions()) {
        Door.Direction doorDir =
            switch (doorPos.direction()) {
              case NORTH -> Door.Direction.NORTH;
              case SOUTH -> Door.Direction.SOUTH;
              case EAST -> Door.Direction.EAST;
              case WEST -> Door.Direction.WEST;
            };

        // Use appropriate dimensions based on direction
        float w =
            (doorDir == Door.Direction.NORTH || doorDir == Door.Direction.SOUTH)
                ? doorLength
                : doorThickness;
        float h =
            (doorDir == Door.Direction.NORTH || doorDir == Door.Direction.SOUTH)
                ? doorThickness
                : doorLength;

        room.addDoor(new Door(doorPos.position().x, doorPos.position().y, doorDir, w, h));
      }

      level.getRoomManager().addRoom(room);
    }
    return this;
  }

  /**
   * Initializes the room manager with the physics world. Creates sensors and initializes doors for
   * all rooms.
   *
   * @param world The Box2D physics world
   * @return this builder for chaining
   */
  public LevelBuilder initializeRoomManager(World world) {
    level.getRoomManager().initialize(world);
    level.getRoomManager().initializeDoors();
    return this;
  }

  public Level build() {
    return level;
  }

  // Helper for "floor" tiles
  private boolean isFloor(TiledMapTileLayer layer, int x, int y) {
    if (layer.getCell(x, y) == null || layer.getCell(x, y).getTile() == null) {
      return false;
    }

    var props = layer.getCell(x, y).getTile().getProperties();

    if (props.containsKey("type")) {
      return "floor".equals(props.get("type", String.class));
    }

    // legacy maps fallback: if it's not marked as wall, is considered as a floor
    // tile
    return !props.get("isWall", false, Boolean.class);
  }
}
