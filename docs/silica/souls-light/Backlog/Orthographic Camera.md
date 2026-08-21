---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[GameScreen]]"
  - "[[Rendering Pipeline]]"
tags:
  - orthographic-camera
  - viewport
  - pixel-art
  - rendering
  - libgdx
last modified: 2026-08-15
AI: true
sources:
  - "4-part.md"
type: Note
---

# Orthographic Camera

An `OrthographicCamera` is used in the `GameScreen` class to render pixel art at a fixed resolution while maintaining aspect ratio. The camera is paired with a `FitViewport` to handle window resizing.

- **Initialization**:
  ```java
  this.camera = new OrthographicCamera();
  ```
- **Viewport setup**:
  ```java
  this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
  ```
  - `WORLD_WIDTH = 480` and `WORLD_HEIGHT = 270` define the logical pixel art resolution.
  - The viewport scales the camera to fit the screen while preserving aspect ratio.
- **Usage**: The camera's combined projection matrix is applied to the `SpriteBatch` for rendering:
  ```java
  camera.update();
  batch.setProjectionMatrix(camera.combined);
  ```
- **Resizing**: The viewport is updated on window resize to maintain correct scaling:
  ```java
  viewport.update(width, height, true);
  ```
  - The `true` parameter centers the camera after resizing.
