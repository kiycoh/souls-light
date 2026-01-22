package io.github.soulslight.model;

public class EnemyMemento {
    public String type;
    public float x;
    public float y;
    public float health;

    public EnemyMemento() {}

    public EnemyMemento(String type, float x, float y, float health) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.health = health;
    }
}
