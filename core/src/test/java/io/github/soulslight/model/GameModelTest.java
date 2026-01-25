package io.github.soulslight.model;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.model.enemies.Chaser;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.utils.GdxTestExtension;
import java.lang.reflect.Field;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxTestExtension.class)
class GameModelTest {

    private GameModel model;

    @BeforeAll
    static void initBox2D() {
        Box2D.init();
    }

    @BeforeEach
    void setUp() throws Exception {
        Field instance = GameManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);

        GameManager.getInstance().setSelectedPlayerClass(Player.PlayerClass.WARRIOR);
        GameManager.getInstance().startCampaign(io.github.soulslight.manager.GameMode.STORY);
    }

    @AfterEach
    void tearDown() {
        if (model != null) {
            try {
                model.dispose();
            } catch (Exception e) {
                // Ignora errori di dispose durante i test per evitare falsi negativi
                // se il mondo nativo è già stato compromesso
                System.err.println("Warning: Error during model dispose: " + e.getMessage());
            }
            model = null;
        }
    }

    @Test
    void testInitialization() {
        model = new GameModel();
        assertNotNull(model.getWorld());
        assertNotNull(model.getLevel());
    }

    @Test
    void testPhysicsUpdate() {
        model = new GameModel();
        Player p1 = model.getPlayers().get(0);
        Vector2 initialPos = p1.getPosition().cpy();

        if(p1.getBody() != null) {
            p1.getBody().setLinearVelocity(10f, 0f);
        }

        model.update(1.0f);

        assertNotEquals(initialPos.x, p1.getPosition().x, 0.1f);
    }

    @Test
    void testDeadEnemyCleanup() {
        model = new GameModel();

        Chaser enemy = new Chaser();
        // Usa coordinate sicure, lontano da muri o altri player per evitare collisioni impreviste durante il test
        enemy.createBody(model.getWorld(), 500, 500);
        model.getLevel().addEnemy(enemy);

        assertTrue(model.getActiveEnemies().contains(enemy));
        assertNotNull(enemy.getBody());

        enemy.takeDamage(9999f);
        assertTrue(enemy.isDead());

        // Update
        model.update(0.1f);

        assertFalse(model.getActiveEnemies().contains(enemy));
        assertNull(enemy.getBody());
    }

    @Test
    void testMementoSaveAndLoad() {
        model = new GameModel();
        Player p1 = model.getPlayers().get(0);

        p1.setHealth(50f);
        p1.setPosition(200f, 200f);

        GameStateMemento memento = model.createMemento();

        // Cambiamo stato
        p1.setHealth(10f);
        p1.setPosition(300f, 300f);

        // RESTORE: Qui c'è il rischio maggiore di crash nativo
        // Restore deve distruggere i corpi vecchi e crearne di nuovi.
        model.restoreMemento(memento);

        Player restoredP1 = model.getPlayers().get(0);
        assertEquals(50f, restoredP1.getHealth(), 0.1f);
        assertEquals(200f, restoredP1.getPosition().x, 2.0f);
    }
}
