---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[GameModel]]"
  - "[[Rendering Pipeline]]"
tags:
  - box2d
  - physics
  - game-engine
  - initialization
last modified: 2026-08-15
AI: true
sources:
  - "4-part.md"
type: Note
---

# Physics World Initialization

The physics world is initialized in the `GameModel` constructor as a `Box2D World` instance with a gravity vector of `(0, 0)` and continuous collision detection enabled (`true`).

- **Constructor call**:
  ```java
  this.physicsWorld = new World(new Vector2(0, 0), true);
  ```
- **Disposal**: The world is disposed via `physicsWorld.dispose()` in the `dispose()` method to release Box2D resources.
- **Access**: The world is exposed through `getPhysicsWorld()` for use by physics-related systems (e.g., collision detection, rigid body simulation).
