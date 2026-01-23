package io.github.soulslight.model;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.model.combat.MageAttack;
import io.github.soulslight.model.combat.WarriorAttack;
import io.github.soulslight.model.enemies.*;
import io.github.soulslight.model.entities.Player;
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
  @ValueSource(strings = { "Chaser", "Ranger", "SpikedBall", "Shielder", "Oblivion" })
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
  public void testChaserAttackAndRetreat() {
    Chaser chaser = (Chaser) EnemyRegistry.getEnemy("Chaser");
    chaser.createBody(world, 0, 0);

    Player player = new Player(Player.PlayerClass.WARRIOR, world, 20, 0);
    List<Player> players = Collections.singletonList(player);

    // Il chaser dovrebbe entrare in stato di attacco
    chaser.updateBehavior(players, 0.1f);
    world.step(0.1f, 6, 2);
    chaser.update(0.1f);

    // Verifica che la velocità sia 0
    assertEquals(
        0,
        chaser.getBody().getLinearVelocity().len(),
        0.1f,
        "Il Chaser deve fermarsi per attaccare");

    chaser.attack(players);

    chaser.updateBehavior(players, 0.1f);

    chaser.updateBehavior(players, 0.1f);
    world.step(0.1f, 6, 2);

    // Verifica che si sia ritirato post attacco
    assertTrue(
        chaser.getBody().getLinearVelocity().x < 0,
        "Il Chaser deve ritirarsi (andare a sinistra) dopo l'attacco");
  }

  @Test
  public void testChaserMemorySearch() {
    Chaser chaser = new Chaser();
    chaser.createBody(world, 0, 0);

    Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 0);
    List<Player> players = Collections.singletonList(player);

    // Vede il player ed entra in stato di attacco
    chaser.updateBehavior(players, 0.1f);

    // Ora il player è lontanissimo e il chaser non lo vede più
    player.setPosition(1000, 1000);
    player.update(0);

    // Poichè non lo vede dovrebbe entrare nella fase di ricerca
    chaser.updateBehavior(players, 0.1f);
    world.step(0.1f, 6, 2);

    // Verifica che si muova verso destra (dove era il player prima)
    assertTrue(
        chaser.getBody().getLinearVelocity().x > 0,
        "Il Chaser deve andare verso l'ultima posizione nota del player");
    assertEquals(
        100f, chaser.getLastKnownPlayerPos().x, 0.1f, "Deve aver memorizzato la X del player");
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

  // --- Helper Class ---
  private static class TestProjectileListener implements io.github.soulslight.model.combat.ProjectileListener {
    public int callCount = 0;
    public List<Vector2> targets = new ArrayList<>();

    @Override
    public void onProjectileRequest(Vector2 origin, Vector2 target, String type) {
      callCount++;
      targets.add(target);
    }
  }

  @Test
  public void testRangerShootingLogic() {
    Ranger ranger = (Ranger) EnemyRegistry.getEnemy("Ranger");
    ranger.createBody(world, 0, 0);

    Player player = new Player(Player.PlayerClass.ARCHER, world, 300, 0);
    List<Player> players = Collections.singletonList(player);

    TestProjectileListener listener = new TestProjectileListener();
    ranger.addProjectileListener(listener);

    ranger.updateBehavior(players, 0.1f);

    assertTrue(listener.callCount > 0, "Il Ranger deve aver notificato il listener per sparare");
  }

  @Test
  public void testSpikedBallChargeBehavior() {
    SpikedBall ball = (SpikedBall) EnemyRegistry.getEnemy("SpikedBall");
    ball.createBody(world, 0, 0);

    Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 0);
    List<Player> players = Collections.singletonList(player);

    ball.updateBehavior(players, 2.0f);

    float delta = 0.1f;
    ball.updateBehavior(players, delta);
    world.step(delta, 6, 2);
    ball.update(delta);

    assertTrue(ball.getX() > 0, "La palla deve muoversi");

    ball.updateBehavior(players, 4.0f);

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

    ball.updateBehavior(players, 2.0f);

    float delta = 0.1f;
    ball.updateBehavior(players, delta);
    world.step(delta, 6, 2);
    ball.update(delta);

    assertTrue(ball.getX() > 0);
    float positionBeforeHit = ball.getX();

    ball.onWallHit(new Vector2(-1, 0));

    ball.updateBehavior(players, 0.1f);
    world.step(0.1f, 6, 2);
    ball.update(0.1f);

    assertTrue(
        ball.getX() < positionBeforeHit,
        "La palla deve rimbalzare indietro (X diminuire) dopo l'urto");
  }

  @Test
  public void testSpikedBallPlayerCollision() {
    SpikedBall ball = (SpikedBall) EnemyRegistry.getEnemy("SpikedBall");
    ball.createBody(world, 0, 0);

    Player player = new Player(Player.PlayerClass.WARRIOR, world, 30, 0);
    List<Player> players = Collections.singletonList(player);

    ball.updateBehavior(players, 2.0f);
    ball.updateBehavior(players, 0.1f);
    world.step(0.1f, 6, 2);
    ball.updateBehavior(players, 0.1f);

    assertTrue(
        ball.getBody().getLinearVelocity().x < 0,
        "La palla deve rimbalzare indietro dopo aver colpito il player");
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
  public void testShielderInterceptionPosition() {
    Shielder shielder = new Shielder();
    shielder.createBody(world, 50, 50);

    Player player = new Player(Player.PlayerClass.WARRIOR, world, 0, 0);

    Ranger ally = new Ranger();
    ally.setHealth(100);
    ally.createBody(world, 100, 0);

    shielder.setAllies(Collections.singletonList(ally));

    shielder.updateBehavior(Collections.singletonList(player), 0.1f);

    world.step(0.1f, 6, 2);
    shielder.update(0.1f);

    assertTrue(shielder.getY() < 50, "Shielder deve scendere per mettersi in linea");
    assertTrue(shielder.getX() > 50, "Shielder deve avanzare verso il punto di protezione (60)");
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

    float distanceX = Math.abs(oblivion.getX() - player.getX());
    assertEquals(120f, distanceX, 1.0f);
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

  @Test
  public void testOblivionTripleShotGeneration() {
    Oblivion oblivion = (Oblivion) EnemyRegistry.getEnemy("Oblivion");
    oblivion.createBody(world, 0, 0);

    Player player = new Player(Player.PlayerClass.WARRIOR, world, 400, 0);
    List<Player> players = Collections.singletonList(player);

    oblivion.takeDamage(oblivion.getHealth() + 100);
    oblivion.updateBehavior(players, 0.1f);

    TestProjectileListener listener = new TestProjectileListener();
    oblivion.addProjectileListener(listener);

    oblivion.updateBehavior(players, 0.1f);
    oblivion.updateBehavior(players, 0.6f);

    assertEquals(3, listener.callCount, "Oblivion deve generare esattamente 3 eventi projectile");

    Vector2 centerShot = listener.targets.get(0);
    assertTrue(centerShot.x > 0, "Il colpo centrale deve andare verso il player");
  }

  @Test
  public void testOblivionDealsDamage() {
    Oblivion oblivion = new Oblivion();
    oblivion.createBody(world, 0, 0);

    Player player = new Player(Player.PlayerClass.WARRIOR, world, 30, 0);
    float initialHp = player.getHealth();

    oblivion.updateBehavior(Collections.singletonList(player), 0.1f);
    oblivion.updateBehavior(Collections.singletonList(player), 0.5f);

    assertTrue(
        player.getHealth() < initialHp, "Il player deve aver subito danno dallo smash di Oblivion");
    assertEquals(initialHp - 40f, player.getHealth(), 0.1f, "Il danno deve essere esattamente 40");
  }
}
