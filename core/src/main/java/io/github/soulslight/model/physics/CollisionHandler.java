package io.github.soulslight.model.physics;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.model.Constants;

public class CollisionHandler {

  public static void createMapBodies(World world, TiledMap map) {
    TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
    if (layer == null) return;

    float tileSize = layer.getTileWidth(); // Assuming square tiles
    float scaledSize = tileSize / Constants.PPM;

    for (int row = 0; row < layer.getHeight(); row++) {
      for (int col = 0; col < layer.getWidth(); col++) {
        TiledMapTileLayer.Cell cell = layer.getCell(col, row);

        if (cell != null
            && cell.getTile() != null
            && cell.getTile().getProperties().containsKey("type")) {
          String type = (String) cell.getTile().getProperties().get("type");

          if ("wall".equals(type)) {
            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;
            // Center of the tile
            float x = (col * tileSize) + (tileSize / 2);
            float y = (row * tileSize) + (tileSize / 2);

            bdef.position.set(x / Constants.PPM, y / Constants.PPM);

            Body body = world.createBody(bdef);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(scaledSize / 2, scaledSize / 2);

            FixtureDef fdef = new FixtureDef();
            fdef.shape = shape;
            fdef.filter.categoryBits = Constants.BIT_WALL;
            fdef.filter.maskBits = Constants.BIT_PLAYER | Constants.BIT_ENEMY;

            body.createFixture(fdef);
            shape.dispose();
          }
        }
      }
    }
  }
}
