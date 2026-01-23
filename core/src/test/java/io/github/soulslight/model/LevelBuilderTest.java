package io.github.soulslight.model.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.manager.ResourceManager;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.enemies.Chaser;
import io.github.soulslight.model.enemies.DungeonEnemyFactory;
import io.github.soulslight.model.enemies.EnemyFactory;
import io.github.soulslight.model.enemies.EnemyRegistry;
import io.github.soulslight.model.enemies.Oblivion;
import io.github.soulslight.model.enemies.Ranger;
import io.github.soulslight.model.enemies.Shielder;
import io.github.soulslight.model.enemies.SpikedBall;
import io.github.soulslight.utils.GdxTestExtension;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxTestExtension.class)
class LevelBuilderTest {

  private World world;

  @BeforeEach
  void setup() {
    Box2D.init();
    world = new World(new Vector2(0, 0), true);
    EnemyRegistry.loadCache(null);
  }

  @Test
  void testAddEnemies() {
    int mapW = 10;
    int mapH = 10;
    int tileSize = 32;
    TiledMap map = createAllFloorMap(mapW, mapH, tileSize);

    EnemyFactory factory = new DungeonEnemyFactory();

    int melee = 3;
    int ranged = 2;
    int tank = 1;
    int ball = 1;
    boolean boss = true;
    int expectedTotal = melee + ranged + tank + ball + 1;

    Level level =
        new LevelBuilder()
            .buildMap(map)
            .spawnRandom(factory, world, melee, ranged, tank, ball, boss)
            .build();

    List<AbstractEnemy> enemies = level.getEnemies();
    assertNotNull(enemies);
    assertEquals(expectedTotal, enemies.size(), "Numero nemici spawnati errato");

    long chasers = enemies.stream().filter(e -> e instanceof Chaser).count();
    long rangers = enemies.stream().filter(e -> e instanceof Ranger).count();
    long shielders = enemies.stream().filter(e -> e instanceof Shielder).count();
    long balls = enemies.stream().filter(e -> e instanceof SpikedBall).count();
    long bosses = enemies.stream().filter(e -> e instanceof Oblivion).count();

    assertEquals(melee, chasers);
    assertEquals(ranged, rangers);
    assertEquals(tank, shielders);
    assertEquals(ball, balls);
    assertEquals(1, bosses);

    float maxX = mapW * tileSize;
    float maxY = mapH * tileSize;

    Set<String> usedSpawns = new HashSet<>();
    for (AbstractEnemy e : enemies) {
      assertNotNull(e.getBody(), "Ogni nemico deve avere un corpo Box2D");
      assertNotNull(e.getSpawnPoint(), "Ogni nemico deve avere uno spawnPoint");

      float bx = e.getBody().getPosition().x;
      float by = e.getBody().getPosition().y;

      assertEquals(e.getSpawnPoint().x, bx, 0.001f, "Body e spawnPoint devono combaciare");
      assertEquals(e.getSpawnPoint().y, by, 0.001f, "Body e spawnPoint devono combaciare");

      assertTrue(bx >= 0 && bx <= maxX, "Spawn fuori dai limiti mappa");
      assertTrue(by >= 0 && by <= maxY, "Spawn fuori dai limiti mappa");

      usedSpawns.add(String.format("%.3f,%.3f", bx, by));
    }

    assertEquals(
        enemies.size(), usedSpawns.size(), "Due nemici sono stati spawnati nello stesso punto");
  }

  private TiledMap createAllFloorMap(int width, int height, int tileSize) {
    TiledMap map = new TiledMap();
    map.getProperties().put("width", width);
    map.getProperties().put("height", height);
    map.getProperties().put("tilewidth", tileSize);
    map.getProperties().put("tileheight", tileSize);

    TiledMapTileLayer layer = new TiledMapTileLayer(width, height, tileSize, tileSize);

    StaticTiledMapTile floorTile =
        new StaticTiledMapTile(ResourceManager.getInstance().getFloorTextureRegion());
    floorTile.getProperties().put("type", "floor");

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(floorTile);
        layer.setCell(x, y, cell);
      }
    }

    map.getLayers().add(layer);
    return map;
  }
}
