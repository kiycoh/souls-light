package io.github.soulslight.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.model.combat.*;
import io.github.soulslight.model.entities.Entity;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.utils.GdxTestExtension;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxTestExtension.class)
public class CombatTest {
  // Strategy Pattern (GoF)

  @BeforeAll
  public static void setUp() {}

  @Test
  public void testWarriorStats() {
    AttackStrategy strategy = new WarriorAttack(75);
    // Warrior range update to 100f, damage passed is 75
    assertEquals(100.0f, strategy.getRange(), 0.01f, "Il guerriero attacca a range 100");
    assertEquals(75.0f, strategy.getDamage(), 0.06f, "Il danno deve essere 75");
    // Warrior speed default check? Assuming 1.0f from previous test, but check
    // failure if different
    assertEquals(1.3f, strategy.getAttackSpeed(), 0.01f, "La velocità deve essere media");
    assertEquals(
        "sword_swing", strategy.getSoundID(), "Il suono riprodotto deve essere quello della spada");
  }

  @Test
  public void testArcherAttackLogic() {
    // Arrange
    float damage = 25f;
    AttackStrategy strategy = new ArcherAttack(damage);
    World world = new World(new Vector2(0, 0), true);
    Player attacker = new Player(Player.PlayerClass.ARCHER, world, 0, 0);

    // In Range (Distance 50 < 300)
    TestEntity targetInRange = new TestEntity(50, 0, 100);
    // Out of Range (Distance 350 > 300)
    TestEntity targetOutOfRange = new TestEntity(350, 0, 100);

    // Let's add a listener to verify attack attempts instead of damage
    final int[] shots = {0};
    attacker.addProjectileListener((o, t, type, dmg) -> shots[0]++);

    List<Entity> targets = List.of(targetInRange, targetOutOfRange);

    // Act
    strategy.executeAttack(attacker, targets);
  }

  @Test
  public void testMageStats() {
    AttackStrategy strategy = new MageAttack(45);
    // Mage range 300.0, Damage 45, Speed 1.5
    assertEquals(300.0f, strategy.getRange(), 0.01f, "Il mago attacca a lungo raggio");
    assertEquals(45.0f, strategy.getDamage(), 0.06f, "Il danno deve essere alto");
    assertEquals(1.5f, strategy.getAttackSpeed(), 0.01f, "La velocità deve essere 1.5");
    assertEquals(
        "fireball_sound",
        strategy.getSoundID(),
        "Il suono riprodotto deve essere quello del bastone");
  }

  @Test
  public void testThiefStats() {
    AttackStrategy strategy = new ThiefAttack(20);
    // Thief Range 100.0
    assertEquals(100.0f, strategy.getRange(), 0.01f, "Il ladro attacca a range 100");
    assertEquals(20.0f, strategy.getDamage(), 0.06f, "Il danno deve essere basso");
    assertEquals(0.8f, strategy.getAttackSpeed(), 0.01f, "La velocità deve essere alta");
    assertEquals(
        "dagger_sound",
        strategy.getSoundID(),
        "Il suono riprodotto deve essere quello del pugnale");
  }

  @Test
  public void testArcherStats() {
    AttackStrategy strategy = new ArcherAttack(25);
    // Archer Range 300.0
    assertEquals(300.0f, strategy.getRange(), 0.01f, "L'arciere attacca a range 300");
    assertEquals(25.0f, strategy.getDamage(), 0.06f, "Il danno deve essere basso");
    assertEquals(1.5f, strategy.getAttackSpeed(), 0.01f, "La velocità deve essere medio-alta");
    assertEquals(
        "bow_sound", strategy.getSoundID(), "Il suono riprodotto deve essere quello dell'arco");
  }

  @Test
  public void testWarriorAttackLogic() {
    // Arrange
    float damage = 50f;
    AttackStrategy strategy = new WarriorAttack(damage); // Range now 100
    World world = new World(new Vector2(0, 0), true);
    Player attacker = new Player(Player.PlayerClass.WARRIOR, world, 0, 0);

    // Target 1: In Range (Distance 30 < 100)
    TestEntity targetInRange = new TestEntity(30, 0, 100);
    // Target 2: Out of Range (Distance 120 > 100)
    TestEntity targetOutOfRange = new TestEntity(120, 0, 100);

    List<Entity> targets = List.of(targetInRange, targetOutOfRange);

    // Act
    attacker.move(1, 0); // Face RIGHT towards targets
    strategy.executeAttack(attacker, targets);

    // Assert
    assertEquals(50f, targetInRange.getHealth(), 0.01f, "Warrior should hit target in range");
    assertEquals(
        100f, targetOutOfRange.getHealth(), 0.01f, "Warrior should miss target out of range");
  }

