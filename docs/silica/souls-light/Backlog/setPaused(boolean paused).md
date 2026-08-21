---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[GameModel]]"
  - "[[update(float deltaTime)]]"
tags:
  - game-state
  - pause-mechanics
  - physics-control
  - libgdx
last modified: 2026-08-15
AI: true
sources:
  - "4-part.md"
type: Note
---

# setPaused(boolean paused)

> [!NOTE] Controls the game’s pause state, halting physics and game logic updates when active.

- **Pause flag**: toggles `isPaused` boolean in `GameModel`; defaults to `false` on initialization.
- **Update guard**: the `update(float deltaTime)` method checks `isPaused` and returns early if `true`, preventing physics and entity updates.
- **Physics integration**: uses LibGDX’s `World.step()` within `update()`; pausing stops these physics iterations.
- **State management**: provides `isPaused()` getter for external systems (e.g., UI, input handling) to query pause status.

```java
public void setPaused(boolean paused) {
    this.isPaused = paused;
}

public boolean isPaused() {
    return isPaused;
}
```
