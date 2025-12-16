package io.github.soulslight.model;
import org.junit.jupiter.api.Test;

import static io.github.soulslight.model.Player.PlayerClass.*;
import static org.junit.jupiter.api.Assertions.*;

public class CombatTest {
    // Strategy Pattern (GoF)

    @Test
    public void testWarriorStats() {
        AttackStrategy strategy = new WarriorAttack();
        // SINTASSI JUNIT 5: assertEquals(atteso, attuale, delta, "Messaggio Opzionale")
        assertEquals(1.0f, strategy.getRange(), 0.01f, "Il guerriero attacca a corto raggio");
        assertEquals(20.0f, strategy.getDamage(), 0.06f, "Il danno deve essere alto");
        assertEquals(1.0f, strategy.getAttackSpeed(), 0.01f, "La velocità deve essere media");
        assertEquals("sword_swing", strategy.getSoundID(), "Il suono riprodotto deve essere quello della spada");
    }

    @Test
    public void testMageStats() {
        AttackStrategy strategy = new MageAttack();
        // Sintassi: (atteso, attuale, tolleranza, messaggio)
        assertEquals(13.0f, strategy.getRange(), 0.01f, "Il mago attacca a lungo raggio");
        assertEquals(25.0f, strategy.getDamage(), 0.06f, "Il danno deve essere alto");
        assertEquals(0.5f, strategy.getAttackSpeed(), 0.01f, "La velocità deve essere lenta");
        assertEquals("stick_sound", strategy.getSoundID(), "Il suono riprodotto deve essere quello del bastone");
    }

    @Test
    public void testThiefStats() {
        AttackStrategy strategy = new ThiefAttack();
        assertEquals(0.8f, strategy.getRange(), 0.01f, "Il ladro attacca a corto raggio");
        assertEquals(8.0f, strategy.getDamage(), 0.06f, "Il danno deve essere basso");
        assertEquals(2.0f, strategy.getAttackSpeed(), 0.01f, "La velocità deve essere alta");
        assertEquals("dagger_sound", strategy.getSoundID(), "Il suono riprodotto deve essere quello del pugnale");
    }

    @Test
    public void testArcherStats() {
        AttackStrategy strategy = new ArcherAttack();
        assertEquals(10.0f, strategy.getRange(), 0.01f, "L'arciere attacca a lungo raggio");
        assertEquals(7.0f, strategy.getDamage(), 0.06f, "Il danno deve essere basso");
        assertEquals(1.5f, strategy.getAttackSpeed(), 0.01f, "La velocità deve essere medio-alta");
        assertEquals("bow_sound", strategy.getSoundID(), "Il suono riprodotto deve essere quello dell'arco");
    }

    @Test
    public void testWarriorInitialization() {
        // 1. Creo il player usando l'Enum (come abbiamo fatto prima)
        Player warrior = new Player(Player.PlayerClass.WARRIOR);

        // 2. Controllo il TIPO di strategia (InstanceOf)
        // Sintassi: (ClasseAttesa.class, OggettoDaTestare, "Messaggio opzionale")
        assertInstanceOf(WarriorAttack.class, WARRIOR.getStrategy(),
            "Il player deve avere istanza di WarriorAttack");

        // 3. Controllo i VALORI (AssertEquals)
        // Sintassi: (ValoreAtteso, ValoreReale, Delta, "Messaggio opzionale")
        assertEquals(20.0f, WARRIOR.getStrategy().getDamage(), 0.01f,
            "Il player deve fare i danni del guerriero");
    }

    @Test
    public void testMageInitialization() {

        Player mage = new Player(Player.PlayerClass.MAGE);

        // 2. Controllo il TIPO di strategia (InstanceOf)
        // Sintassi: (ClasseAttesa.class, OggettoDaTestare, "Messaggio opzionale")
        assertInstanceOf(MageAttack.class, MAGE.getStrategy(),
            "Il player deve avere istanza di MageAttack");

        // 3. Controllo i VALORI (AssertEquals)
        // Sintassi: (ValoreAtteso, ValoreReale, Delta, "Messaggio opzionale")
        assertEquals(25.0f, MAGE.getStrategy().getDamage(), 0.01f,
            "Il player deve fare i danni del mago");
    }


    @Test
    public void testThiefInitialization() {
        Player mage = new Player(Player.PlayerClass.THIEF);

        // 2. Controllo il TIPO di strategia (InstanceOf)
        // Sintassi: (ClasseAttesa.class, OggettoDaTestare, "Messaggio opzionale")
        assertInstanceOf(ThiefAttack.class, THIEF.getStrategy(),
            "Il player deve avere istanza di ThiefAttack");

        // 3. Controllo i VALORI (AssertEquals)
        // Sintassi: (ValoreAtteso, ValoreReale, Delta, "Messaggio opzionale")
        assertEquals(8.0f, THIEF.getStrategy().getDamage(), 0.01f,
            "Il player deve fare i danni del ladro");
    }


    @Test
    public void testArcherInitialization() {

        Player mage = new Player(Player.PlayerClass.ARCHER);

        // 2. Controllo il TIPO di strategia (InstanceOf)
        // Sintassi: (ClasseAttesa.class, OggettoDaTestare, "Messaggio opzionale")
        assertInstanceOf(ArcherAttack.class, ARCHER.getStrategy(),
            "Il player deve avere istanza di ArcherAttack");

        // 3. Controllo i VALORI (AssertEquals)
        // Sintassi: (ValoreAtteso, ValoreReale, Delta, "Messaggio opzionale")
        assertEquals(7.0f, ARCHER.getStrategy().getDamage(), 0.01f,
            "Il player deve fare i danni dell'arciere");
    }





   /* @Test
    public void testDoAnAttack(){ //DA VERIFICARE AL COMPLETAMENTO DELLA CLASSE PLAYER
        Player warrior2 = new Player(new WarriorAttack());
        float damage = warrior2.doAnAttack();

        assertEquals(20.0f, damage, 0.01f, "Il guerriero deve infliggere il danno definito dalla sua strategia");

    }*/

  /*  @Test
    public void testDoAnAttackWithoutStrategy(){ //DA VERIFICARE AL COMPLETAMENTO DELLA CLASSE PLAYER
        Player player = new Player();

        // ASSUME CHE .doAnAttack() lanci eccezione
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> player.doAnAttack(),
            "Attaccare senza strategia deve lanciare un'eccezione"
        );

        assertEquals(
            "Attack strategy not set",
            exception.getMessage(),
            "Il messaggio dell'eccezione deve essere chiaro"
        );
    }*/
}
