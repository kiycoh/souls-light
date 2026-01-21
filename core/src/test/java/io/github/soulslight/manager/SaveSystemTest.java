package io.github.soulslight.manager;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import io.github.soulslight.model.GameStateMemento;
import io.github.soulslight.model.PlayerMemento;
import io.github.soulslight.model.entities.Player;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SaveSystemTest {

  @Test
  void testMementoIsPOJO() {
      // Mandate check: Memento should NOT be a record anymore to support standard LibGDX JSON
      assertFalse(GameStateMemento.class.isRecord(), "GameStateMemento should be a POJO for LibGDX JSON");
      assertFalse(PlayerMemento.class.isRecord(), "PlayerMemento should be a POJO for LibGDX JSON");
  }

  @Test
  void testPOJOSerialization() {
      // Create a dummy memento
      List<PlayerMemento> players = new ArrayList<>();
      players.add(new PlayerMemento(Player.PlayerClass.WARRIOR, 100f, 10f, 20f));
      players.add(new PlayerMemento(Player.PlayerClass.ARCHER, 80f, 15f, 25f));
      
      GameStateMemento original = new GameStateMemento(players, 2);

      // Serialize with LibGDX Json (Standard Way)
      Json json = new Json();
      json.setOutputType(OutputType.json);
      
      String jsonText = json.toJson(original);
      // System.out.println(jsonText); // For debugging
      
      assertNotNull(jsonText);
      
      // Deserialize
      GameStateMemento loaded = json.fromJson(GameStateMemento.class, jsonText);
      
      assertNotNull(loaded);
      assertEquals(original.currentLevelIndex, loaded.currentLevelIndex);
      assertEquals(original.players.size(), loaded.players.size());
      
      PlayerMemento p1 = loaded.players.get(0);
      assertEquals(Player.PlayerClass.WARRIOR, p1.type);
      assertEquals(100f, p1.health);
      
      PlayerMemento p2 = loaded.players.get(1);
      assertEquals(Player.PlayerClass.ARCHER, p2.type);
      assertEquals(80f, p2.health);
  }
}
