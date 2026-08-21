---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[Specifiche progettuali]]"
  - "[[Requisiti funzionali]]"
tags:
  - non-functional-requirements
  - game-design
  - quality-attributes
last modified: 2026-08-15
AI: true
sources:
  - "2-allineamento-metodologico.md"
type: Note
---

# Gioco deve

The game must satisfy the following non-functional requirements to ensure quality attributes and constraints are met:

- **Performance**: The game must be responsive and fast, with a target frame rate of at least 60 FPS and a minimum acceptable frame rate of 30 FPS for no more than 1 second on recommended hardware.

- **Usability**: The user interface must be easy to use. The average time to complete the tutorial and first critical interactions (e.g., weapon swap, inventory opening) must be less than X minutes. The rate of critical user errors (e.g., not understanding how to revive Nox) must be less than 5% during the first user test.

- **Security**: The system must ensure file integrity and prevent unauthorized access. Measures include hashing map files, tracking violation rates, and enforcing a corruption rate for save/map files below 5% under normal gameplay conditions. Save files must resist tampering.

- **Maintainability**: The game must be easy to correct and improve over time.

- **Scalability**: The game must support future expansions without degradation.

- **Storage**: The game installation file (excluding user saves) must occupy less than 600 MB at release.
