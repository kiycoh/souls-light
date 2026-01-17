package io.github.soulslight.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.GameStateMemento;

public class SaveManager {

    private static final String SAVE_FILE = "savegame.json";
    private final Json json;

    public SaveManager() {
        this.json = new Json();
    }

    // Salva il gioco
    public void saveGame(GameModel model) {

        float currentHealth = 0;
        if (model.getPlayer() != null) {
            currentHealth = model.getPlayer().getHealth();
        }

        // 1. Crea il memento con la VITA (Health)
        GameStateMemento memento = new GameStateMemento(
            currentHealth,
            model.getPlayer().getPosition(),
            1
        );

        // Converte in file di testo
        String text = json.toJson(memento);
        FileHandle file = Gdx.files.local(SAVE_FILE);
        file.writeString(text, false);

        Gdx.app.log("SaveManager", "Partita salvata (HP: " + currentHealth + ") su: " + file.path());
    }

   //Ricarica il salvataggio
    public void loadGame(GameModel model) {
        FileHandle file = Gdx.files.local(SAVE_FILE);

        if (!file.exists()) {
            Gdx.app.log("SaveManager", "Nessun salvataggio trovato.");
            return;
        }

        try {
            GameStateMemento memento = json.fromJson(GameStateMemento.class, file.readString());
            model.restoreMemento(memento); // Deleghiamo al model
            Gdx.app.log("SaveManager", "Partita caricata con successo!");
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Errore nel caricamento del salvataggio", e);
        }
    }

    public boolean hasSaveFile() {
        return Gdx.files.local(SAVE_FILE).exists();
    }
}
