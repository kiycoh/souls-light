package io.github.soulslight.model;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LevelBuilder {
  private Level level;

  public LevelBuilder() {
    this.level = new Level();
  }

  public LevelBuilder buildMap(TiledMap map) {
    level.setMap(map);
    return this;
  }

  // --- 1A. Enemy generation from tmx file (legacy method from InGameEnemies branch) ---
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

    // legacy maps fallback: if it's not marked as wall, is considered as a floor tile
    return !props.get("isWall", false, Boolean.class);
  }
}
