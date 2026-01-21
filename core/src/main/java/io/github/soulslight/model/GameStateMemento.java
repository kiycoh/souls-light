package io.github.soulslight.model;

import java.util.ArrayList;
import java.util.List;

public class GameStateMemento {
    // list supports 1, 2 or N players
    public List<PlayerMemento> players = new ArrayList<>();
    public int currentLevelIndex;

    // No-arg constructor
    public GameStateMemento() {}

    public GameStateMemento(List<PlayerMemento> players, int currentLevelIndex) {
        this.players = players;
        this.currentLevelIndex = currentLevelIndex;
    }
}
