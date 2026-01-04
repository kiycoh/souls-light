package io.github.soulslight.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.badlogic.gdx.backends.headless.HeadlessNativesLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MovementTest {

  @BeforeAll
  public static void setUp() {
    HeadlessNativesLoader.load();
  }

  @Test
  public void testMovement() {
    Player player =
        new Player(Player.PlayerClass.WARRIOR, new World(new Vector2(0, 0), true), 0, 0);

    // Test initial position
    assertEquals(new Vector2(0, 0), player.getPosition(), "Initial position should be (0,0)");

    // Test movement
    player.move(10, 5);
    assertEquals(
        new Vector2(10, 5),
        player.getBody().getLinearVelocity(),
        "Velocity should update after move");
  }
}
