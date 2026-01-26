package io.github.soulslight.model.enemies;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import java.util.HashMap;

public class EnemyRegistry {
  private static final HashMap<String, AbstractEnemy> cache = new HashMap<>();

  // Metodo per caricare un'istanza sola per ciascun nemico e poi poter clonare
  public static void loadCache(TextureAtlas atlas) {

    // Creiamo i nemici
    Chaser chaser = new Chaser();
    chaser.setHealth(100);

    Ranger ranger = new Ranger();
    ranger.setHealth(70);

    SpikedBall spikedBall = new SpikedBall();
    spikedBall.setHealth(500);

    Shielder shielder = new Shielder();
    shielder.setHealth(250);

    Oblivion oblivion = new Oblivion();

    // Assegnamo la grafica
    if (atlas != null) {
      // findRegion cerca il nome del file nell'atlas
      chaser.setTextureRegion(atlas.findRegion("skeleton"));
      ranger.setTextureRegion(atlas.findRegion("archer"));
      spikedBall.setTextureRegion(atlas.findRegion("slime"));
      shielder.setTextureRegion(atlas.findRegion("shielder"));
      oblivion.setTextureRegion(atlas.findRegion("boss_oblivion"));
    }

    // Aggiungiamo alla cache
    cache.put("Chaser", chaser);
    cache.put("Ranger", ranger);
    cache.put("SpikedBall", spikedBall);
    cache.put("Shielder", shielder);
    cache.put("Oblivion", oblivion);
  }

  // Prende il tipo del nemico dalla cache
  public static AbstractEnemy getEnemy(String type) {
    AbstractEnemy prototype = cache.get(type);
    return (prototype != null) ? prototype.clone() : null;
  }
}
