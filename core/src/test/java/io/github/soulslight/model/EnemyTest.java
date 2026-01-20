package io.github.soulslight.model;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.utils.GdxTestExtension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(GdxTestExtension.class)
public class EnemyTest {

  private World world;

  @BeforeEach
  public void setup() {
    Box2D.init();
    world = new World(new Vector2(0, 0), true);
    EnemyRegistry.loadCache(null);
  }

  @ParameterizedTest
  @ValueSource(strings = {"Chaser", "Ranger", "SpikedBall", "Shielder", "Oblivion"})
  public void testCloneIndependenceForAll(String enemyType) {
    AbstractEnemy original = EnemyRegistry.getEnemy(enemyType);
    AbstractEnemy clone = EnemyRegistry.getEnemy(enemyType);

    original.createBody(world, 0, 0);
    clone.createBody(world, 100, 100);

    assertNotNull(original);
    assertNotNull(clone);
    assertNotSame(original, clone);

    original.setPosition(500, 500);
    assertEquals(100, clone.getX(), 0.1f);

    original.setHealth(1);
    assertNotEquals(1, clone.getHealth());
  }

  @Test
  public void testChaserBehaviour() {
    AbstractEnemy chaser = EnemyRegistry.getEnemy("Chaser");
    chaser.createBody(world, 0, 0);

    Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 0);
    List<Player> players = Collections.singletonList(player);

    float delta = 1.0f;
    chaser.updateBehavior(players, delta);
    world.step(delta, 6, 2);
    chaser.update(delta);

