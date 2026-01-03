package io.github.soulslight.model.Maps;

import java.util.*;

public class Room {
  private String templateName;
  private final List<String> enemies =
      new ArrayList<>(); // I tipi di nemici da caricare si potrebbero organizzare in un array list

  public void setTemplateName(String name) {
    this.templateName = name;
  }

  public String getTemplateName() {
    return templateName;
  }

  public void addEnemy(String enemyType) {
    this.enemies.add(enemyType);
  }

  public List<String> getEnemies() {
    return enemies;
  }
}
