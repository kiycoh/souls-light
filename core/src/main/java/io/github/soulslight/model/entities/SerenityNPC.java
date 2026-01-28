package io.github.soulslight.model.entities;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

// Task said extends Entity. But rooms hold AbstractEnemy list.
// If I extend Entity, I cannot add it to room.enemies list.
// However, Room has enemies list. Does it have generic entities list?
// No. Room has `private final List<AbstractEnemy> enemies;`
//
// If SerenityNPC is friendly, it shouldn't be an AbstractEnemy.
// SerenityEvent might manage the NPC separately, or I might need to make it an AbstractEnemy but
// with friendly AI/Team.
// But the plan said "SerenityNPC extends Entity".
// Let's stick to the plan. SerenityEvent will manage the NPC instance.
// The EventRoom doesn't necessarily need to put it in the "enemies" list.
// The SerenityEvent can update/render the NPC.

public class SerenityNPC extends Entity {

  private boolean interacted;

  public SerenityNPC(float x, float y) {
    super();
    this.position.set(x, y);
    this.interacted = false;
    // Placeholder: Health doesn't matter much for invulnerable NPC
    this.health = 100;
    this.maxHealth = 100;
  }

  public void initPhysics(World world) {
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.StaticBody; // NPC doesn't move
    bodyDef.position.set(position);
    this.body = world.createBody(bodyDef);

    CircleShape shape = new CircleShape();
    shape.setRadius(0.5f); // Small size

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = shape;
    fixtureDef.isSensor = true; // Sensor so player can walk through/overlap for interaction

    this.body.createFixture(fixtureDef);
    shape.dispose();
  }

  public void interact(Player player) {
    if (!interacted) {
      // Heal player to max
      player.setHealth(player.getMaxHealth());
      interacted = true;
      System.out.println("SerenityNPC: Player healed!");
    }
  }

  public boolean hasInteracted() {
    return interacted;
  }

  public void reset() {
    interacted = false;
  }
}