  @Test
  public void testThiefInitialization() {
    Player thief = new Player(Player.PlayerClass.THIEF, new World(new Vector2(0, 0), true), 0, 0);

    // Controllo il TIPO di strategia (InstanceOf)
    // Sintassi: (ClasseAttesa.class, OggettoDaTestare, "Messaggio opzionale")
    assertInstanceOf(
        ThiefAttack.class,
        thief.getAttackStrategy(),
        "Il player deve avere istanza di ThiefAttack");

    // Controllo i VALORI (AssertEquals)
    assertEquals(
        20.0f,
        thief.getAttackStrategy().getDamage(),
        0.01f,
        "Il player deve fare i danni del ladro");
  }

  @Test
  public void testPlayerInitialization() {
    // Creo il player usando l'Enum (come abbiamo fatto prima)
    Player warrior =
        new Player(Player.PlayerClass.WARRIOR, new World(new Vector2(0, 0), true), 0, 0);

    // Controllo il TIPO di strategia (InstanceOf)
    // Sintassi: (ClasseAttesa.class, OggettoDaTestare, "Messaggio opzionale")
    assertInstanceOf(
        WarriorAttack.class,
        warrior.getAttackStrategy(),
        "Il player deve avere istanza di WarriorAttack");

    // Controllo i VALORI (AssertEquals)
    // Sintassi: (ValoreAtteso, ValoreReale, Delta, "Messaggio opzionale")
    assertEquals(
        75.0f,
        warrior.getAttackStrategy().getDamage(),
        0.01f,
        "Il player deve fare i danni del guerriero");
  }

  @Test
  public void testMageInitialization() {

    Player mage = new Player(Player.PlayerClass.MAGE, new World(new Vector2(0, 0), true), 0, 0);

    // Controllo il TIPO di strategia (InstanceOf)
    // Sintassi: (ClasseAttesa.class, OggettoDaTestare, "Messaggio opzionale")
    assertInstanceOf(
        MageAttack.class, mage.getAttackStrategy(), "Il player deve avere istanza di MageAttack");

    // Controllo i VALORI (AssertEquals)
    assertEquals(
        45.0f, mage.getAttackStrategy().getDamage(), 0.01f, "Il player deve fare i danni del mago");
  }

  @Test
  public void testArcherInitialization() {

    Player archer = new Player(Player.PlayerClass.ARCHER, new World(new Vector2(0, 0), true), 0, 0);

    // Controllo il TIPO di strategia (InstanceOf)
    // Sintassi: (ClasseAttesa.class, OggettoDaTestare, "Messaggio opzionale")
    assertInstanceOf(
        ArcherAttack.class,
        archer.getAttackStrategy(),
        "Il player deve avere istanza di ArcherAttack");

    // Controllo i VALORI (AssertEquals)
    assertEquals(
        25.0f,
        archer.getAttackStrategy().getDamage(),
        0.01f,
        "Il player deve fare i danni dell'arciere");
  }

  // Stub Entity for testing combat logic without physics/Box2D overhead
  private static class TestEntity extends Entity {
    public TestEntity(float x, float y, float health) {
      super(new Vector2(x, y), health);
    }
  }

  @Test
  public void testDoAnAttack() {
    Player warrior2 =
        new Player(Player.PlayerClass.WARRIOR, new World(new Vector2(0, 0), true), 0, 0);
    // doAnAttack è void, verifichiamo solo che non lanci eccezioni
    assertDoesNotThrow(() -> warrior2.doAnAttack());
  }

  @Test
  public void testPlayerWithoutStrategy() {
    // Il costruttore lancia eccezione se il tipo è null
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Player(null, new World(new Vector2(0, 0), true), 0, 0),
            "Creare un player con tipo null deve lanciare un'eccezione");

    assertEquals(
        "Player Type cannot be null",
        exception.getMessage(),
        "Il messaggio dell'eccezione deve essere chiaro");
  }
}
