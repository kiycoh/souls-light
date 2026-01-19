package io.github.soulslight.model;

import com.badlogic.gdx.math.Vector2;

// Classe per il salvataggio
public class GameStateMemento {
  public float health;
  public float playerX;
  public float playerY;
  public int currentLevelIndex; // Utile per il futuro

  public GameStateMemento() {}

  public GameStateMemento(float health, Vector2 position, int levelIndex) {
    this.health = health;
    this.playerX = position.x;
    this.playerY = position.y;
    this.currentLevelIndex = levelIndex;
  }
}