    assertTrue(chaser.getX() > 0, "Il Chaser deve essersi mosso a destra");
  }

  @Test
  public void testRangerBehaviour() {
    AbstractEnemy ranger = EnemyRegistry.getEnemy("Ranger");
    ranger.createBody(world, 10, 10);

    Player player = new Player(Player.PlayerClass.ARCHER, world, 10, 5);
    List<Player> players = Collections.singletonList(player);

    float initialDistance = ranger.getPosition().dst(player.getPosition());

    float delta = 1.0f;
    ranger.updateBehavior(players, delta);
    world.step(delta, 6, 2);
    ranger.update(delta);

    float newDistance = ranger.getPosition().dst(player.getPosition());

    assertTrue(newDistance > initialDistance, "Il Ranger deve scappare");
    assertTrue(ranger.getY() > 10, "Il Ranger deve salire");
  }

  @Test
  public void testSpikedBallChargeBehavior() {
    SpikedBall ball = (SpikedBall) EnemyRegistry.getEnemy("SpikedBall");
    ball.createBody(world, 0, 0);

    Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 0);
    List<Player> players = Collections.singletonList(player);

    // 1. Consuma Cooldown
    ball.updateBehavior(players, 2.0f);

    // 2. Inizio Carica
    float delta = 0.1f;
    ball.updateBehavior(players, delta);
    world.step(delta, 6, 2);
    ball.update(delta);

    assertTrue(ball.getX() > 0, "La palla deve muoversi");

    // 3. Timeout Carica
    ball.updateBehavior(players, 4.0f);

    // Verifica stop
    float stopPosition = ball.getX();
    ball.updateBehavior(players, 0.1f);
    world.step(0.1f, 6, 2);
    ball.update(0.1f);

    assertEquals(stopPosition, ball.getX(), 0.1f, "La palla deve fermarsi dopo il timeout");
  }

  @Test
  public void testSpikedBallWallCollision() {
    SpikedBall ball = (SpikedBall) EnemyRegistry.getEnemy("SpikedBall");
    ball.createBody(world, 0, 0);

    Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 0);
    List<Player> players = Collections.singletonList(player);

    // Start Carica
    ball.updateBehavior(players, 2.0f);

    float delta = 0.1f;
    ball.updateBehavior(players, delta);
    world.step(delta, 6, 2);
    ball.update(delta);

    // La palla si è mossa a destra
    assertTrue(ball.getX() > 0);
    float positionBeforeHit = ball.getX();

    // FIX: Simuliamo un muro verticale a DESTRA.
    // La normale punta verso SINISTRA (-1, 0)
    ball.onWallHit(new Vector2(-1, 0));

    // Step successivo
    ball.updateBehavior(players, 0.1f);
    world.step(0.1f, 6, 2);
    ball.update(0.1f);

    // FIX LOGICA TEST: Ora la palla RIMBALZA, quindi torna indietro.
    // La X deve essere MINORE della posizione prima dell'urto.
    assertTrue(
        ball.getX() < positionBeforeHit,
        "La palla deve rimbalzare indietro (X diminuire) dopo l'urto");
  }

  @Test
  public void testShielderProtectionMovement() {
    Ranger ally = new Ranger();
    ally.setHealth(100);
    ally.createBody(world, 0, 0);

    Player player = new Player(Player.PlayerClass.WARRIOR, world, 1000, 1000);

    Shielder shielder = new Shielder();
    shielder.setHealth(300);
    shielder.createBody(world, 50, 50);

    List<AbstractEnemy> allies = new ArrayList<>();
    allies.add(ally);
    allies.add(shielder);
    shielder.setAllies(allies);

    // Usiamo un delta piccolo per simulare un frame fisico realistico
    float delta = 0.1f;

    shielder.updateBehavior(Collections.singletonList(player), delta);
    world.step(delta, 6, 2);
    shielder.update(delta);

    assertTrue(shielder.getX() < 50, "Shielder (X=50) deve muoversi verso alleato (X=0)");
    assertTrue(shielder.getY() < 50, "Shielder (Y=50) deve muoversi verso alleato (Y=0)");
  }

  @Test
  public void testShielderDieIfAlone() {
    Shielder shielder = new Shielder();
    shielder.setHealth(300);
    List<AbstractEnemy> allies = new ArrayList<>();
    allies.add(shielder);
    shielder.setAllies(allies);
    Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 100);

    shielder.updateBehavior(Collections.singletonList(player), 1.0f);
    assertTrue(shielder.getHealth() <= 0);
  }

  @Test
  public void testShielderAttackDecision() {
    Shielder shielder = new Shielder();
    shielder.createBody(world, 0, 0);

    Player player = new Player(Player.PlayerClass.WARRIOR, world, 10, 0);
    Chaser ally = new Chaser();
    ally.setHealth(100);
    List<AbstractEnemy> allies = new ArrayList<>();
    allies.add(shielder);
    allies.add(ally);
    shielder.setAllies(allies);

    assertDoesNotThrow(() -> shielder.updateBehavior(Collections.singletonList(player), 0.1f));
    float dist = shielder.getPosition().dst(player.getPosition());
    assertTrue(dist <= shielder.getAttackStrategy().getRange());
  }

  @Test
  public void testOblivionTeleport() {
    Oblivion oblivion = (Oblivion) EnemyRegistry.getEnemy("Oblivion");
    oblivion.createBody(world, 0, 0);

    Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 100);
    player.setPosition(1000, 500);
    List<Player> players = Collections.singletonList(player);

    oblivion.updateBehavior(players, 4.9f);
    oblivion.updateBehavior(players, 0.2f);
    oblivion.update(0.2f);

    assertEquals(500, oblivion.getY(), 1.0f);
    // Note: Teleport logic clamps to map bounds. Tests might fail if map bounds are 0.
    // Oblivion constructor init map bounds to 0.
    // But teleport logic has: if (mapWidthBoundary > 0 && mapHeightBoundary > 0) ...
    // If 0, it doesn't clamp.
    // New X = 1000 +/- 120 (1120 or 880).
    float distanceX = Math.abs(oblivion.getX() - player.getX());
    // Since direction is random, we check distance.
    assertEquals(120f, distanceX, 1.0f); // Changed 150 to 120 (TELEPORT_OFFSET)
  }

  @Test
  public void testOblivionPhaseTransition() {
    Oblivion oblivion = (Oblivion) EnemyRegistry.getEnemy("Oblivion");
    oblivion.createBody(world, 0, 0);
    Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 100);
    List<Player> players = Collections.singletonList(player);

    float initialHP = oblivion.getHealth();
    oblivion.takeDamage(initialHP + 50);
    oblivion.updateBehavior(players, 0.1f);

    assertTrue(oblivion.isPhaseTwo());
    assertFalse(oblivion.isDead());
  }

  @Test
  public void testOblivionPhaseTwoStrategySwitch() {
    Oblivion oblivion = (Oblivion) EnemyRegistry.getEnemy("Oblivion");
    oblivion.createBody(world, 0, 0);
    Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 100);
    List<Player> players = Collections.singletonList(player);

    oblivion.takeDamage(oblivion.getHealth() + 100);
    oblivion.updateBehavior(players, 0.1f);

    oblivion.getBody().setTransform(0, 0, 0);
    player.getBody().setTransform(400, 0, 0);
    player.update(0);

    oblivion.updateBehavior(players, 0.1f);
    assertTrue(oblivion.getAttackStrategy() instanceof MageAttack);

    player.getBody().setTransform(20, 0, 0);
    player.update(0);

    oblivion.updateBehavior(players, 0.1f);
    assertTrue(oblivion.getAttackStrategy() instanceof WarriorAttack);
  }
}