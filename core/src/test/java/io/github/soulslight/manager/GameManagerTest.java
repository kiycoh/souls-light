package io.github.soulslight.manager;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class GameManagerTest {

    @Test
    public void testGameManagerUniqueInstance() {
        GameManager instance1 = GameManager.getInstance();
        GameManager instance2 = GameManager.getInstance();

        assertSame(instance1, instance2, "GameManager should return the same instance");

        // Check if constructor is private
        Constructor<?>[] constructors = GameManager.class.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Constructor should be private");
        }
    }
}

