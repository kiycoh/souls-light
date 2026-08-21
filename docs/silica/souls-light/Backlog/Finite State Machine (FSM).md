---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[AI/NPC Behavior Systems]]"
  - "[[Oblivion]]"
  - "[[Game Design]]"
tags:
  - ai
  - fsm
  - pathfinding
  - npc
  - boss
  - aggro
last modified: 2026-08-15
AI: true
sources:
  - "3-part.md"
type: Note
---

# Finite State Machine (FSM)

Sistema di intelligenza artificiale basato su **Finite State Machine (FSM)** per la gestione del comportamento di NPC e boss nel gioco **Souls Light**. Integra pathfinding A* per la navigazione nei dungeon e sistemi di rilevamento della linea di vista per l’aggro.

- **Stati principali (FSM)**:
  - **Idle**: stato di inattività, il nemico attende eventi o il giocatore.
  - **Search**: il nemico cerca il giocatore tramite pathfinding A*.
  - **Attack**: il nemico attacca il giocatore con pattern predefiniti o dinamici.
  - **Death**: stato terminale, gestione dell’animazione e della scomparsa del nemico.

- **Complessità scalabile**:
  - **AI Basic**: pattern di movimento semplici per nemici di base.
  - **AI Avanzata**: pattern di attacco complessi e fasi multiple per boss (es. **Oblivion**).
  - **Swarm Intelligence**: gestione di gruppi di nemici tramite comportamenti coordinati.

- **Sistemi integrati**:
  - **Line of Sight Detection**: rilevamento della linea di vista per attivare l’aggro.
  - **Pathfinding A***: algoritmo di pathfinding per la navigazione dinamica nei dungeon.
  - **Behavioral Patterns**: differenziazione dei comportamenti in base al tipo di nemico (es. nemici base vs. boss).

- **Implementazione**:
  - Integrazione con sistemi di dialogo degli NPC per coerenza narrativa.
  - Gestione delle transizioni tra stati tramite eventi (es. danno ricevuto, perdita di vista del giocatore).
