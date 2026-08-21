---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[update]]"
  - "[[applyKnockback]]"
  - "[[createBody]]"
tags:
  - tdd
  - agile-development
  - testing
  - workflow
last modified: 2026-08-15
AI: true
type: Note
sources:
  - "2-allineamento-metodologico.md"
  - "3-part.md"
---

# Test-Driven Development (TDD)

Test-Driven Development (TDD) is a software development workflow where tests are written before implementation to guide design and ensure correctness.

- **Workflow**:
  - User stories are prioritized (e.g., via planning poker) to establish a baseline.
  - Tasks are assigned to sprints.
  - Diagrams and tests are constructed for TDD.
  - Example implementations (e.g., map systems) are demonstrated.

- **Key Tenets**:
  - Tests define requirements before code is written.
  - Iterative refinement ensures flexibility and reusability of components (e.g., animation systems triggered by specific frames).

- **Non-Functional Requirements (NFRs)**: NFRs define quality attributes and constraints the system must meet, distinct from functional user stories.


## Additional notes: test driven development (from 3-part.md)

Test Driven Development (TDD) è una metodologia di sviluppo software che prevede la scrittura dei test prima dell'implementazione delle classi, seguendo un ciclo iterativo noto come **Red-Green-Refactor**. L'obiettivo è migliorare la qualità del codice e ridurre i difetti.

**Ciclo TDD**
1. **Red**: Scrivere un test che fallisce per una nuova funzionalità o modifica.
2. **Green**: Implementare la funzionalità minima necessaria per far passare il test.
3. **Refactor**: Migliorare il codice senza alterare il comportamento, assicurandosi che i test continuino a passare.

**[[Specifiche Implementative|Specifiche implementative]] per Souls Light**
- **Tech stack**: IntelliJ IDEA / Visual Studio Code, Java + Gradle.
- **Architettura**: LibGDX + Autumn MVC framework + dipendenze.
- **Versionamento**: Git (GitHub repo).
- **Modalità di gioco**: Co-op, endless.
- **World**: Tile map dinamica e randomica generata tramite mappa di rumore.

**Componenti tecniche**
- **Rendering**: Sprite Renderer con batching automatico, 2D Light system per effetti dinamici, Particle System integrato per VFX.
- **Fisica**: Box2D-based physics per collision detection, rigid body physics per proiettili e oggetti, particle systems per effetti speciali.
- **Collision detection**: Tilemap Collider 2D per la geometria dei livelli, Composite Collider 2D per ottimizzazione delle prestazioni, trigger-based interactions per pickup di oggetti e zone di danno.

**Strategia di testing**
- **Tipologie di test**: Unit test, integration test, user acceptance testing, test di performance.
- **Strumenti**: Strumenti specifici per l'automazione dei test.
- **Bug tracking**: Gestione centralizzata dei difetti.
- **Aggiornamenti**: Strategia per la distribuzione degli aggiornamenti del gioco.
