package io.github.soulslight.manager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.entities.Projectile;
import io.github.soulslight.utils.GdxTestExtension;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxTestExtension.class)
class ProjectileManagerTest {

  private World world;
  private ProjectileManager manager;

  @BeforeEach
  void setUp() {
    world = new World(new Vector2(0, 0), true);
    manager = new ProjectileManager(world);
  }

  @Test
  void testAddProjectile() {
    Projectile p = mock(Projectile.class);
    manager.addProjectile(p);
    assertEquals(1, manager.getProjectiles().size());
  }

  @Test
  void testProjectileCleanup() {
    Projectile p = new Projectile(world, 0, 0, new Vector2(10, 0));
    manager.addProjectile(p);

    // Override velocità per sicurezza nel test
    p.getBody().setLinearVelocity(10f, 0f);

    // Update
    manager.update(11.0f, new ArrayList<>(), new ArrayList<>());

    assertTrue(manager.getProjectiles().isEmpty());
    assertEquals(0, world.getBodyCount());
  }

  @Test
  void testWallCollisionDetection() {
    BodyDef wallDef = new BodyDef();
    wallDef.position.set(5f, 0);
    wallDef.type = BodyDef.BodyType.StaticBody;
    Body wall = world.createBody(wallDef);
    PolygonShape shape = new PolygonShape();
    shape.setAsBox(0.5f, 10f);
    wall.createFixture(shape, 1.0f);
    shape.dispose();

    Projectile p = new Projectile(world, 3.5f, 0, new Vector2(10f, 0));

    p.getBody().setLinearVelocity(15f, 0f);

    manager.addProjectile(p);

    // Step Fisico
    world.step(0.1f, 6, 2);

    // Update Manager
    manager.update(0.1f, new ArrayList<>(), new ArrayList<>());

    // Verifica
    assertTrue(
        manager.getProjectiles().isEmpty(),
        "Il proiettile deve aver colpito il muro (3.5 -> 5.0 attraverso 4.5)");
  }

  @Test
  void testPlayerHitDetection() {

    Projectile p = new Projectile(world, 2f, 0, new Vector2(10f, 0));
    p.getBody().setLinearVelocity(30f, 0f); // Sovrascriviamo velocità
    manager.addProjectile(p);

    // Player Mock
    Player player = mock(Player.class);
    when(player.getPosition()).thenReturn(new Vector2(5f, 0)); // Sulla traiettoria
    when(player.isDead()).thenReturn(false);
    when(player.isInvincible()).thenReturn(false);
    Body playerBody = mock(Body.class);
    when(player.getBody()).thenReturn(playerBody);

    List<Player> players = new ArrayList<>();
    players.add(player);

    // Step Fisico
    world.step(0.1f, 6, 2);

    // Update
    manager.update(0.1f, players, new ArrayList<>());

    // Verifica collisione
    verify(player).takeDamage(anyFloat());
    assertTrue(manager.getProjectiles().isEmpty(), "Proiettile rimosso dopo colpo");
  }

  @Test
  void testInvinciblePlayerIsNotHit() {
    Projectile p = new Projectile(world, 2f, 0, new Vector2(10f, 0));
    p.getBody().setLinearVelocity(30f, 0f);
    manager.addProjectile(p);

    Player player = mock(Player.class);
    when(player.getPosition()).thenReturn(new Vector2(5f, 0));
    when(player.isInvincible()).thenReturn(true); // INVINCIBILE

    List<Player> players = new ArrayList<>();
    players.add(player);

    world.step(0.1f, 6, 2);

    manager.update(0.1f, players, new ArrayList<>());

    verify(player, never()).takeDamage(anyFloat());
    assertEquals(1, manager.getProjectiles().size(), "Proiettile passa attraverso");
  }
}
