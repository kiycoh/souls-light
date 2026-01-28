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

  private static TextureManager instance;

  private final Map<String, Texture> textures = new HashMap<>();

  private Animation<TextureRegion> chaserWalkAnim;
  private Animation<TextureRegion> rangerWalkAnim;
  private Animation<TextureRegion> shielderWalkAnim;
  private Animation<TextureRegion> spikedBallWalkAnim;
  private Animation<TextureRegion> spikedBallChargeAnim;

  private Animation<TextureRegion> p1WalkAnim;
  private Animation<TextureRegion> p2WalkAnim;

  private Animation<TextureRegion> oblivionIdleAnim;
  private Animation<TextureRegion> oblivionWalkAnim;
  private Animation<TextureRegion> oblivionMeleeWindupAnim;
  private Animation<TextureRegion> oblivionMeleeAttackAnim;
  private Animation<TextureRegion> oblivionSpellAnim;
  private Animation<TextureRegion> oblivionTeleportAnim;
  private Animation<TextureRegion> oblivionDeathAnim;

  private final Map<String, Animation<TextureRegion>> builtAnims = new HashMap<>();

  // used for tests
  private String ASSETS_BASE = "";

  private TextureManager() {
    // Lazy loading typically handled in load() if explicitly called, or implicitly
    // on first access?
    // For now, keeping load() separate to control when assets are loaded
    // (GameScreen ctor)
  }

  public static synchronized TextureManager getInstance() {
    if (instance == null) {
      instance = new TextureManager();
    }
    return instance;
  }

  public void load() {
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

    buildAnimIfExists("p1Walk", 16, 23);
    p1WalkAnim = getBuiltAnim("p1Walk");

    buildAnimIfExists("p2Walk", 16, 23);
    p2WalkAnim = getBuiltAnim("p2Walk");

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

    buildAnimIfExists("oblivionDeath", 288, 160);
    oblivionDeathAnim = getBuiltAnim("oblivionDeath");

    // Filtro Pixel Art
    for (Texture t : textures.values()) {
      t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }
  }

  private void detectAssetsBase() {
    if (Gdx.files.internal("images/player.png").exists()) {
      ASSETS_BASE = "";
    } else if (Gdx.files.internal("../assets/images/player.png").exists()) {
      ASSETS_BASE = "../assets/";
    } else {
      ASSETS_BASE = "";
    }
  }

  public Texture get(String name) {
    if (!textures.containsKey(name)) {
      // Protezione contro i crash: se chiedi una texture che non esiste, stampa
      // errore e ridai il
      // player
      System.err.println("ERRORE: Texture mancante -> " + name);
      return textures.get("player");
    }
    return textures.get(name);
  }

  public Texture getEnemyTexture(AbstractEnemy enemy) {
    if (enemy instanceof Ranger) return get("archer");
    if (enemy instanceof SpikedBall) return get("slime");
    if (enemy instanceof Shielder) return get("shielder");
    if (enemy instanceof Oblivion) return get("boss");
    return get("skeleton");
  }

  public TextureRegion getChaserWalkFrame(float stateTime) {
    if (chaserWalkAnim == null) return null;
    return chaserWalkAnim.getKeyFrame(stateTime, true);
  }

  public TextureRegion getRangerWalkFrame(float stateTime) {
    if (rangerWalkAnim == null) return null;
    return rangerWalkAnim.getKeyFrame(stateTime, true);
  }

  public TextureRegion getShielderWalkFrame(float stateTime) {
    if (shielderWalkAnim == null) return null;
    return shielderWalkAnim.getKeyFrame(stateTime, true);
  }

  public TextureRegion getSpikedBallWalkFrame(float stateTime) {
    if (spikedBallWalkAnim == null) return null;
    return spikedBallWalkAnim.getKeyFrame(stateTime, true);
  }

  public TextureRegion getSpikedBallChargeFrame(float stateTime) {
    if (spikedBallChargeAnim == null) return null;
    return spikedBallChargeAnim.getKeyFrame(stateTime, true);
  }

  public TextureRegion getP1WalkFrame(float stateTime) {
    if (p1WalkAnim == null) return null;
    return p1WalkAnim.getKeyFrame(stateTime, true);
  }

  public TextureRegion getP2WalkFrame(float stateTime) {
    if (p2WalkAnim == null) return null;
    return p2WalkAnim.getKeyFrame(stateTime, true);
  }

  public TextureRegion getOblivionIdleFrame(float stateTime) {
    if (oblivionIdleAnim == null) return null;
    return oblivionIdleAnim.getKeyFrame(stateTime, true);
  }

  public TextureRegion getOblivionWalkFrame(float stateTime) {
    if (oblivionWalkAnim == null) return null;
    return oblivionWalkAnim.getKeyFrame(stateTime, true);
  }

  public TextureRegion getOblivionMeleeWindupFrame(float stateTime) {
    if (oblivionMeleeWindupAnim == null) return null;
    return oblivionMeleeWindupAnim.getKeyFrame(stateTime, true);
  }

  public TextureRegion getOblivionMeleeAttackFrame(float stateTime) {
    if (oblivionMeleeAttackAnim == null) return null;
    return oblivionMeleeAttackAnim.getKeyFrame(stateTime, true);
  }

  public TextureRegion getOblivionSpellFrame(float stateTime) {
    if (oblivionSpellAnim == null) return null;
    return oblivionSpellAnim.getKeyFrame(stateTime, true);
  }

  public TextureRegion getOblivionTeleportFrame(float stateTime) {
    if (oblivionTeleportAnim == null) return null;
    return oblivionTeleportAnim.getKeyFrame(stateTime, true);
  }

  public TextureRegion getOblivionDeathFrame(float stateTime) {
    if (oblivionDeathAnim == null) return null;
    return oblivionDeathAnim.getKeyFrame(stateTime, false);
  }

  public void dispose() {
    for (Texture t : textures.values()) t.dispose();
    textures.clear();

    chaserWalkAnim = null;
    rangerWalkAnim = null;
    shielderWalkAnim = null;
    spikedBallWalkAnim = null;
    spikedBallChargeAnim = null;

    p1WalkAnim = null;
    p2WalkAnim = null;

    oblivionIdleAnim = null;
    oblivionWalkAnim = null;
    oblivionMeleeWindupAnim = null;
    oblivionMeleeAttackAnim = null;
    oblivionSpellAnim = null;
    oblivionTeleportAnim = null;
    oblivionDeathAnim = null;

    builtAnims.clear();
  }

  // helper
  private void buildAnimIfExists(String key, int frameW, int frameH) {
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

  private Animation<TextureRegion> getBuiltAnim(String key) {
    return builtAnims.get(key);
  }
}
