---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[GameModel]]"
  - "[[Enemies]]"
  - "[[Player Movement Mechanics]]"
tags:
  - rendering
  - pipeline-2d
  - pixel-art
  - box2d
  - libgdx
last modified: 2026-08-15
AI: true
type: Note
sources:
  - "3-part.md"
  - "4-part.md"
---

# Rendering Pipeline

Il rendering pipeline del gioco si basa su una **pipeline 2D** con le seguenti componenti e specifiche tecniche:

- **Sprite 2D Pipeline**:
  - Stile pixel art con sprite base di **32x32 pixel**.
  - **Sprite Atlas optimization** per ridurre il numero di draw call tramite l’uso di una grande immagine che contiene tutti gli sprite.

- **Audio Asset**:
  - **Dynamic audio loading** per ottimizzare la gestione della memoria durante l’esecuzione.

- **2D Rendering Specifiche**:
  - **Sprite Renderer** con batching automatico per migliorare le prestazioni.
  - **2D Light System** per effetti di illuminazione dinamica.
  - **Particle System integrato** per effetti visivi (VFX).

- **Physics Engine**:
  - Fisica basata su **Box2D** per la detection delle collisioni.
  - **Rigid body physics** applicata a proiettili e oggetti.
  - **Particle systems** per effetti speciali.

- **Collision Detection**:
  - **Tilemap Collider 2D** per la geometria dei livelli.
  - **Composite Collider 2D** per ottimizzare le prestazioni.
  - **Trigger-based interactions** per pickup di oggetti e zone di danno.


## Additional notes: art (from 4-part.md)

### Pixel-Art Rendering Setup
- **Viewport**: `FitViewport` configured for pixel-art scaling with `WORLD_WIDTH = 480` and `WORLD_HEIGHT = 270`.
- **Camera**: Orthographic camera centered on the viewport.

### Rendering Loop
- `render(float delta)`: Executes MVC update loop (`controller.update(delta)` → `model.update(delta)`) and clears screen with black background (`ScreenUtils.clear(0, 0, 0, 1)`).
- **Camera Update**: `camera.update()` and `batch.setProjectionMatrix(camera.combined)` applied before batch rendering.

### Resize Handling
- `resize(int width, int height)`: Updates viewport to maintain pixel-perfect rendering (`viewport.update(width, height, true)`).


## Additional notes: pixel (from 4-part.md)

- **Pixel Art Viewport**: `FitViewport` configured with world dimensions `480x270` pixels to maintain 16:9 aspect ratio while scaling to screen size.
- **Camera Setup**: Orthographic camera centered on the viewport; projection matrix applied to `SpriteBatch` for pixel-perfect rendering.
- **Viewport Resize Handling**: On resize, viewport updates with `true` to center the camera and preserve pixel alignment.
