package io.github.soulslight.model;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.*;


public class EnemyRegistry {
    private static HashMap<String, AbstractEnemy> cache = new HashMap<>();

    private static Texture spriteSheet;

    public static void loadCache(){
        spriteSheet = new Texture("placeHolder.png");

        Chaser chaser = new Chaser();
        chaser.setHP(100);

        chaser.setTextureRegion(new TextureRegion(spriteSheet, 0, 0, 32, 32));
        cache.put("Chaser", chaser);
    }

    public static AbstractEnemy getEnemy(String type){
        AbstractEnemy prototype = cache.get(type);
        return( prototype!= null) ? prototype.clone() : null;
    }

}
