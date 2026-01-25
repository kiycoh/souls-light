package io.github.soulslight.manager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Graphics.DisplayMode;
import io.github.soulslight.utils.GdxTestExtension;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxTestExtension.class)
class SettingManagerTest {

    @BeforeEach
    void setUp() throws Exception {
        Field instance = SettingsManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);

        // Pulizia delle preferenze salvate in memoria
        // assicura che ogni test parta con valori di default
        Gdx.app.getPreferences("soulslight-settings").clear();
    }

    @Test
    void testSingletonInstance() {
        SettingsManager i1 = SettingsManager.getInstance();
        assertNotNull(i1);
        SettingsManager i2 = SettingsManager.getInstance();
        assertSame(i1, i2, "Deve restituire la stessa istanza");
    }

    @Test
    void testAutoAimLogic() {
        SettingsManager settings = SettingsManager.getInstance();

        // Default dovrebbe essere false
        assertFalse(settings.isAutoAimEnabled(), "Default AutoAim deve essere false");

        // Cambio valore
        settings.setAutoAimEnabled(true);
        assertTrue(settings.isAutoAimEnabled());

        settings.setAutoAimEnabled(false);
        assertFalse(settings.isAutoAimEnabled());
    }

    @Test
    void testMusicVolumeClamping() {
        SettingsManager settings = SettingsManager.getInstance();

        // Default 0.5f
        assertEquals(0.5f, settings.getMusicVolume(), 0.01f);

        // Test valore valido
        settings.setMusicVolume(0.8f);
        assertEquals(0.8f, settings.getMusicVolume(), 0.01f);

        // Test Upper Bound ( > 1.0 )
        settings.setMusicVolume(1.5f);
        assertEquals(1.0f, settings.getMusicVolume(), 0.01f, "Il volume deve essere limitato a 1.0");

        // Test Lower Bound ( < 0.0 )
        settings.setMusicVolume(-0.5f);
        assertEquals(0.0f, settings.getMusicVolume(), 0.01f, "Il volume deve essere limitato a 0.0");
    }

    @Test
    void testFullscreenToggle() {
        Graphics mockGraphics = mock(Graphics.class);
        Graphics originalGraphics = Gdx.graphics; // Salviamo l'originale
        Gdx.graphics = mockGraphics;

        try {
            SettingsManager settings = SettingsManager.getInstance();

            // Default false
            assertFalse(settings.isFullscreen());

            // Attiva Fullscreen
            // Mockiamo getDisplayMode per evitare NullPointerException
            DisplayMode mockMode = mock(DisplayMode.class);
            when(mockGraphics.getDisplayMode()).thenReturn(mockMode);

            settings.setFullscreen(true);

            assertTrue(settings.isFullscreen(), "La preferenza deve essere salvata come true");
            // Verifica che sia stato chiamato il metodo di LibGDX
            verify(mockGraphics).setFullscreenMode(mockMode);

            //Disattiva Fullscreen
            settings.setFullscreen(false);

            assertFalse(settings.isFullscreen(), "La preferenza deve essere salvata come false");
            // Verifica che sia stato chiamato il metodo per la finestra (1280x720 come da codice)
            verify(mockGraphics).setWindowedMode(1280, 720);

        } finally {
            // Ripristina graphics originale per non rompere altri test
            Gdx.graphics = originalGraphics;
        }
    }
}
