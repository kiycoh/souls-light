package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyTest {

    @BeforeEach
    public void setup() {
        EnemyRegistry.loadCache();
    }



    @Test
    public void testCloneIndependence() {
        //  Chiedo al registro due copie dello stesso nemico
        AbstractEnemy chaser1 = EnemyRegistry.getEnemy("Chaser");
        AbstractEnemy chaser2 = EnemyRegistry.getEnemy("Chaser");

        // Assicuriamoci che esistano
        assertNotNull(chaser1, "Il registro deve restituire un chaser");
        assertNotNull(chaser2, "Il registro deve restituire un secondo chaser");

        //  Verifico che siano due oggetti diversi in memoria
        assertNotSame(chaser1, chaser2, "I due chaser devono essere oggetti distinti in memoria");

        //  Modifico il primo
        chaser1.setPosition(50, 50);
        chaser1.setHP(10); // Lo ferisco

        //  Verifico che il secondo non abbia subito modifiche
        assertEquals(0, chaser2.getX(), "Il chaser2 non doveva muoversi");
        assertEquals(0, chaser2.getY(), "Il chaser2non doveva muoversi");
        assertEquals(100, chaser2.getHP(), "Il chaser2 deve avere ancora tutta la vita");
    }


    @Test
    public void testChaserBehaviour() {

        AbstractEnemy chaser = EnemyRegistry.getEnemy("Chaser");
        chaser.setPosition(0, 0);

        Player player = new Player(Player.PlayerClass.WARRIOR); 
        player.setPosition(100, 0); // Lontano sull'asse X
        List<Player> players = Collections.singletonList(player);


        float deltaTime = 1.0f;
        chaser.updateBehavior(players, deltaTime);

        // Assert: Il Chaser deve essersi mosso verso destra (X > 0)
        assertTrue(chaser.getX() > 0, "Il Chaser dovrebbe essersi avvicinato al player sull'asse X");
        assertEquals(0, chaser.getY(), 0.01f, "Il Chaser non doveva muoversi sulla Y");
    }

   /* @Test
    public void testRangerBehaviour() {
        // Setup: Un Ranger vicino al player
        AbstractEnemy ranger = EnemyRegistry.getEnemy("Ranger");
        ranger.setPosition(10, 10); // Molto vicino

        Player player = new Player(Player.PlayerClass.WARRIOR);
        player.setPosition(10, 10);
        List<Player> players = Collections.singletonList(player);

        // Action: Update
        ranger.updateBehavior(players, 1.0f);

        // Assert: Il Ranger deve SCAPPARE (allontanarsi) o FERMARSI per sparare
        // Dipende dalla tua logica, ma qui verifichi che faccia quello che deve.
        // Esempio: se la logica è "scappa se troppo vicino":
        // assertTrue(ranger.getPosition().dst(player.getPosition()) > 0, "Il ranger deve allontanarsi");
    }*/
}
