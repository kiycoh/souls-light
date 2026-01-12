package io.github.soulslight.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SettingsManager {

    private static final String PREF_NAME = "soulslight-settings";
    private static final String KEY_AUTO_AIM = "auto_aim";
    private static final String KEY_MUSIC_VOL = "music_volume";
    private static final String KEY_FULLSCREEN = "fullscreen";

    private static SettingsManager instance;
    private final Preferences preferences;

    private SettingsManager() {
        this.preferences = Gdx.app.getPreferences(PREF_NAME);
    }

    public static synchronized SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    public boolean isAutoAimEnabled() {
        return preferences.getBoolean(KEY_AUTO_AIM, false);
    }

    public void setAutoAimEnabled(boolean enabled) {
        preferences.putBoolean(KEY_AUTO_AIM, enabled);
        preferences.flush();
    }

    public float getMusicVolume() {
        return preferences.getFloat(KEY_MUSIC_VOL, 0.5f);
    }

    public void setMusicVolume(float volume) {
        // Clamp between 0.0 and 1.0
        float v = Math.max(0f, Math.min(1f, volume));
        preferences.putFloat(KEY_MUSIC_VOL, v);
        preferences.flush();
    }

    public boolean isFullscreen() {
        return preferences.getBoolean(KEY_FULLSCREEN, false);
    }

    public void setFullscreen(boolean fullscreen) {
        preferences.putBoolean(KEY_FULLSCREEN, fullscreen);
        preferences.flush();
        
        if (fullscreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(1280, 720);
        }
    }
}
