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
        assertNotNull(chaser1, "Il registro deve restituire uno zombie");
        assertNotNull(chaser2, "Il registro deve restituire un secondo zombie");

        // 2. Verifico che siano due oggetti DIVERSI in memoria
        assertNotSame(chaser1, chaser2, "I due zombie devono essere oggetti distinti in memoria");

        // 3. Modifico il primo
        chaser1.setPosition(50, 50);
        chaser1.setHP(10); // Lo ferisco

        // 4. Verifico che il secondo sia rimasto INTATTO (Valori di default)
        assertEquals(0, chaser2.getX(), "Lo zombie 2 non doveva muoversi");
        assertEquals(0, chaser2.getY(), "Lo zombie 2 non doveva muoversi");
        assertEquals(100, chaser2.getHP(), "Lo zombie 2 deve avere ancora tutta la vita");
    }


    @Test
    public void testChaserBehaviour() {
        // Setup: Un Chaser a (0,0) e un Player a (100, 0)
        AbstractEnemy chaser = EnemyRegistry.getEnemy("Chaser");
        chaser.setPosition(0, 0);

        Player player = new Player(Player.PlayerClass.WARRIOR); // Assumiamo costruttore base
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
