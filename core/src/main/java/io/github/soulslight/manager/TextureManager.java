package io.github.soulslight.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import io.github.soulslight.model.AbstractEnemy;
import io.github.soulslight.model.Oblivion;
import io.github.soulslight.model.Ranger;
import io.github.soulslight.model.Shielder;
import io.github.soulslight.model.SpikedBall;

import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    private static final Map<String, Texture> textures = new HashMap<>();

    public static void load() {
        // Carica le texture e assegna le chiavi sottoforma di stringhe
        if (!textures.isEmpty()) return;

        textures.put("player", new Texture(Gdx.files.internal("images/player.png")));
        textures.put("skeleton", new Texture(Gdx.files.internal("images/skeleton.png")));
        textures.put("archer", new Texture(Gdx.files.internal("images/archer.png")));
        textures.put("slime", new Texture(Gdx.files.internal("images/slime.png")));
        textures.put("shielder", new Texture(Gdx.files.internal("images/shielder.png")));
        textures.put("boss", new Texture(Gdx.files.internal("images/boss.png")));

        if (Gdx.files.internal("images/arrow.png").exists()) {
            textures.put("arrow", new Texture(Gdx.files.internal("images/arrow.png")));
        } else {
            // Se non c'è l'immagine arrow, usa quella del player o un'altra esistente
            textures.put("arrow", textures.get("player"));
        }

        // Filtro Pixel Art per non sfocare
        for (Texture t : textures.values()) {
            t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    public static Texture get(String name) {
        if (!textures.containsKey(name)) {
            // Protezione contro i crash: se chiedi una texture che non esiste, stampa errore e ridai il player
            System.err.println("ERRORE: Texture mancante -> " + name);
            return textures.get("player");
        }
        return textures.get(name);
    }

    public static Texture getEnemyTexture(AbstractEnemy enemy) {
        if (enemy instanceof Ranger) return get("archer");
        if (enemy instanceof SpikedBall) return get("slime");
        if (enemy instanceof Shielder) return get("shielder");
        if (enemy instanceof Oblivion) return get("boss");
        return get("skeleton");
    }

    public static void dispose() {
        for (Texture t : textures.values()) t.dispose();
        textures.clear();
    }
}
