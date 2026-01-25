package io.github.soulslight.manager;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import io.github.soulslight.model.EnemyMemento;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.GameStateMemento;
import io.github.soulslight.model.PlayerMemento;
import io.github.soulslight.model.ProjectileMemento;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.utils.GdxTestExtension;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(GdxTestExtension.class)
class SaveSystemTest {

  @Test
  void testMementoIsPOJO() {
    // Mandate check: Memento should NOT be a record anymore to support standard
    // LibGDX JSON
    assertFalse(
        GameStateMemento.class.isRecord(), "GameStateMemento should be a POJO for LibGDX JSON");
    assertFalse(PlayerMemento.class.isRecord(), "PlayerMemento should be a POJO for LibGDX JSON");
    assertFalse(EnemyMemento.class.isRecord(), "EnemyMemento should be a POJO for LibGDX JSON");
    assertFalse(
        ProjectileMemento.class.isRecord(), "ProjectileMemento should be a POJO for LibGDX JSON");
  }

  @Test
  void testPOJOSerialization() {
    // Create a dummy memento
    List<PlayerMemento> players = new ArrayList<>();
    players.add(new PlayerMemento(Player.PlayerClass.WARRIOR, 100f, 10f, 20f));

    List<EnemyMemento> enemies = new ArrayList<>();
    enemies.add(new EnemyMemento("Chaser", 50f, 50f, 100f));

    List<ProjectileMemento> projectiles = new ArrayList<>();
    projectiles.add(new ProjectileMemento(10f, 10f, 1f, 1f));

    long seed = 123456789L;

    GameStateMemento original =
        new GameStateMemento(
            players,
            enemies,
            projectiles,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            seed,
            2);

    // Serialize with LibGDX Json (Standard Way)
    Json json = new Json();
    json.setOutputType(OutputType.json);

    String jsonText = json.toJson(original);

    assertNotNull(jsonText);

    // Deserialize
    GameStateMemento loaded = json.fromJson(GameStateMemento.class, jsonText);

    assertNotNull(loaded);
    assertEquals(original.currentLevelIndex, loaded.currentLevelIndex);
    assertEquals(original.seed, loaded.seed);
    assertEquals(original.players.size(), loaded.players.size());
    assertEquals(original.enemies.size(), loaded.enemies.size());
    assertEquals(original.projectiles.size(), loaded.projectiles.size());

    EnemyMemento e1 = loaded.enemies.get(0);
    assertEquals("Chaser", e1.type);
    assertEquals(50f, e1.x);
  }

  @Test
  void testSaveManagerIntegration() {
    SaveManager saveManager = new SaveManager();

    // Ensure clean state
    FileHandle saveFile = Gdx.files.local("savegame.sav");
    if (saveFile.exists()) saveFile.delete();

    assertFalse(saveManager.hasSaveFile(), "Should not have save file initially");

    // Mock GameModel to return a valid memento
    GameModel mockModel = Mockito.mock(GameModel.class);
    GameStateMemento memento =
        new GameStateMemento(
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            999L,
            1);
    Mockito.when(mockModel.createMemento()).thenReturn(memento);

    // Save
    saveManager.saveGame(mockModel);

    assertTrue(saveManager.hasSaveFile(), "Should have save file after saving");
    assertTrue(saveFile.exists(), "Physical file should exist");

    // Clean up
    saveFile.delete();
    assertFalse(saveManager.hasSaveFile(), "Should not have save file after deletion");
  }

  @Test
  void testLoadFromBackup() {
    SaveManager saveManager = new SaveManager();

    // Creiamo un salvataggio di BACKUP valido
    FileHandle backup = Gdx.files.local("savegame.bak");

    // Creiamo un memento vuoto ma valido
    GameStateMemento validMemento =
        new GameStateMemento(
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            123L,
            1);

    Json json = new Json();
    String validJson = json.toJson(validMemento);

    // Scriviamo nel backup codificato correttamente
    String encodedBackup = com.badlogic.gdx.utils.Base64Coder.encodeString(validJson);
    backup.writeString(encodedBackup, false);

    // Creiamo un salvataggio primario corrotto
    FileHandle primary = Gdx.files.local("savegame.sav");
    // "INVALID" non è Base64 valido, ma il SaveManager ora dovrebbe gestirlo e
    // passare al backup
    primary.writeString("INVALID_CONTENT_NOT_BASE64", false);

    // Proviamo a caricare
    GameModel mockModel = Mockito.mock(GameModel.class);
    saveManager.loadGame(mockModel);

    // Verifica: Il model deve aver chiamato restoreMemento
    Mockito.verify(mockModel, Mockito.times(1)).restoreMemento(Mockito.any(GameStateMemento.class));

    // Pulizia
    backup.delete();
    primary.delete();
  }

  @Test
  void testBackupCreationOnSave() {
    SaveManager saveManager = new SaveManager();
    GameModel mockModel = Mockito.mock(GameModel.class);

    // Primo salvataggio
    GameStateMemento m1 =
        new GameStateMemento(
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            1L,
            1);
    Mockito.when(mockModel.createMemento()).thenReturn(m1);
    saveManager.saveGame(mockModel);

    // Secondo salvataggio
    GameStateMemento m2 =
        new GameStateMemento(
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            2L,
            2);
    Mockito.when(mockModel.createMemento()).thenReturn(m2);
    saveManager.saveGame(mockModel);

    // Verifica esistenza file
    FileHandle save = Gdx.files.local("savegame.sav");
    FileHandle backup = Gdx.files.local("savegame.bak"); //

    assertTrue(save.exists());
    assertTrue(backup.exists(), "Il file di backup deve esistere dopo il secondo salvataggio"); //

    save.delete();
    backup.delete();
  }

  @Test
  void testCorruptedSaveNoCrash() {
    SaveManager saveManager = new SaveManager();
    FileHandle save = Gdx.files.local("savegame.sav");
    FileHandle backup = Gdx.files.local("savegame.bak"); // 1. Riferimento al backup

    // Cancelliamo eventuali residui di altri test
    if (backup.exists()) {
      backup.delete();
    }
    if (save.exists()) {
      save.delete();
    }

    // Scriviamo dati spazzatura nel file principale
    save.writeString("!!! QUESTA STRINGA NON E' BASE64 !!!", false);

    GameModel mockModel = Mockito.mock(GameModel.class);

    assertDoesNotThrow(() -> saveManager.loadGame(mockModel));

    // restoreMemento non deve essere mai stato chiamato perché entrambi i tentativi
    // sono falliti
    Mockito.verify(mockModel, Mockito.never()).restoreMemento(Mockito.any());

    // Pulizia finale
    if (save.exists()) save.delete();
  }
}
