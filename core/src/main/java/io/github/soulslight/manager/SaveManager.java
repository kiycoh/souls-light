package io.github.soulslight.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.GameStateMemento;

public class SaveManager {

  private static final String SAVE_FILE = "savegame.sav"; // Renamed to .sav to imply binary/encoded
  private static final String BACKUP_FILE = "savegame.bak";
  private final Json json;

  public SaveManager() {
    this.json = new Json();
    this.json.setIgnoreUnknownFields(true);
  }

  public void saveGame(GameModel model) {
    FileHandle file = Gdx.files.local(SAVE_FILE);
    FileHandle backup = Gdx.files.local(BACKUP_FILE);

    try {
      // Create Memento
      GameStateMemento memento = model.createMemento();
      String jsonString = json.toJson(memento);

      // Encode to Base64 (Obfuscation + prevent encoding issues)
      String encodedString = Base64Coder.encodeString(jsonString);

      // Backup existing save if it exists
      if (file.exists()) {
        file.copyTo(backup);
      }

      // Write new save
      file.writeString(encodedString, false);
      Gdx.app.log("SaveManager", "Game saved successfully.");

    } catch (Exception e) {
      Gdx.app.error("SaveManager", "Failed to save game!", e);
    }
  }

  public void loadGame(GameModel model) {
    FileHandle file = Gdx.files.local(SAVE_FILE);

    // Try loading primary file
    if (loadFromFile(file, model)) {
      return;
    }

    // If primary failed, try backup
    Gdx.app.log("SaveManager", "Primary save failed. Attempting backup...");
    FileHandle backup = Gdx.files.local(BACKUP_FILE);
    if (loadFromFile(backup, model)) {
      Gdx.app.log("SaveManager", "Backup loaded successfully.");
    } else {
      Gdx.app.error("SaveManager", "Backup failed or does not exist.");
    }
  }

  private boolean loadFromFile(FileHandle file, GameModel model) {
    if (!file.exists()) return false;

    try {
      String encodedContent = file.readString();
      if (encodedContent == null || encodedContent.trim().isEmpty()) {
        return false;
      }

      // Decode Base64
      String jsonString = Base64Coder.decodeString(encodedContent);

      GameStateMemento memento = json.fromJson(GameStateMemento.class, jsonString);

      if (memento != null && memento.players != null) {
        model.restoreMemento(memento);
        return true;
      }
    } catch (SerializationException e) {
      Gdx.app.error("SaveManager", "Corrupted JSON in file: " + file.name(), e);
    } catch (IllegalArgumentException e) {
      Gdx.app.error("SaveManager", "Invalid Base64 content in file: " + file.name(), e);
    } catch (Exception e) {
      Gdx.app.error("SaveManager", "General error loading file: " + file.name(), e);
    }
    return false;
  }

  public boolean hasSaveFile() {
    return Gdx.files.local(SAVE_FILE).exists();
  }
}
