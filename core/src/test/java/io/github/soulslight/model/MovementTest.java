package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MovementTest {

    @Test
    public void testMovement() {
        Player player = new Player(new WarriorAttack());

        // Test initial position
        assertEquals(new Vector2(0, 0), player.getPosition(), "Initial position should be (0,0)");

        // Test movement
        player.move(10, 5);
        assertEquals(new Vector2(10, 5), player.getPosition(), "Position should update after move");
    }
}

