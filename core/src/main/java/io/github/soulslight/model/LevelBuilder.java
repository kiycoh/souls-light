package io.github.soulslight.model;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public class LevelBuilder {
    private Level level;

    public LevelBuilder() {
        this.level = new Level();
    }

    public LevelBuilder buildMap(TiledMap map) {
        level.setMap(map);
        return this;
    }

    // --- 1. GENERAZIONE NEMICI (SPAWN) ---
    public LevelBuilder spawnFromTiled(EnemyFactory factory, World world) {

        // Calcolo dimensioni mappa per il Boss
        MapProperties prop = level.getMap().getProperties();
        int mapW = prop.get("width", Integer.class);
        int mapH = prop.get("height", Integer.class);
        int tileW = prop.get("tilewidth", Integer.class);
        int tileH = prop.get("tileheight", Integer.class);

        float totalMapWidth = mapW * tileW;
        float totalMapHeight = mapH * tileH;

        MapLayer layer = level.getMap().getLayers().get("Livello di oggetti 1");

        if (layer == null) {
            System.out.println("WARNING: Layer 'Livello di oggetti 1' non trovato!");
            return this;
        }

        for (MapObject object : layer.getObjects()) {
            float x = 0;
            float y = 0;

            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                x = rect.x;
                y = rect.y;
            } else {
                x = object.getProperties().get("x", Float.class);
                y = object.getProperties().get("y", Float.class);
            }

            String type = object.getProperties().get("enemyType", "melee", String.class);
            AbstractEnemy enemy = null;

            switch (type.toLowerCase()) {
                case "ranger":
                case "archer":
                    enemy = factory.createRanged();
                    break;
                case "tank":
                case "shielder":
                    enemy = factory.createTank();
                    break;
                case "ball":
                case "trap":
                    enemy = factory.createBall();
                    break;
                case "boss":
                case "oblivion":
                    enemy = factory.createBoss();
                    break;
                case "melee":
                case "chaser":
                default:
                    enemy = factory.createMelee();
                    break;
            }

            if (enemy != null) {
                enemy.createBody(world, x, y);
                enemy.setSpawnPoint(x, y);

                if (enemy instanceof Oblivion) {
                    ((Oblivion) enemy).setMapBounds(totalMapWidth, totalMapHeight);
                }
                level.addEnemy(enemy);
            }
        }
        return this;
    }

    // --- 2. GENERAZIONE FISICA MURI (NUOVO) ---
    public LevelBuilder buildPhysicsFromMap(World world) {
        // Qui chiamiamo il metodo privato passando il 'world' ricevuto
        createCollisionFromProperties(world);
        return this;
    }

    // Metodo privato estratto (fuori da buildPhysicsFromMap!)
    private void createCollisionFromProperties(World world) {
        // Prende il layer 0 (dove ci sono i muri)
        TiledMapTileLayer layer = (TiledMapTileLayer) level.getMap().getLayers().get(0);
        float tileSize = layer.getTileWidth();

        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);

                if (cell != null && cell.getTile() != null) {
                    boolean isWall = cell.getTile().getProperties().get("isWall", false, Boolean.class);

                    if (isWall) {
                        // Passiamo 'world' al metodo che crea il corpo
                        createWallBody(world, x * tileSize, y * tileSize, tileSize);
                    }
                }
            }
        }
    }

    // Metodo privato estratto
    private void createWallBody(World world, float x, float y, float size) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(x + size / 2, y + size / 2);
        bdef.type = BodyDef.BodyType.StaticBody;

        Body body = world.createBody(bdef); // Usa il parametro 'world', non 'physicsWorld'
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size / 2, size / 2);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0f;

        body.createFixture(fdef);
        shape.dispose();
    }

    // --- 3. SETTAGGI AMBIENTE ---
    public LevelBuilder setEnvironment(String musicTrack, float lightLevel) {
        level.setMusicTrack(musicTrack);
        level.setAmbientLight(lightLevel);
        return this;
    }

    public Level build() {
        return level;
    }
}
