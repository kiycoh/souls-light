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
    TextureManager.getInstance().load();
  }

  @Test
  void chaserWalkFramesHaveExpectedSize() {
    TextureRegion frame = TextureManager.getInstance().getChaserWalkFrame(0f);
    assertNotNull(frame, "Animazione chaserWalk mancante (file o split errato)");
    assertEquals(16, frame.getRegionWidth(), "Larghezza frame chaser errata");
    assertEquals(23, frame.getRegionHeight(), "Altezza frame chaser errata");
  }

  @Test
  void rangerWalkFramesHaveExpectedSize() {
    TextureRegion frame = TextureManager.getInstance().getRangerWalkFrame(0f);
    assertNotNull(frame, "Animazione rangerWalk mancante (file o split errato)");
    assertEquals(16, frame.getRegionWidth(), "Larghezza frame ranger errata");
    assertEquals(17, frame.getRegionHeight(), "Altezza frame ranger errata");
  }

  @Test
  void shielderWalkFramesHaveExpectedSize() {
    TextureRegion frame = TextureManager.getInstance().getShielderWalkFrame(0f);
    assertNotNull(frame, "Animazione shielderWalk mancante (file o split errato)");
    assertEquals(16, frame.getRegionWidth(), "Larghezza frame shielder errata");
    assertEquals(27, frame.getRegionHeight(), "Altezza frame shielder errata");
  }

  @Test
  void spikedBallWalkFramesHaveExpectedSize() {
    TextureRegion frame = TextureManager.getInstance().getSpikedBallWalkFrame(0f);
    assertNotNull(frame, "Animazione spikedBallWalk mancante (file o split errato)");
    assertEquals(32, frame.getRegionWidth(), "Larghezza frame spiked ball walk errata");
    assertEquals(34, frame.getRegionHeight(), "Altezza frame spiked ball walk errata");
  }

  @Test
  void spikedBallChargeFramesHaveExpectedSize() {
    TextureRegion frame = TextureManager.getInstance().getSpikedBallChargeFrame(0f);
    assertNotNull(frame, "Animazione spikedBallCharge mancante (file o split errato)");
    assertEquals(32, frame.getRegionWidth(), "Larghezza frame spiked ball charge errata");
    assertEquals(34, frame.getRegionHeight(), "Altezza frame spiked ball charge errata");
  }

  @Test
  void testGetMissingTextureReturnsFallback() {
    // Chiediamo una texture che sicuramente non esiste
    Texture missing = TextureManager.getInstance().get("non_esiste");
    Texture fallback = TextureManager.getInstance().get("player");

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
        TextureManager.getInstance().get("archer"),
        TextureManager.getInstance().getEnemyTexture(mockRanger),
        "Ranger deve usare texture 'archer'");
    assertEquals(
        TextureManager.getInstance().get("slime"),
        TextureManager.getInstance().getEnemyTexture(mockBall),
        "SpikedBall deve usare texture 'slime'");
    assertEquals(
        TextureManager.getInstance().get("shielder"),
        TextureManager.getInstance().getEnemyTexture(mockShielder),
        "Shielder deve usare texture 'shielder'");
    assertEquals(
        TextureManager.getInstance().get("boss"),
        TextureManager.getInstance().getEnemyTexture(mockBoss),
        "Oblivion deve usare texture 'boss'");
  }

  @Test
  void testTextureFiltersAreNearest() {
    Texture playerTex = TextureManager.getInstance().get("player");

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
    assertNotNull(TextureManager.getInstance().get("player"));
    assertNotNull(TextureManager.getInstance().get("skeleton"));
    assertNotNull(TextureManager.getInstance().get("boss"));
    assertNotNull(TextureManager.getInstance().get("arrow"));
  }
}
