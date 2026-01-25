package io.github.soulslight.manager;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.soulslight.model.enemies.*;
import io.github.soulslight.utils.GdxTestExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(GdxTestExtension.class)
class TextureManagerTest {

  @BeforeAll
  static void init() {
    TextureManager.load();
  }

  @Test
  void chaserWalkFramesHaveExpectedSize() {
    TextureRegion frame = TextureManager.getChaserWalkFrame(0f);
    assertNotNull(frame, "Animazione chaserWalk mancante (file o split errato)");
    assertEquals(16, frame.getRegionWidth(), "Larghezza frame chaser errata");
    assertEquals(23, frame.getRegionHeight(), "Altezza frame chaser errata");
  }

  @Test
  void rangerWalkFramesHaveExpectedSize() {
    TextureRegion frame = TextureManager.getRangerWalkFrame(0f);
    assertNotNull(frame, "Animazione rangerWalk mancante (file o split errato)");
    assertEquals(16, frame.getRegionWidth(), "Larghezza frame ranger errata");
    assertEquals(17, frame.getRegionHeight(), "Altezza frame ranger errata");
  }

  @Test
  void shielderWalkFramesHaveExpectedSize() {
    TextureRegion frame = TextureManager.getShielderWalkFrame(0f);
    assertNotNull(frame, "Animazione shielderWalk mancante (file o split errato)");
    assertEquals(16, frame.getRegionWidth(), "Larghezza frame shielder errata");
    assertEquals(27, frame.getRegionHeight(), "Altezza frame shielder errata");
  }

  @Test
  void spikedBallWalkFramesHaveExpectedSize() {
    TextureRegion frame = TextureManager.getSpikedBallWalkFrame(0f);
    assertNotNull(frame, "Animazione spikedBallWalk mancante (file o split errato)");
    assertEquals(32, frame.getRegionWidth(), "Larghezza frame spiked ball walk errata");
    assertEquals(34, frame.getRegionHeight(), "Altezza frame spiked ball walk errata");
  }

  @Test
  void spikedBallChargeFramesHaveExpectedSize() {
    TextureRegion frame = TextureManager.getSpikedBallChargeFrame(0f);
    assertNotNull(frame, "Animazione spikedBallCharge mancante (file o split errato)");
    assertEquals(32, frame.getRegionWidth(), "Larghezza frame spiked ball charge errata");
    assertEquals(34, frame.getRegionHeight(), "Altezza frame spiked ball charge errata");
  }

  @Test
  void testGetMissingTextureReturnsFallback() {
    // Chiediamo una texture che sicuramente non esiste
    Texture missing = TextureManager.get("non_esiste");
    Texture fallback = TextureManager.get("player");

    assertNotNull(missing, "Il metodo get non deve mai restituire null");
    assertNotNull(fallback, "La texture di fallback (player) deve esistere");

    // Verifichiamo che siano lo stesso oggetto
    assertSame(
        fallback,
        missing,
        "Se manca una texture, deve restituire quella del player per evitare crash");
  }

  @Test
  void testEnemyTextureMapping() {
    // Mockiamo le classi per evitare di dover istanziare Box2D World
    Ranger mockRanger = Mockito.mock(Ranger.class);
    SpikedBall mockBall = Mockito.mock(SpikedBall.class);
    Shielder mockShielder = Mockito.mock(Shielder.class);
    Oblivion mockBoss = Mockito.mock(Oblivion.class);

    assertEquals(
        TextureManager.get("archer"),
        TextureManager.getEnemyTexture(mockRanger),
        "Ranger deve usare texture 'archer'");
    assertEquals(
        TextureManager.get("slime"),
        TextureManager.getEnemyTexture(mockBall),
        "SpikedBall deve usare texture 'slime'");
    assertEquals(
        TextureManager.get("shielder"),
        TextureManager.getEnemyTexture(mockShielder),
        "Shielder deve usare texture 'shielder'");
    assertEquals(
        TextureManager.get("boss"),
        TextureManager.getEnemyTexture(mockBoss),
        "Oblivion deve usare texture 'boss'");
  }

  @Test
  void testTextureFiltersAreNearest() {
    Texture playerTex = TextureManager.get("player");

    assertEquals(
        Texture.TextureFilter.Nearest,
        playerTex.getMinFilter(),
        "Il filtro Minification deve essere Nearest");
    assertEquals(
        Texture.TextureFilter.Nearest,
        playerTex.getMagFilter(),
        "Il filtro Magnification deve essere Nearest");
  }

  @Test
  void testStaticTexturesLoaded() {
    assertNotNull(TextureManager.get("player"));
    assertNotNull(TextureManager.get("skeleton"));
    assertNotNull(TextureManager.get("boss"));
    assertNotNull(TextureManager.get("arrow"));
  }
}
