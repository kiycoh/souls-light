package io.github.soulslight.manager;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.soulslight.utils.GdxTestExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
}
