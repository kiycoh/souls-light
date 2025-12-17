package io.github.soulslight.controller;

import java.util.ArrayList;
import java.util.List;

public class MovementInput {
    private List<MovementListener> listeners = new ArrayList<>();

    public void addMovementListener(MovementListener listener) {
        listeners.add(listener);
    }

    public List<MovementListener> getListeners() {
        return listeners;
    }

    public interface MovementListener {
        void onMove(float x, float y);
    }
}

