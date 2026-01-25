package io.github.soulslight.manager;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.soulslight.utils.GdxTestExtension;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxTestExtension.class)
class ResourceManagerTest {

    @BeforeEach
    void resetSingleton() throws Exception {
        Field instance = ResourceManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testSingletonInstance() {
        ResourceManager instance1 = ResourceManager.getInstance();
        assertNotNull(instance1, "L'istanza non deve essere null");

        ResourceManager instance2 = ResourceManager.getInstance();
        assertSame(instance1, instance2, "Deve restituire sempre la stessa istanza (Pattern Singleton)");
    }

    @Test
    void testPlayerTextureFallback() {
        // Verifica che, se manca il file, venga generata la texture procedurale rossa
        ResourceManager rm = ResourceManager.getInstance();
        Texture playerTex = rm.getPlayerTexture();

        assertNotNull(playerTex, "La texture del player non deve essere null");
        assertEquals(16, playerTex.getWidth());
        assertEquals(16, playerTex.getHeight());
    }

    @Test
    void testWallAndFloorGeneration() {
        ResourceManager rm = ResourceManager.getInstance();

        TextureRegion wall = rm.getWallTextureRegion();
        assertNotNull(wall, "La texture del muro non deve essere null");
        assertEquals(32, wall.getRegionWidth(), "Il muro generato deve essere 32x32");

        TextureRegion floor = rm.getFloorTextureRegion();
        assertNotNull(floor, "La texture del pavimento non deve essere null");
        assertEquals(32, floor.getRegionWidth(), "Il pavimento generato deve essere 32x32");
    }

    @Test
    void testFloorVariantsArray() {
        ResourceManager rm = ResourceManager.getInstance();

        TextureRegion[] floors = rm.getFloorTextureRegions();

        assertNotNull(floors, "L'array delle varianti non deve essere null");
        assertEquals(8, floors.length, "Devono esserci esattamente 8 varianti di pavimento");

        for (int i = 0; i < floors.length; i++) {
            assertNotNull(floors[i], "La variante " + i + " non deve essere null");
            assertEquals(32, floors[i].getRegionWidth(), "La variante deve essere scalata a 32 pixel");
        }
    }

    @Test
    void testWallMasksInitialization() {
        ResourceManager rm = ResourceManager.getInstance();

        TextureRegion[] masks = rm.getWallMaskRegions();

        assertNotNull(masks);
        assertEquals(16, masks.length, "Devono esserci 16 maschere per i bitmask (4 bit)");

    }

    @Test
    void testInnerCornersFallback() {
        ResourceManager rm = ResourceManager.getInstance();

        // Questi metodi hanno logica di fallback: se il file manca, ritornano getWallTextureRegion()
        TextureRegion ne = rm.getInnerCornerWallNE();
        TextureRegion nw = rm.getInnerCornerWallNW();
        TextureRegion se = rm.getInnerCornerWallSE();
        TextureRegion sw = rm.getInnerCornerWallSW();

        assertNotNull(ne);
        assertNotNull(nw);
        assertNotNull(se);
        assertNotNull(sw);

        // Verifica dimensionale
        assertEquals(32, ne.getRegionWidth());
    }

    @Test
    void testDisposeNoCrash() {
        ResourceManager rm = ResourceManager.getInstance();

        // Carichiamo qualcosa in memoria per assicurarci che ci sia qualcosa da disporre
        rm.getPlayerTexture();
        rm.getFloorTextureRegions();

        // Verifica che dispose non lanci eccezioni
        assertDoesNotThrow(() -> rm.dispose());
    }
}
