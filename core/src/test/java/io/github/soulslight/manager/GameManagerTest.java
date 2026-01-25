package io.github.soulslight.manager;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.map.LevelFactory;
import io.github.soulslight.utils.GdxTestExtension;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxTestExtension.class)
class GameManagerTest {

  @BeforeEach
  void resetSingleton() throws Exception {
    Field instance = GameManager.class.getDeclaredField("instance");
    instance.setAccessible(true);
    instance.set(null, null);
  }

  @Test
  void testSingletonInstance() {
    GameManager instance1 = GameManager.getInstance();
    assertNotNull(instance1, "L'istanza non deve essere null");

    GameManager instance2 = GameManager.getInstance();
    assertSame(
        instance1, instance2, "Deve restituire sempre la stessa istanza (Pattern Singleton)");
  }

  @Test
  void testStartCampaignResetsState() {
    GameManager gm = GameManager.getInstance();

    // Sporchiamo lo stato
    gm.setCurrentLevelIndex(5);

    // Avviamo nuova campagna
    gm.startCampaign(GameMode.STORY);

    assertEquals(
        1, gm.getCurrentLevelIndex(), "StartCampaign deve resettare l'indice del livello a 1");
    assertEquals(
        GameMode.STORY, gm.getGameMode(), "Il GameMode deve essere impostato correttamente");
  }

  @Test
  void testPlayerManagement() {
    GameManager gm = GameManager.getInstance();

    // Verifichiamo la lista vuota iniziale
    assertNotNull(gm.getPlayers());
    assertTrue(gm.getPlayers().isEmpty());

    // Aggiungiamo un player
    Player p1 = new Player(Player.PlayerClass.WARRIOR, new World(new Vector2(0, 0), true), 0, 0);
    gm.addPlayer(p1);

    assertEquals(1, gm.getPlayers().size(), "Deve aver aggiunto il player");
    assertEquals(p1, gm.getPlayers().get(0));

    // Test clear
    gm.cleanUp();
    assertTrue(gm.getPlayers().isEmpty(), "CleanUp deve rimuovere tutti i player");
  }

  @Test
  void testClassSelection() {
    GameManager gm = GameManager.getInstance();

    // Default
    assertEquals(
        Player.PlayerClass.WARRIOR,
        gm.getSelectedPlayerClass(),
        "Il default dovrebbe essere WARRIOR");

    // Cambio selezione
    gm.setSelectedPlayerClass(Player.PlayerClass.MAGE);
    assertEquals(
        Player.PlayerClass.MAGE,
        gm.getSelectedPlayerClass(),
        "Deve aggiornare la classe selezionata");
  }

  @Test
  void testStoryProgression() {
    GameManager gm = GameManager.getInstance();
    gm.startCampaign(GameMode.STORY);

    int initialLevel = gm.getCurrentLevelIndex();

    boolean hasNext = gm.advanceToNextLevel();

    if (initialLevel < LevelFactory.getStoryModeLevelCount()) {
      assertTrue(hasNext, "Dovrebbe esserci un livello successivo");
      assertEquals(initialLevel + 1, gm.getCurrentLevelIndex(), "L'indice deve incrementare");
    } else {
      assertFalse(hasNext);
    }
  }

  @Test
  void testFinalLevelDetection() {
    GameManager gm = GameManager.getInstance();
    gm.startCampaign(GameMode.STORY);

    // Forziamo l'indice all'ultimo livello
    int maxLevels = LevelFactory.getStoryModeLevelCount();
    gm.setCurrentLevelIndex(maxLevels);

    assertTrue(gm.isFinalLevel(), "Deve rilevare che siamo all'ultimo livello della storia");

    // Proviamo ad avanzare oltre la fine
    boolean result = gm.advanceToNextLevel();
    assertFalse(result, "Non deve avanzare oltre l'ultimo livello");
  }

  @Test
  void testCustomModeLoop() {
    GameManager gm = GameManager.getInstance();
    gm.startCampaign(GameMode.CUSTOM);

    // Forziamo un indice alto
    gm.setCurrentLevelIndex(100);

    // In modalità custom , advanceToNextLevel dovrebbe sempre tornare true (loop infinito o
    // procedurale)
    boolean result = gm.advanceToNextLevel();

    assertTrue(result, "La modalità Custom non deve mai finire (return true)");
  }

  @Test
  void testCleanUp() {
    GameManager gm = GameManager.getInstance();
    Player p1 = new Player(Player.PlayerClass.WARRIOR, new World(new Vector2(0, 0), true), 0, 0);
    gm.addPlayer(p1);

    gm.cleanUp();

    assertTrue(gm.getPlayers().isEmpty(), "Players deve essere vuoto dopo cleanup");
    assertNull(gm.getCurrentLevel(), "CurrentLevel deve essere null dopo cleanup");
  }
}
