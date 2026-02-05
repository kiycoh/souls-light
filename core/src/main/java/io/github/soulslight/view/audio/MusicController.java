package io.github.soulslight.view.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import io.github.soulslight.manager.SettingsManager;

/**
 * Handles game music playback including exploration/boss music crossfading. Extracted from
 * GameScreen to satisfy Single Responsibility Principle.
 */
public class MusicController implements Disposable {

  private Music explorationMusic;
  private Music bossMusic;

  // Crossfade state
  private boolean bossCrossfadeStarted = false;
  private boolean bossCrossfadeCompleted = false;
  private float bossCrossfadeTime = 0f;
  private static final float BOSS_FADE_DURATION = 5f; // seconds

  public MusicController() {
    loadMusic();
  }

  private void loadMusic() {
    if (explorationMusic == null) {
      explorationMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/exploration.mp3"));
      explorationMusic.setLooping(true);
    }
    if (bossMusic == null) {
      bossMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/bossfight.mp3"));
      bossMusic.setLooping(true);
    }
  }

  /** Starts playing exploration music if not already playing. */
  public void playExplorationMusic() {
    if (explorationMusic != null && !explorationMusic.isPlaying()) {
      explorationMusic.setVolume(SettingsManager.getInstance().getMusicVolume());
      explorationMusic.play();
    }
  }

  /** Starts playing boss music if not already playing. */
  public void playBossMusic() {
    if (bossMusic != null && !bossMusic.isPlaying()) {
      bossMusic.setVolume(0f);
      bossMusic.play();
    }
  }

  /** Begins crossfade from exploration to boss music. */
  public void startBossCrossfade() {
    if (!bossCrossfadeStarted) {
      bossCrossfadeStarted = true;
      bossCrossfadeTime = 0f;
      playBossMusic();
    }
  }

  /** Updates crossfade progress. Call once per frame. */
  public void updateBossCrossfade(float delta) {
    if (!bossCrossfadeStarted || bossCrossfadeCompleted) return;
    if (explorationMusic == null && bossMusic == null) return;

    bossCrossfadeTime += delta;
    float t = MathUtils.clamp(bossCrossfadeTime / BOSS_FADE_DURATION, 0f, 1f);
    float baseVolume = SettingsManager.getInstance().getMusicVolume();

    if (explorationMusic != null) {
      explorationMusic.setVolume(baseVolume * (1f - t));
    }
    if (bossMusic != null) {
      bossMusic.setVolume(baseVolume * t);
    }

    if (bossCrossfadeTime >= BOSS_FADE_DURATION) {
      bossCrossfadeCompleted = true;
    }
  }

  /** Fades out all music (for outro). Call once per frame during outro. */
  public void updateFadeOut(float delta) {
    float fadeSpeed = 0.5f; // Volume per second (2 seconds to fade out)

    if (bossMusic != null && bossMusic.isPlaying()) {
      float v = bossMusic.getVolume();
      if (v > 0) {
        bossMusic.setVolume(Math.max(0f, v - fadeSpeed * delta));
      } else {
        bossMusic.stop();
      }
    }

    if (explorationMusic != null && explorationMusic.isPlaying()) {
      float v = explorationMusic.getVolume();
      if (v > 0) {
        explorationMusic.setVolume(Math.max(0f, v - fadeSpeed * delta));
      } else {
        explorationMusic.stop();
      }
    }
  }

  /** Syncs volume with settings (when not crossfading). */
  public void updateVolume() {
    // If NOT crossfading and NOT outro, keep effective volume synced with Settings
    if ((!bossCrossfadeStarted && !bossCrossfadeCompleted) || bossCrossfadeCompleted) {
      float vol = SettingsManager.getInstance().getMusicVolume();

      if (bossCrossfadeCompleted) {
        if (bossMusic != null && bossMusic.isPlaying()) bossMusic.setVolume(vol);
        if (explorationMusic != null) explorationMusic.setVolume(0f);
      } else {
        if (explorationMusic != null && explorationMusic.isPlaying())
          explorationMusic.setVolume(vol);
        if (bossMusic != null) bossMusic.setVolume(0f);
      }
    }
  }

  /** Pauses all music (for screen hide). */
  public void pause() {
    if (explorationMusic != null && explorationMusic.isPlaying()) {
      explorationMusic.pause();
    }
    if (bossMusic != null && bossMusic.isPlaying()) {
      bossMusic.pause();
    }
  }

  /** Resumes music playback. */
  public void resume() {
    // Resume the appropriate music based on crossfade state
    if (bossCrossfadeCompleted) {
      if (bossMusic != null) bossMusic.play();
    } else {
      if (explorationMusic != null) explorationMusic.play();
    }
  }

  public boolean isBossCrossfadeStarted() {
    return bossCrossfadeStarted;
  }

  @Override
  public void dispose() {
    if (explorationMusic != null) {
      explorationMusic.dispose();
      explorationMusic = null;
    }
    if (bossMusic != null) {
      bossMusic.dispose();
      bossMusic = null;
    }
  }
}
