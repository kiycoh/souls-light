package io.github.soulslight.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.GameStateMemento;

public class SaveManager {

    private static final String SAVE_FILE = "savegame.json";
    private final Json json;

    public SaveManager() {
        this.json = new Json();
        // Keeps the parser from crashing on old/removed fields
        this.json.setIgnoreUnknownFields(true);
    }

    // SAVE: Always overwrites or creates the file
    public void saveGame(GameModel model) {
        try {
            GameStateMemento memento = model.createMemento();
            String text = json.toJson(memento);

            FileHandle file = Gdx.files.local(SAVE_FILE);
            file.writeString(text, false); // false = Overwrite mode

            Gdx.app.log("SaveManager", "Game saved successfully to: " + file.path());
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to save game!", e);
        }
    }

    // LOAD: Handles corrupted/empty files by deleting them
    public void loadGame(GameModel model) {
        FileHandle file = Gdx.files.local(SAVE_FILE);

        if (!file.exists()) {
            Gdx.app.log("SaveManager", "No save file found.");
            return;
        }

        try {
            // Attempt to read the file
            String fileContent = file.readString();

            // Check for empty content before parsing
            if (fileContent == null || fileContent.trim().isEmpty()) {
                Gdx.app.error("SaveManager", "Save file is empty. Deleting it.");
                file.delete();
                return;
            }

            GameStateMemento memento = json.fromJson(GameStateMemento.class, fileContent);

            // VALIDATION: Check if data is consistent (has players)
            if (memento != null && memento.players != null && !memento.players.isEmpty()) {
                model.restoreMemento(memento);
                Gdx.app.log("SaveManager", "Game loaded successfully!");
            } else {
                // FIX: If incompatible (e.g. version mismatch), delete the file
                Gdx.app.error("SaveManager", "Incompatible save file detected. Deleting to allow new save.");
                file.delete();
            }

        } catch (SerializationException e) {
            // If JSON format is broken, delete the file
            Gdx.app.error("SaveManager", "Corrupted save file. Deleting.", e);
            file.delete();
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "General error loading game", e);
        }
    }

    public boolean hasSaveFile() {
        return Gdx.files.local(SAVE_FILE).exists();
    }
}
