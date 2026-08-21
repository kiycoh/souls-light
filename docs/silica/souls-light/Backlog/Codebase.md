---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[GameController]]"
  - "[[GameModel]]"
  - "[[GameScreen]]"
  - "[[SoulsLightGame]]"
  - "[[Soul's Light]]"
tags:
  - codebase
  - mvc
  - libgdx
  - java
  - gradle
  - architecture
last modified: 2026-08-15
AI: true
sources:
  - "3-part.md"
type: Note
---

# Codebase

> [!NOTE] Struttura e componenti principali della codebase di *Soul’s Light*, basata su **LibGDX** e architettura **MVC**.

**Struttura del progetto:**
```
📁 .gradle
📁 .idea
📁 build
📁 core
  📁 core/build
  📁 core/src
    📁 core/src/main
 📁 core/src/main/java/io/github/soulslight/controller
 📄 GameController.java
 📁 core/src/main/java/io/github/soulslight/model
 📄 GameModel.java
 📁 core/src/main/java/io/github/soulslight/view
 📄 GameScreen.java
 📁 core/src/main/java/io/github/soulslight
 📄 SoulsLightGame.java
  📁 core/build.gradle
📁 gradle
```

**Componenti principali:**
- **GameController.java**: gestisce gli input dell’utente e li trasmette al *model*.
- **GameModel.java**: contiene la logica di gioco e lo stato dell’applicazione.
- **GameScreen.java**: si occupa del rendering della schermata di gioco.
- **SoulsLightGame.java**: punto di ingresso dell’applicazione.

**Settimana 3 [24/11/2025 - 28/11/2025]:**
- **Obiettivi:**
  - Iniziare diagrammi con design pattern (1° sprint).
  - Completare user story epiche.
- **Progressi:**
  - 24 nov 2025: Continuazione epiche, definizione primo sprint.
  - 28 nov 2025: Creazione repository e architettura MVC base → **Codebase**.
