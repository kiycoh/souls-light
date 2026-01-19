package io.github.soulslight.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.utils.GdxTestExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxTestExtension.class)
public class CombatTest {
  // Strategy Pattern (GoF)

  @BeforeAll
  public static void setUp() {
    // HeadlessNativesLoader.load(); // Handled by GdxTestExtension
  }

  @Test
  public void testWarriorStats() {
    AttackStrategy strategy = new WarriorAttack(20.0f);
    // SINTASSI JUNIT 5: assertEquals(atteso, attuale, delta, "Messaggio Opzionale")
    assertEquals(45.0f / Constants.PPM, strategy.getRange(), 0.01f, "Il guerriero attacca a corto raggio");
    assertEquals(20.0f, strategy.getDamage(), 0.06f, "Il danno deve essere alto");
    assertEquals(1.0f, strategy.getAttackSpeed(), 0.01f, "La velocità deve essere media");
    assertEquals(
        "sword_swing", strategy.getSoundID(), "Il suono riprodotto deve essere quello della spada");
  }

  @Test
  public void testMageStats() {
    AttackStrategy strategy = new MageAttack(25.0f);
    // Sintassi: (atteso, attuale, tolleranza, messaggio)
    assertEquals(300.0f / Constants.PPM, strategy.getRange(), 0.01f, "Il mago attacca a lungo raggio");
    assertEquals(25.0f, strategy.getDamage(), 0.06f, "Il danno deve essere alto");
    assertEquals(0.5f, strategy.getAttackSpeed(), 0.01f, "La velocità deve essere lenta");
    assertEquals(
        "stick_sound", strategy.getSoundID(), "Il suono riprodotto deve essere quello del bastone");
  }

  @Test
  public void testThiefStats() {
    AttackStrategy strategy = new ThiefAttack(8.0f);
    assertEquals(0.8f, strategy.getRange(), 0.01f, "Il ladro attacca a corto raggio");
    assertEquals(8.0f, strategy.getDamage(), 0.06f, "Il danno deve essere basso");
    assertEquals(2.0f, strategy.getAttackSpeed(), 0.01f, "La velocità deve essere alta");
    assertEquals(
        "dagger_sound",
        strategy.getSoundID(),
        "Il suono riprodotto deve essere quello del pugnale");
  }

  @Test
  public void testArcherStats() {
    AttackStrategy strategy = new ArcherAttack(7.0f);
    assertEquals(250.0f / Constants.PPM, strategy.getRange(), 0.01f, "L'arciere attacca a lungo raggio");
    assertEquals(7.0f, strategy.getDamage(), 0.06f, "Il danno deve essere basso");
    assertEquals(1.5f, strategy.getAttackSpeed(), 0.01f, "La velocità deve essere medio-alta");
    assertEquals(
        "bow_sound", strategy.getSoundID(), "Il suono riprodotto deve essere quello dell'arco");
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
        20.0f,
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
        25.0f, mage.getAttackStrategy().getDamage(), 0.01f, "Il player deve fare i danni del mago");
  }

  @Test
  public void testThiefInitialization() {
    Player thief = new Player(Player.PlayerClass.THIEF, new World(new Vector2(0, 0), true), 0, 0);

    // Controllo il TIPO di strategia (InstanceOf)
    assertInstanceOf(
        ThiefAttack.class,
        thief.getAttackStrategy(),
        "Il player deve avere istanza di ThiefAttack");

    // Controllo i VALORI (AssertEquals)
    assertEquals(
        8.0f,
        thief.getAttackStrategy().getDamage(),
        0.01f,
        "Il player deve fare i danni del ladro");
  }

  @Test
  public void testArcherInitialization() {

    Player archer = new Player(Player.PlayerClass.ARCHER, new World(new Vector2(0, 0), true), 0, 0);

    // Controllo il TIPO di strategia (InstanceOf)
    assertInstanceOf(
        ArcherAttack.class,
        archer.getAttackStrategy(),
        "Il player deve avere istanza di ArcherAttack");

    // Controllo i VALORI (AssertEquals)
    assertEquals(
        7.0f,
        archer.getAttackStrategy().getDamage(),
        0.01f,
        "Il player deve fare i danni dell'arciere");
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
