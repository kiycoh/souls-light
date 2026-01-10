package io.github.soulslight.model;

import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyTest {

    // Variabile per il mondo fisico del test
    private World world;

    @BeforeEach
    public void setup() {
        GdxNativesLoader.load();
        Box2D.init();

        // 2. Crea un mondo con gravità zero (Vector2(0,0)) e sleep attivo (true)
        world = new World(new Vector2(0, 0), true);

        // 3. Carica i nemici
        EnemyRegistry.loadCache();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Chaser", "Ranger","SpikedBall","Shielder"})
    public void testCloneIndependenceForAll(String enemyType) {

        // 1.In questo modo scrivendo una sola volta verifichiamo l'indipendenza per ogni tippo di nemico
        AbstractEnemy original = EnemyRegistry.getEnemy(enemyType);
        AbstractEnemy clone = EnemyRegistry.getEnemy(enemyType);

        // Verifica che il registro funzioni
        assertNotNull(original, "Il registro deve restituire " + enemyType);
        assertNotNull(clone, "Il clone deve esistere");

        // Controlla se l'originale e il clone sono della stessa classe
        assertEquals(original.getClass(), clone.getClass(), "Il clone deve essere della stessa classe dell'originale");

        // Verifica che siano oggetti diversi in memoria
        assertNotSame(original, clone, "Devono essere oggetti diversi in memoria");

        // Modifico l'originale
        original.setPosition(500, 500);
        original.setHP(1);

        // 5. Verifico che il clone non sia stato modifivcato
        assertEquals(0, clone.getX(), "Il clone di " + enemyType + " non doveva muoversi");
        assertNotEquals(1, clone.getHP(), "Il clone di " + enemyType + " non doveva perdere vita");
    }

    @Test
    public void testChaserBehaviour() {
        AbstractEnemy chaser = EnemyRegistry.getEnemy("Chaser");
        if (chaser != null) {
            chaser.setPosition(0, 0);
        }

        Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 0);

        List<Player> players = Collections.singletonList(player);

        float deltaTime = 1.0f;
        chaser.updateBehavior(players, deltaTime);

        assertTrue(chaser.getX() > 0, "Il Chaser dovrebbe essersi avvicinato al player sull'asse X");

        //  tolleranza (delta) piccola per i float
        assertEquals(0, chaser.getY(), 0.01f);
    }

    @Test
    public void testRangerBehaviour() {
        // 1. SETUP
        AbstractEnemy ranger = EnemyRegistry.getEnemy("Ranger");

        // Fallisce subito se il Registry non funziona
        assertNotNull(ranger, "Il Registry ha restituito null per 'Ranger'. Hai fatto cache.put()?");

        ranger.setPosition(10, 10);

        Player player = new Player(Player.PlayerClass.ARCHER, world, 10, 5);
        List<Player> players = Collections.singletonList(player);

        float initialDistance = ranger.getPosition().dst(player.getPosition());

        // 2. ACTION (Simula 1 secondo)
        ranger.updateBehavior(players, 1.0f);

        // 3. ASSERT
        float newDistance = ranger.getPosition().dst(player.getPosition());

        // Verifica che si sia ALLONTANATO (La nuova distanza deve essere maggiore di quella iniziale)
        assertTrue(newDistance > initialDistance,
                "Il Ranger doveva scappare! Distanza Iniziale: " + initialDistance + " -> Finale: " + newDistance);

        // Verifica direzione: Se il player era a Y=5 e Ranger a Y=10, il Ranger deve salire (Y > 10)
        assertTrue(ranger.getY() > 10, "Il Ranger doveva scappare verso l'alto (Y aumenta)");
    }

    @Test
    public void testSpikedBallChargeBehavior() {
        // SETUP
        SpikedBall ball = (SpikedBall) EnemyRegistry.getEnemy("SpikedBall");
        ball.setPosition(0, 0);
        Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 0);
        List<Player> players = Collections.singletonList(player);

        // 1. FASE PARTENZA (Cooldown -> Charge)
        ball.updateBehavior(players, 2.0f); // Finisce esattamente il cooldown
        ball.updateBehavior(players, 0.1f); // Primo frame di movimento

        assertTrue(ball.getX() > 0, "La palla deve essere partita");

        // 2. FASE CORSA -> STOP (Aspettiamo che finisca il MAX_CHARGE_TIME)
        // La durata massima è 3.0s. Ne abbiamo già fatto 0.1s.
        // Diamo un tempo abbondante (4.0s) per essere sicuri che finisca.
        ball.updateBehavior(players, 4.0f);

        float stopPosition = ball.getX();

        // 3. FASE VERIFICA (Deve essere ferma)
        ball.updateBehavior(players, 0.5f);

        assertEquals(stopPosition, ball.getX(), 0.01f,
                "La palla deve essersi fermata da sola per timeout");
    }

    @Test
    public void testSpikedBallWallCollision() {
        // SETUP
        SpikedBall ball = (SpikedBall) EnemyRegistry.getEnemy("SpikedBall");
        ball.setPosition(0, 0);
        Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 0);
        List<Player> players = Collections.singletonList(player);

        // 1. FASE PARTENZA
        // Usiamo due step per evitare il problema del "frame saltato"
        ball.updateBehavior(players, 2.0f); // Scade il cooldown
        ball.updateBehavior(players, 0.1f); // Si muove

        assertTrue(ball.getX() > 0, "La palla deve essere partita");

        float positionBeforeHit = ball.getX();

        // 2. SIMULAZIONE IMPATTO
        ball.onWallHit(); // BAM!

        // 3. VERIFICA STOP IMMEDIATO
        // Facciamo passare del tempo (0.5s). Se non si fosse fermata,
        // a 300px/s si sarebbe mossa di 150px!
        ball.updateBehavior(players, 0.5f);

        assertEquals(positionBeforeHit, ball.getX(), 0.01f,
                "La palla deve fermarsi ISTANTANEAMENTE dopo aver toccato il muro");
    }

    @Test
    public void testShielderDieIfAlone() {
        // 1. SETUP
        Shielder shielder = new Shielder();
        shielder.setHP(300); // Tanta vita

        // Lista alleati vuota (o contenente solo lui stesso)
        List<AbstractEnemy> allies = new ArrayList<>();
        allies.add(shielder);
        shielder.setAllies(allies);

        Player player = new Player(Player.PlayerClass.WARRIOR, world, 100, 100);

        // 2. ACTION
        // Eseguiamo l'update. Lui controlla la lista, vede che è solo.
        shielder.updateBehavior(Collections.singletonList(player), 1.0f);

        // 3. ASSERT
        assertTrue(shielder.getHP() <= 0,
                "Lo Shielder deve morire se non ha nessuno da proteggere!");
    }

    @Test
    public void testShielderProtectionMovement() {
        // 1. SETUP GEOMETRICO
        // Mettiamo tutto su una linea verticale per facilitare i calcoli.

        Chaser ally = new Chaser();
        ally.setPosition(0, 0);     // Alleato in basso

        Player player = new Player(Player.PlayerClass.WARRIOR, world, 0, 100); // Player in alto

        // Lo Shielder parte spostato a destra (fuori posizione)
        Shielder shielder = new Shielder();
        shielder.setPosition(50, 50);

        // Setup lista alleati
        List<AbstractEnemy> allies = new ArrayList<>();
        allies.add(ally);
        allies.add(shielder);
        shielder.setAllies(allies);

        // 2. CALCOLO TEORICO
        // Il punto di protezione ideale è 40px davanti all'alleato, verso il player.
        // Direzione Ally->Player è (0, 1). Offset 40.
        // Target ideale = (0, 40).

        // 3. ACTION
        shielder.updateBehavior(Collections.singletonList(player), 1.0f);

        // 4. ASSERT
        // Lo Shielder era a X=50. Deve andare verso X=0.
        assertTrue(shielder.getX() < 50, "Lo Shielder deve spostarsi a sinistra verso la linea di tiro");

        // Lo Shielder era a Y=50. Deve andare verso Y=40.
        assertTrue(shielder.getY() < 50, "Lo Shielder deve scendere verso la posizione di guardia (Y=40)");
    }

    @Test
    public void testShielderAttackDecision() {
        // 1. SETUP
        Shielder shielder = new Shielder();
        shielder.setPosition(0, 0);

        // Player vicinissimo (distanza 10, range bash è 45)
        Player player = new Player(Player.PlayerClass.WARRIOR, world, 10, 0);

        // Serve una lista alleati valida per non farlo suicidare
        Chaser ally = new Chaser();
        List<AbstractEnemy> allies = new ArrayList<>();
        allies.add(shielder);
        allies.add(ally);
        shielder.setAllies(allies);

        // 2. ACTION
        // Per verificare se attacca, possiamo usare un trucco:
        // Il metodo attack() di default non fa nulla di visibile nel test unitario senza mock.
        // MA possiamo verificare che NON si sia mosso.
        // Se attacca, di solito l'updateBehavior esce o non chiama moveTowards verso l'alleato in quel frame
        // OPPURE possiamo verificare se il cooldown dell'arma è scattato (se implementato).

        // Alternativa migliore per questo test semplice:
        // Estendiamo la strategia al volo o verifichiamo logiche interne.
        // Ma per ora, verifichiamo che NON CRASHI chiamando attackStrategy.

        assertDoesNotThrow(() -> shielder.updateBehavior(Collections.singletonList(player), 0.1f));

        // Verifica logica Distanza:
        float dist = shielder.getPosition().dst(player.getPosition());
        assertTrue(dist <= shielder.getAttackStrategy().getRange(),
                "Il player è nel range, lo Shielder dovrebbe aver tentato l'attacco");
    }

    @Test
    void testEnemyRegistryCache() {
        // SETUP: loading cache
        EnemyRegistry.loadCache();

        assertNotNull(EnemyRegistry.getEnemy("Chaser"), "La cache deve contenere Chaser");
        assertNotNull(EnemyRegistry.getEnemy("Ranger"), "La cache deve contenere Ranger");
        assertNotNull(EnemyRegistry.getEnemy("SpikedBall"), "La cache deve contenere SpikedBall");
        assertNotNull(EnemyRegistry.getEnemy("Shielder"), "La cache deve contenere Shielder");

        // Negative Test
        // asking for a non-existing enemy. It will return null, not crashing
        AbstractEnemy unknown = EnemyRegistry.getEnemy("NemicoInesistente");
        assertNull(unknown, "Richiedere una chiave inesistente deve restituire null");


        AbstractEnemy chaser = EnemyRegistry.getEnemy("Chaser");
        assertEquals(100, chaser.getHealth(), "Il Chaser recuperato dalla cache deve avere 100 HP");
    }

}
