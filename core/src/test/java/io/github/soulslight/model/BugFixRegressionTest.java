package io.github.soulslight.model;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.enemies.EnemyRegistry;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.inventory.Inventory;
import io.github.soulslight.model.items.DungeonKey;
import io.github.soulslight.model.items.HealthPotion;
import io.github.soulslight.model.items.ItemType;
import io.github.soulslight.utils.GdxTestExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Regressioni per i difetti emersi dall'audit. Ogni test qui dentro fallisce sul codice precedente
 * al fix: servono a impedire che tornino, non a documentare il comportamento.
 */
@ExtendWith(GdxTestExtension.class)
class BugFixRegressionTest {

  @BeforeAll
  static void initBox2D() {
    Box2D.init();
    EnemyRegistry.loadCache();
  }

  private World world() {
    return new World(new Vector2(0, 0), true);
  }

  // ---------- A1 ----------

  @Test
  @DisplayName("A1: setHealth non deve toccare maxHealth")
  void setHealthLasciaIntattoIlMassimo() {
    Player p = new Player(Player.PlayerClass.WARRIOR, world(), 0, 0);
    float max = p.getMaxHealth();

    p.setHealth(30f);

    assertEquals(30f, p.getHealth(), 0.01f, "la vita corrente deve cambiare");
    assertEquals(max, p.getMaxHealth(), 0.01f, "il massimo non deve seguire la vita corrente");
  }

  @Test
  @DisplayName("A1: un nemico ripristinato a vita bassa conserva il proprio massimo")
  void nemicoRipristinatoConservaIlMassimo() {
    AbstractEnemy chaser = EnemyRegistry.getEnemy("Chaser");
    assertNotNull(chaser);
    float max = chaser.getMaxHealth();
    assertTrue(max > 1f, "il prototipo deve avere un massimo sensato");

    chaser.setHealth(1f); // è ciò che fa restoreMemento

    assertEquals(1f, chaser.getHealth(), 0.01f);
    assertEquals(max, chaser.getMaxHealth(), 0.01f, "la barra vita resterebbe piena al 100%");
  }

  // ---------- A2 ----------

  @Test
  @DisplayName("A2: gli HP del Player vengono dalla classe scelta, come mostra la UI")
  void gliHpVengonoDallaClasse() {
    for (Player.PlayerClass type : Player.PlayerClass.values()) {
      Player p = new Player(type, world(), 0, 0);
      assertEquals(
          type.getBaseHP(),
          p.getMaxHealth(),
          0.01f,
          type.name() + ": ClassSelectionScreen mostra getBaseHP(), il gioco deve rispettarlo");
      assertEquals(p.getMaxHealth(), p.getHealth(), 0.01f, type.name() + ": parte a vita piena");
    }
  }

  @Test
  @DisplayName("A2: classi diverse hanno HP diversi")
  void leClassiSiDistinguonoSugliHp() {
    Player warrior = new Player(Player.PlayerClass.WARRIOR, world(), 0, 0);
    Player mage = new Player(Player.PlayerClass.MAGE, world(), 0, 0);

    assertTrue(
        warrior.getMaxHealth() > mage.getMaxHealth(),
        "il Warrior deve reggere piu' colpi del Mage");
  }

  // ---------- A4 ----------

  @Test
  @DisplayName("A4: isFull() distingue davvero pieno da vuoto")
  void isFullRispondeCorrettamente() {
    Inventory inv = new Inventory(2);
    assertFalse(inv.isFull(), "appena creato non e' pieno");

    assertTrue(inv.addItem(new DungeonKey()));
    assertTrue(inv.addItem(new DungeonKey()));

    assertTrue(inv.isFull(), "con tutti gli slot occupati deve dirsi pieno");
  }

  // ---------- A6 ----------

  @Test
  @DisplayName("A6: lo Shielder che si sacrifica notifica i death listener")
  void shielderSacrificatoNotificaLaMorte() {
    AbstractEnemy shielder = EnemyRegistry.getEnemy("Shielder");
    assertNotNull(shielder);
    shielder.createBody(world(), 0, 0);

    final int[] morti = {0};
    shielder.addDeathListener(e -> morti[0]++);

    // Nessun Ranger da proteggere: updateBehavior lo fa suicidare.
    shielder.updateBehavior(java.util.Collections.emptyList(), 0.1f);

    assertTrue(shielder.isDead(), "deve risultare morto");
    assertEquals(1, morti[0], "la Room lo rimuove solo se il listener scatta");
  }

  // ---------- A7 ----------

  @Test
  @DisplayName("A7: dopo setHealth lo stato di morte resta coerente e i danni tornano a passare")
  void statoDiMorteCoerenteDopoSetHealth() {
    Player p = new Player(Player.PlayerClass.WARRIOR, world(), 0, 0);

    p.takeDamage(99999f);
    assertTrue(p.isDead(), "vita a zero significa morto");

    p.setHealth(100f);
    assertFalse(p.isDead(), "rimesso in vita, isDead() deve concordare");

    p.takeDamage(40f);
    assertEquals(60f, p.getHealth(), 0.01f, "takeDamage non deve restare un no-op permanente");
  }

  // ---------- B1 ----------

  @Test
  @DisplayName("B1: una chiave arbitraria nel salvataggio non istanzia nulla")
  void chiaveSconosciutaNonCostruisceNiente() {
    assertNull(ItemType.fromKey("java.lang.Thread"));
    assertNull(ItemType.fromKey("io.github.soulslight.model.GameModel"));
    assertNull(ItemType.fromKey(""));
    assertNull(ItemType.fromKey(null));
  }

  @Test
  @DisplayName("B1: il round-trip dell'inventario resta fedele, anche per i salvataggi vecchi")
  void roundTripInventarioFedele() {
    ItemType potion = ItemType.of(new HealthPotion());
    assertNotNull(potion);
    assertSame(potion, ItemType.fromKey(potion.key()));
    assertInstanceOf(HealthPotion.class, potion.create());

    // I salvataggi precedenti al fix scrivevano il nome di classe completo.
    assertSame(potion, ItemType.fromKey("io.github.soulslight.model.items.HealthPotion"));
  }
}
