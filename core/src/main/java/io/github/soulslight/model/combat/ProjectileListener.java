package io.github.soulslight.model.combat;

import com.badlogic.gdx.math.Vector2;

/**
 * Pattern: Observer Listener interface for entities that need to spawn projectiles. Decouples the
 * entity logic from the GameModel/ProjectileManager.
 */
public interface ProjectileListener {
  void onProjectileRequest(Vector2 origin, Vector2 target, String type);
}
