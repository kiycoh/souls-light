---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[GameScreen]]"
  - "[[Rendering Pipeline]]"
tags:
  - libgdx
  - viewport
  - pixel-art
  - screen-lifecycle
last modified: 2026-08-15
AI: true
sources:
  - "4-part.md"
type: Note
---

# resize(int width, int height)

> [!NOTE] This method handles window resizing while maintaining true pixel art proportions.

- **Viewport update**: calls `viewport.update(width, height, true)` where `true` centers the camera after resizing.
- **Aspect ratio preservation**: uses a `FitViewport` initialized with `WORLD_WIDTH = 480` and `WORLD_HEIGHT = 270` to ensure pixel-perfect rendering regardless of window dimensions.
- **MVC separation**: invoked by the LibGDX framework during screen resize events; no direct model/controller interaction required.

```java
@Override
public void resize(int width, int height) {
    viewport.update(width, height, true); // centers camera
}
```
