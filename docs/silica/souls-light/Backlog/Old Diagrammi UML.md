---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[GameModel]]"
  - "[[Player]]"
  - "[[Test-Driven Development (TDD)]]"
tags:
  - uml
  - architettura
  - memento
  - dependency-injection
  - test-comportamentali
last modified: 2026-08-15
AI: true
sources:
  - "3-part.md"
type: Note
---

# Old Diagrammi UML

I **vecchi diagrammi UML** rappresentano una versione precedente della struttura architetturale di *Souls Light*, focalizzata su componenti, controller, modelli e pattern come il **Memento** per il salvataggio dello stato del gioco.

- **Componenti principali**:
  - **GameManager**: Componente singleton che gestisce lo stato del gioco (avvio, pausa, salvataggio, caricamento).
  - **AudioManager**: Componente singleton per la gestione dell’audio (musica, effetti sonori, mute).
  - **EventManager**: Componente singleton per la gestione degli eventi di gioco.
  - **MovementSystem**: Componente per la gestione del movimento del giocatore.
- **Controller (Gestione Input/UI)**:
  - **MainMenuController**: Gestisce il menu principale (inizializzazione, avvio gioco, opzioni, uscita).
  - **InputController**: Gestisce gli input da tastiera/touch.
- **Modello e Memento**:
  - **Player**: Componente che rappresenta il giocatore con attributi come HP, posizione (x, y) e determinazione (Will).
  - **Memento**: Interfaccia per il pattern Memento.
  - **ConcreteMemento**: Implementazione concreta del pattern Memento per il salvataggio dello stato del giocatore.
- **Relazioni**:
  - **Dependency Injection**: Utilizzo di iniezione di dipendenze (es. `@Inject` in `MainMenuController` e `InputController`).
  - **Memento Pattern**: Relazioni tra `GameManager`, `Player` e `ConcreteMemento` per la gestione dello stato.
- **Test comportamentali**:
  - **testRangerBehaviour**: Verifica che il Ranger mantenga la distanza dal giocatore per attaccare da lontano.
  - **testPusherBehaviour**: Verifica che il Pusher si avvicini al giocatore per spingerlo su trappole.
  - **testShielderBehaviour**: Comportamento del Shielder da definire.
  - **testOblivionBehaviour**: Comportamento di Oblivion da definire.
