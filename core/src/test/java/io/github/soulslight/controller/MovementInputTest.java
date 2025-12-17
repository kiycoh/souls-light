package io.github.soulslight.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

public class MovementInputTest {

    @Test
    public void testAddMovementListener() {
        MovementInput movementInput = new MovementInput();
        MovementInput.MovementListener listener = Mockito.mock(MovementInput.MovementListener.class);

        movementInput.addMovementListener(listener);

        assertTrue(movementInput.getListeners().contains(listener));
    }
}

