package io.github.soulslight.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Singleton manager for handling music and sound effects. Centralizes audio resource management and
 * volume control.
 */
public class AudioManager {

  private static AudioManager instance;

  private Music currentMusic;
  private String currentMusicPath;

  // Cache for sound effects to avoid repeated loading (optional, but good
  // practice)
  // For now, we'll keep it simple and load/dispose as requested or rely on
  // AssetManager later.
  // Given current codebase uses direct Gdx.audio references, we will mimic that
  // but centralized.

  private AudioManager() {}

  public static synchronized AudioManager getInstance() {
    if (instance == null) {
      instance = new AudioManager();
    }
    return instance;
  }

  /**
   * Plays music from the specified path. If the requested music is already playing, it continues
   * unless forceRestart is true. Stops any other currently playing music.
   */
  private enum FadeState {
    NONE,
    FADING_IN,
    FADING_OUT
  }

  private FadeState currentFadeState = FadeState.NONE;
  private final float DEFAULT_FADE_DURATION = 2.0f;
  private float currentMusicFadeDuration = DEFAULT_FADE_DURATION;
  private float nextFadeDuration = -1f;
  private float currentVolume = 0f;

  private String nextMusicPath;
  private boolean nextMusicLoop;

  /** Sets the duration for the NEXT fade operation (in or out). Resets to default after use. */
  public void setNextFadeDuration(float duration) {
    this.nextFadeDuration = duration;
  }

  private float consumeFadeDuration() {
    if (nextFadeDuration > 0) {
      float d = nextFadeDuration;
      nextFadeDuration = -1f;
      return d;
    }
    return DEFAULT_FADE_DURATION;
  }

  /** Plays music with a crossfade/fade transition. */
  public void playMusic(String path, boolean loop, boolean forceRestart) {
    if (!forceRestart
        && currentMusicPath != null
        && currentMusicPath.equals(path)
        && currentMusic != null
        && currentMusic.isPlaying()
        && currentFadeState != FadeState.FADING_OUT) {
      return;
    }

    // If something is playing, fade it out first, then load next
    if (currentMusic != null && currentMusic.isPlaying()) {
      if (currentFadeState != FadeState.FADING_OUT) {
        currentFadeState = FadeState.FADING_OUT;
        currentMusicFadeDuration = consumeFadeDuration();
        nextMusicPath = path;
        nextMusicLoop = loop;
      } else {
        // Already fading out, just update the next track
        nextMusicPath = path;
        nextMusicLoop = loop;
      }
    } else {
      // Nothing playing, start immediately with fade in
      startMusicInternal(path, loop);
    }
  }

  public void playMusic(String path, boolean loop) {
    playMusic(path, loop, false);
  }

  /** Plays music immediately, skipping any fade out/in. */
  public void playMusicImmediate(String path, boolean loop) {
    stopMusicImmediate();
    startMusicInternal(path, loop);
    // Force volume to target immediately
    if (currentMusic != null) {
      currentVolume = SettingsManager.getInstance().getMusicVolume();
      currentMusic.setVolume(currentVolume);
      currentFadeState = FadeState.NONE;
    }
  }

  private void startMusicInternal(String path, boolean loop) {
    stopMusicImmediate(); // Ensure clean slate

    try {
      currentMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
      currentMusicPath = path;
      currentMusic.setLooping(loop);

      // Start at 0 for fade in
      currentVolume = 0f;
      currentMusic.setVolume(0f);
      currentMusic.play();

      // If we are starting fresh, we also use the consumed duration (if set for this
      // play)
      // But wait, playMusic might have consumed it for FADE_OUT.
      // If playMusic triggered FADE_OUT, we used it.
      // Then update() finishes fade out and calls startMusicInternal again.
      // Should we re-use the same duration for fade in?
      // Usually symmetric.
      // We can store it in a temp, but here startMusicInternal is called from
      // update() or direct.
      // If direct, we should consume.
      // If from update(), we might want to respect the fade in duration?
      // Let's use DEFAULT for fade in, typically acceptable, or reuse.
      // Simplified: Use currentMusicFadeDuration which persists?
      // currentMusicFadeDuration was set during FADE_OUT.
      // If we keep it, we use it for fade in too.
      // If startMusicInternal is called directly (no fade out), we should consume.

      if (currentFadeState == FadeState.NONE) {
        currentMusicFadeDuration = consumeFadeDuration();
      }
      // else we are coming from FADE_OUT (via update), keep existing duration?
      // Yes, symmetric fade seems appropriate.

      currentFadeState = FadeState.FADING_IN;
    } catch (Exception e) {
      Gdx.app.error("AudioManager", "Failed to load/play music: " + path, e);
    }
  }

