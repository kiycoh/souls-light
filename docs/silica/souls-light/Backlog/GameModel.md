---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[GameController]]"
tags:
  - gamemodel
  - libgdx
  - architettura
  - java
last modified: 2026-08-15
AI: true
type: Note
sources:
  - "3-part.md"
  - "4-part.md"
---

# GameModel

### Panoramica

`GameModel` è una classe centrale nel progetto **Soul’s Light**, responsabile della gestione dello stato del gioco e delle logiche di business. Fa parte della struttura standard LibGDX e si colloca nel package `io.github.soulslight.model`.

### Responsabilità

- **Gestione dello Stato del Gioco**: Mantiene lo stato globale del gioco, inclusi personaggi, nemici, oggetti, e progressi del giocatore.
- **Logiche di Business**: Implementa le regole di gioco, le meccaniche di combattimento, e le interazioni tra entità.
- **Integrazione con Controller e View**: Collabora con `GameController` per ricevere input e con `GameScreen` per la visualizzazione.

### Struttura del Codice

```java
package io.github.soulslight.model;

/**
 * {@link GameModel} gestisce lo stato del gioco e le logiche di business.
 */
public class GameModel {
    // Attributi e metodi per la gestione dello stato del gioco
}
```

### Dipendenze

- **LibGDX**: Framework principale per la gestione della grafica, degli input, e del ciclo di vita del gioco.
- **Package `io.github.soulslight.controller`**: Per la gestione degli input e delle azioni del giocatore.
- **Package `io.github.soulslight.view`**: Per il rendering e la visualizzazione dello stato del gioco.

### Esempio di Integrazione

```java
package io.github.soulslight;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.soulslight.controller.GameController;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.view.GameScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class SoulsLightGame extends Game {
    private SpriteBatch batch;
    private GameModel model;
    private GameController controller;

    @Override
    public void create() {
 batch = new SpriteBatch();
 model = new GameModel();
 controller = new GameController(model);
 this.setScreen(new GameScreen(batch, model, controller));
    }

    @Override
    public void dispose() {
 batch.dispose();
 super.dispose();
    }
}
```

### Note di Implementazione

- **Separazione delle Responsabilità**: `GameModel` si concentra esclusivamente sulla gestione dello stato e delle logiche, delegando la gestione degli input e la visualizzazione ad altri componenti.
- **Estensibilità**: La classe è progettata per essere estesa con nuove funzionalità senza modificare la struttura esistente.


## Additional notes: GAME (from 4-part.md)

### Constants
- `MAX_WILL = 100f`: Maximum shared "Will" value for game mechanics.

### Box2D Physics
- `physicsWorld`: Box2D world instance initialized with gravity vector `(0, 0)` and continuous collision detection enabled (`true`).

### Game State
- `currentWill`: Tracks the player’s current Will value, initialized to `MAX_WILL / 2`.
- `isPaused`: Boolean flag controlling game pause state.

### Methods
- `update(float deltaTime)`: Advances physics simulation (`physicsWorld.step(1/60f, 6, 2)`) and game logic when not paused. Skips updates if `isPaused` is `true`.

### Disposable Implementation
- `dispose()`: Releases Box2D physics world resources via `physicsWorld.dispose()`.


## Additional notes: gamescreen (from 4-part.md)

- **Physics World**: Box2D `World` initialized with gravity vector `(0, 0)` and continuous collision detection enabled (`true`).
- **Game State**: `currentWill` initialized to `MAX_WILL / 2` (50 units).
- **Pause State**: `isPaused` initialized to `false`.
- **Update Loop**: Physics step uses fixed timestep `1/60` s, velocity iterations `6`, and position iterations `2`.
- **Disposal**: `physicsWorld.dispose()` called to release Box2D resources.
