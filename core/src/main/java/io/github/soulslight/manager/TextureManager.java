package io.github.soulslight.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.enemies.Oblivion;
import io.github.soulslight.model.enemies.Ranger;
import io.github.soulslight.model.enemies.Shielder;
import io.github.soulslight.model.enemies.SpikedBall;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TextureManager {

    private static final Map<String, Texture> textures = new HashMap<>();

    private static Animation<TextureRegion> chaserWalkAnim;
    private static Animation<TextureRegion> rangerWalkAnim;
    private static Animation<TextureRegion> shielderWalkAnim;
    private static Animation<TextureRegion> spikedBallWalkAnim;

    private static Animation<TextureRegion> spikedBallChargeAnim;

    private static Animation<TextureRegion> oblivionIdleAnim;
    private static Animation<TextureRegion> oblivionWalkAnim;
    private static Animation<TextureRegion> oblivionMeleeWindupAnim;
    private static Animation<TextureRegion> oblivionMeleeAttackAnim;
    private static Animation<TextureRegion> oblivionSpellAnim;
    private static Animation<TextureRegion> oblivionTeleportAnim;

    private static final Map<String, Animation<TextureRegion>> builtAnims = new HashMap<>();

    // used for tests
    private static String ASSETS_BASE = "";

    public static void load() {
        // Carica le texture e assegna le chiavi sottoforma di stringhe
        if (!textures.isEmpty()) return;

        detectAssetsBase();

        textures.put("player", new Texture(Gdx.files.internal(ASSETS_BASE + "images/player.png")));
        textures.put("skeleton", new Texture(Gdx.files.internal(ASSETS_BASE + "images/skeleton.png")));
        textures.put("archer", new Texture(Gdx.files.internal(ASSETS_BASE + "images/archer.png")));
        textures.put("slime", new Texture(Gdx.files.internal(ASSETS_BASE + "images/slime.png")));
        textures.put("shielder", new Texture(Gdx.files.internal(ASSETS_BASE + "images/shielder.png")));
        textures.put("boss", new Texture(Gdx.files.internal(ASSETS_BASE + "images/boss.png")));

        if (Gdx.files.internal(ASSETS_BASE + "images/arrow.png").exists()) {
            textures.put("arrow", new Texture(Gdx.files.internal(ASSETS_BASE + "images/arrow.png")));
        } else {
            // Se non c'è l'immagine arrow, usa quella del player o un'altra esistente
            textures.put("arrow", textures.get("player"));
        }

        buildAnimIfExists("chaserWalk", 16, 23);
        chaserWalkAnim = getBuiltAnim("chaserWalk");

        buildAnimIfExists("rangerWalk", 16, 17);
        rangerWalkAnim = getBuiltAnim("rangerWalk");

        buildAnimIfExists("shielderWalk", 16, 27);
        shielderWalkAnim = getBuiltAnim("shielderWalk");

        buildAnimIfExists("spikedBallWalk", 32, 34);
        spikedBallWalkAnim = getBuiltAnim("spikedBallWalk");

        buildAnimIfExists("spikedBallCharge", 32, 34);
        spikedBallChargeAnim = getBuiltAnim("spikedBallCharge");

        buildAnimIfExists("oblivionIdle", 288, 160);
        oblivionIdleAnim = getBuiltAnim("oblivionIdle");

        buildAnimIfExists("oblivionWalk", 288, 160);
        oblivionWalkAnim = getBuiltAnim("oblivionWalk");

        buildAnimIfExists("oblivionMeleeWindup", 288, 160);
        oblivionMeleeWindupAnim = getBuiltAnim("oblivionMeleeWindup");

        buildAnimIfExists("oblivionMeleeAttack", 288, 160);
        oblivionMeleeAttackAnim = getBuiltAnim("oblivionMeleeAttack");

        buildAnimIfExists("oblivionSpell", 288, 160);
        oblivionSpellAnim = getBuiltAnim("oblivionSpell");

        buildAnimIfExists("oblivionTeleport", 288, 160);
        oblivionTeleportAnim = getBuiltAnim("oblivionTeleport");

        // Filtro Pixel Art
        for (Texture t : textures.values()) {
            t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    private static void detectAssetsBase() {
        if (Gdx.files.internal("images/player.png").exists()) {
            ASSETS_BASE = "";
        } else if (Gdx.files.internal("../assets/images/player.png").exists()) {
            ASSETS_BASE = "../assets/";
        } else {
            ASSETS_BASE = "";
        }
    }

    public static Texture get(String name) {
        if (!textures.containsKey(name)) {
            // Protezione contro i crash: se chiedi una texture che non esiste, stampa errore e ridai il
            // player
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

    public static TextureRegion getChaserWalkFrame(float stateTime) {
        if (chaserWalkAnim == null) return null;
        return chaserWalkAnim.getKeyFrame(stateTime, true);
    }

    public static TextureRegion getRangerWalkFrame(float stateTime) {
        if (rangerWalkAnim == null) return null;
        return rangerWalkAnim.getKeyFrame(stateTime, true);
    }

    public static TextureRegion getShielderWalkFrame(float stateTime) {
        if (shielderWalkAnim == null) return null;
        return shielderWalkAnim.getKeyFrame(stateTime, true);
    }

    public static TextureRegion getSpikedBallWalkFrame(float stateTime) {
        if (spikedBallWalkAnim == null) return null;
        return spikedBallWalkAnim.getKeyFrame(stateTime, true);
    }

    public static TextureRegion getSpikedBallChargeFrame(float stateTime) {
        if (spikedBallChargeAnim == null) return null;
        return spikedBallChargeAnim.getKeyFrame(stateTime, true);
    }

    public static TextureRegion getOblivionIdleFrame(float stateTime) {
        if (oblivionIdleAnim == null) return null;
        return oblivionIdleAnim.getKeyFrame(stateTime, true);
    }

    public static TextureRegion getOblivionWalkFrame(float stateTime) {
        if (oblivionWalkAnim == null) return null;
        return oblivionWalkAnim.getKeyFrame(stateTime, true);
    }

    public static TextureRegion getOblivionMeleeWindupFrame(float stateTime) {
        if (oblivionMeleeWindupAnim == null) return null;
        return oblivionMeleeWindupAnim.getKeyFrame(stateTime, true);
    }

    public static TextureRegion getOblivionMeleeAttackFrame(float stateTime) {
        if (oblivionMeleeAttackAnim == null) return null;
        return oblivionMeleeAttackAnim.getKeyFrame(stateTime, true);
    }

    public static TextureRegion getOblivionSpellFrame(float stateTime) {
        if (oblivionSpellAnim == null) return null;
        return oblivionSpellAnim.getKeyFrame(stateTime, true);
    }

    public static TextureRegion getOblivionTeleportFrame(float stateTime) {
        if (oblivionTeleportAnim == null) return null;
        return oblivionTeleportAnim.getKeyFrame(stateTime, true);
    }

    public static void dispose() {
        for (Texture t : textures.values()) t.dispose();
        textures.clear();

        chaserWalkAnim = null;
        rangerWalkAnim = null;
        shielderWalkAnim = null;
        spikedBallWalkAnim = null;
        spikedBallChargeAnim = null;

        oblivionIdleAnim = null;
        oblivionWalkAnim = null;
        oblivionMeleeWindupAnim = null;
        oblivionMeleeAttackAnim = null;
        oblivionSpellAnim = null;
        oblivionTeleportAnim = null;

        builtAnims.clear();
    }

    // helper
    private static void buildAnimIfExists(String key, int frameW, int frameH) {
        String path = ASSETS_BASE + "images/" + key + ".png";
        if (!Gdx.files.internal(path).exists()) {
            return;
        }

        Texture tex = new Texture(Gdx.files.internal(path));
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        textures.put(key, tex);

        int texW = tex.getWidth();
        int texH = tex.getHeight();

        int cols = texW / frameW;
        int rows = texH / frameH;

        if (cols <= 0 || rows <= 0) {
            return;
        }

        ArrayList<TextureRegion> frames = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = c * frameW;
                int y = r * frameH;
                frames.add(new TextureRegion(tex, x, y, frameW, frameH));
            }
        }

        if (frames.isEmpty()) {
            return;
        }

        Animation<TextureRegion> anim = new Animation<>(0.08f, frames.toArray(new TextureRegion[0]));
        anim.setPlayMode(Animation.PlayMode.LOOP);
        builtAnims.put(key, anim);
    }

    private static Animation<TextureRegion> getBuiltAnim(String key) {
        return builtAnims.get(key);
    }
}