  public void stopMusic() {
    if (currentMusic != null && currentMusic.isPlaying()) {
      currentFadeState = FadeState.FADING_OUT;
      currentMusicFadeDuration = consumeFadeDuration();
      nextMusicPath = null; // No next music
    }
  }

  private void stopMusicImmediate() {
    if (currentMusic != null) {
      currentMusic.stop();
      currentMusic.dispose();
      currentMusic = null;
    }
    currentMusicPath = null;
    currentFadeState = FadeState.NONE;
  }

  public void update(float delta) {
    if (currentMusic == null) return;

    float targetVolume = SettingsManager.getInstance().getMusicVolume();

    switch (currentFadeState) {
      case FADING_IN:
        currentVolume += (targetVolume / currentMusicFadeDuration) * delta;
        if (currentVolume >= targetVolume) {
          currentVolume = targetVolume;
          currentFadeState = FadeState.NONE;
        }
        currentMusic.setVolume(currentVolume);
        break;

      case FADING_OUT:
        // If targetVolume changed mid-fade, we simply fade to 0
        currentVolume -= (1.0f / currentMusicFadeDuration) * delta; // Fade out 1.0 -> 0

        if (currentVolume <= 0f) {
          currentVolume = 0f;
          currentMusic.setVolume(0f);
          stopMusicImmediate();

          // Check if we have a next track queued
          if (nextMusicPath != null) {
            startMusicInternal(nextMusicPath, nextMusicLoop);
            nextMusicPath = null;
          }
        } else {
          currentMusic.setVolume(currentVolume);
        }
        break;

      case NONE:
        // Keep volume synced with settings
        if (Math.abs(currentMusic.getVolume() - targetVolume) > 0.01f) {
          currentMusic.setVolume(targetVolume);
          currentVolume = targetVolume;
        }
        break;
    }
  }

  public void updateMusicVolume() {
    // Handled in update() mostly, but can force check here if needed
  }

  public void setCurrentMusicVolume(float volume) {
    if (currentMusic != null) {
      currentMusic.setVolume(volume);
      currentVolume = volume;
      // If we manually set volume, we might interfere with fade.
      // Assume manual set disables auto-fade sync for that frame, but update() will
      // overwrite typically.
      // For logic like OutroOverlay which sets volume to 0 manually:
      // update() 'NONE' state will revert it to targetVolume.
      // We should respect manual control if Fading is involved?
      // Actually, if we use this manager, we shouldn't manually set volume outside of
      // settings.
      // OutroOverlay should use playMusic (which fades in) instead of manual logic!
    }
  }

  public boolean isPlaying(String path) {
    return currentMusicPath != null && currentMusicPath.equals(path) && currentMusic.isPlaying();
  }

  /**
   * Plays a sound effect. Note: Sound instances are usually managed by the caller or a separate
   * cache. For this refactor, we provide a helper that loads, plays, and auto-disposes logic is
   * tricky for Sounds (they need to be disposed after playing).
   *
   * <p>Recommendation: Callers should manage Sound lifecycles or we use a proper AssetManager. For
   * now, we will return the Sound object so caller can store/dispose it, essentially wrapping
   * Gdx.audio.newSound.
   */
  public Sound loadSound(String path) {
    return Gdx.audio.newSound(Gdx.files.internal(path));
  }

  /** Dispose all managed resources. */
  public void dispose() {
    stopMusic();
  }
}
