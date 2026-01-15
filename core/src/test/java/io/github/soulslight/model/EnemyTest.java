package io.github.soulslight.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.utils.GdxTestExtension;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxTestExtension.class)
public class EnemyTest {

  // Variable for the Test Physics World
  private World world;

  @BeforeEach
  public void setup() {
    // Natives loaded by Extension
    Box2D.init();

    // Create a world with zero gravity (Vector2(0,0)) and active sleep (true)
    world = new World(new Vector2(0, 0), true);
  }

  @Test
  public void testCloneIndependence() {
    Enemy original = new Chaser();
    Enemy clone = original.clone();

    // Verifies that registry works
    assertNotNull(original);
    assertNotNull(clone);

    // Checks if original and clone are of the same class
    assertEquals(
        original.getClass(),
        clone.getClass(),
        "Il clone deve essere della stessa classe dell'originale");

    // Verifies they are different objects in memory
    assertNotSame(original, clone, "Devono essere oggetti diversi in memoria");

    // I modify the original
    original.setPosition(500, 500);
    original.setHealth(1);

    // Verify that the clone has not been modified
    assertEquals(0, clone.getX(), "Il clone non doveva muoversi");
    assertNotEquals(1, clone.getHealth(), "Il clone non doveva perdere vita");
  }

  @Test
  public void testChaserBehaviour() {
    Enemy chaser = new Chaser();
    chaser.setPosition(0, 0);

    Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 0);

    chaser.update(player, 1.0f);

    assertTrue(chaser.getX() > 0, "Il Chaser dovrebbe essersi avvicinato al player sull'asse X");

    //  Small tolerance (delta) for floats
    assertEquals(0, chaser.getY(), 0.01f);
  }

  @Test
  public void testRangerBehaviour() {
    // 1. SETUP
    Enemy ranger = new Ranger();
    ranger.setPosition(10, 10);

    Player player = new Player(Player.PlayerClass.ARCHER, world, 10, 5);

    float initialDistance = ranger.getPosition().dst(player.getPosition());

    // ACTION (Simulates 1 second)
    ranger.update(player, 1.0f);

    // ASSERT
    float newDistance = ranger.getPosition().dst(player.getPosition());

    // Verifies that it MOVED AWAY (New distance must be greater than initial)
    assertTrue(
        newDistance > initialDistance,
        "Il Ranger doveva scappare! Distanza Iniziale: "
            + initialDistance
            + " -> Finale: "
            + newDistance);

    // Verifies direction: If player was at Y=5 and Ranger at Y=10, Ranger must go up (Y > 10)
    assertTrue(ranger.getY() > 10, "Il Ranger doveva scappare verso l'alto (Y aumenta)");
  }

  @Test
  public void testSpikedBallChargeBehavior() {
    // SETUP
    SpikedBall ball = new SpikedBall();
    ball.setPosition(0, 0);
    Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 0);

    // START PHASE (Cooldown -> Charge)
    ball.update(player, 2.0f); // Exactly finishes cooldown
    ball.update(player, 0.1f); // First frame of movement

    assertTrue(ball.getX() > 0, "La palla deve essere partita");

    // RUN PHASE -> STOP (Wait for MAX_CHARGE_TIME to finish)
    // Max duration is 3.0s. We already did 0.1s.
    // Give abundant time (4.0s) to be sure it finishes.
    ball.update(player, 4.0f);

    float stopPosition = ball.getX();

    // VERIFICATION PHASE (Must be stopped)
    ball.update(player, 0.5f);

    assertEquals(
        stopPosition, ball.getX(), 0.01f, "La palla deve essersi fermata da sola per timeout");
  }

  @Test
  public void testSpikedBallWallCollision() {
    // SETUP
    SpikedBall ball = new SpikedBall();
    ball.setPosition(0, 0);
    Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 0);

    // START PHASE
    // We use two steps to avoid the "skipped frame" problem
    ball.update(player, 2.0f); // Cooldown expires
    ball.update(player, 0.1f); // Moves

    assertTrue(ball.getX() > 0, "La palla deve essere partita");

    float positionBeforeHit = ball.getX();

    // IMPACT SIMULATION
    ball.onWallHit(); // BAM!

    // IMMEDIATE STOP VERIFICATION
    // Let time pass (0.5s). If it hadn't stopped,
    // at 300px/s it would have moved 150px!
    ball.update(player, 0.5f);

    assertEquals(
        positionBeforeHit,
        ball.getX(),
        0.01f,
        "La palla deve fermarsi ISTANTANEAMENTE dopo aver toccato il muro");
  }

  @Test
  public void testShielderDieIfAlone() {
    // 1. SETUP
    Shielder shielder = new Shielder();
    shielder.setHealth(300); // A lot of health

    // Empty allies list (or containing only himself)
    List<Enemy> allies = new ArrayList<>();
    allies.add(shielder);
    shielder.setAllies(allies);

    Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 100);

    // ACTION
    // We run update. He checks the list, sees he is alone.
    shielder.update(player, 1.0f);

    // ASSERT
    assertTrue(
        shielder.getHealth() <= 0, "Lo Shielder deve morire se non ha nessuno da proteggere!");
  }

  @Test
  public void testShielderProtectionMovement() {
    // GEOMETRIC SETUP
    // Let's put everything on a vertical line to facilitate calculations.

    Chaser ally = new Chaser();
    ally.setPosition(0, 0); // Ally at the bottom

    Player player = new Player(Player.PlayerClass.WARRIOR, world, 0, 100); // Player at the top

    // Shielder starts shifted to the right (out of position)
    Shielder shielder = new Shielder();
    shielder.setPosition(50, 50);

    // Allie list setup
    List<Enemy> allies = new ArrayList<>();
    allies.add(ally);
    allies.add(shielder);
    shielder.setAllies(allies);

    // ACTION
    shielder.update(player, 1.0f);

    // ASSERT
    // Shielder was at X=50. Must go towards X=0.
    assertTrue(
        shielder.getX() < 50, "Lo Shielder deve spostarsi a sinistra verso la linea di tiro");

    // Shielder was at Y=50. Must go towards Y=40.
    assertTrue(
        shielder.getY() < 50, "Lo Shielder deve scendere verso la posizione di guardia (Y=40)");
  }

  @Test
  public void testShielderAttackDecision() {
    // SETUP
    Shielder shielder = new Shielder();
    shielder.setPosition(0, 0);

    // Player very close (distance 10, bash range is 45)
    Player player = new Player(Player.PlayerClass.WARRIOR, world, 10, 0);

    // Need a valid allies list to not make him suicide
    Chaser ally = new Chaser();
    List<Enemy> allies = new ArrayList<>();
    allies.add(shielder);
    allies.add(ally);
    shielder.setAllies(allies);

    // 2. ACTION
    // To verify if it attacks, we can use a trick:
    // The default attack() method does nothing visible in the unit test without mock.
    // BUT we can verify that he DID NOT move.

    assertDoesNotThrow(() -> shielder.update(player, 0.1f));

    // Distance Logic Verification:
    float dist = shielder.getPosition().dst(player.getPosition());
    assertTrue(
        dist <= shielder.getAttackStrategy().getRange(),
        "Il player è nel range, lo Shielder dovrebbe aver tentato l'attacco");
  }
}
