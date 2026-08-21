---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[Oblivion]]"
  - "[[Giullare]]"
  - "[[shadow creatures]]"
  - "[[GameModel]]"
  - "[[Player]]"
tags:
  - ai
  - npc
  - behavior-systems
  - fsm
  - pathfinding
  - aggro
  - boss-mechanics
last modified: 2026-08-15
AI: true
sources:
  - "3-part.md"
type: Note
---

# AI e sistemi di comportamento NPC

> [!NOTE] Questo documento descrive l’architettura dei sistemi di intelligenza artificiale e comportamento per i personaggi non giocanti (NPC) e i nemici in *Soul’s Light*.

**Sistema AI basato su Finite State Machine (FSM):**
- Ogni entità controllata dall’AI (nemici, boss, NPC) opera attraverso stati predefiniti:
  - **Idle**: stato di attesa o ricerca.
  - **Search**: ricerca del giocatore o di risorse.
  - **Attack**: attacco al giocatore.
  - **Death**: stato terminale dopo la sconfitta.

**Pathfinding:**
- Implementato tramite algoritmo **A*** per la navigazione dinamica del dungeon.

**Sistema di aggro:**
- **Line of sight detection**: rilevamento della linea di vista per attivare lo stato di attacco.

**Complessità scalabile:**
- **AI Basic**: pattern di movimento semplici per nemici di base.
- **AI Avanzata**: pattern complessi e fasi multiple per boss e nemici elite.

**Boss: Oblivion**
- Sistema AI dedicato con pattern di attacco variabili e fasi evolutive durante lo scontro.

**Abilità del Giullare:**
- Velocità media, vita media.
- Pool armi: sia a distanza che melee.
- Danno variabile e randomico.

**Abilità del Protagonista:**
- Potenziamenti basati sulla **volontà** (non magia):
  - Esempio: *"Collassare la Realtà"* — crea un’esplosione di esistenza in un punto della mappa, danneggiando le *shadow creatures*.
