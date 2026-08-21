---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[Lux può]]"
  - "[[User Story sono]]"
  - "[[Lux's Abilities and Interactions]]"
tags:
  - npc
  - merchant
  - hidden-objects
  - interaction
last modified: 2026-08-15
AI: true
type: Note
sources:
  - "2-allineamento-metodologico.md"
  - "3-part.md"
---

# NPC Types and Interactions

Non-Player Characters (NPCs) in the game serve distinct roles and enable key interactions:

- **Merchant NPC**: Spawns in the Start/Hub scene after death. Interactions open a UI to view and purchase permanent power-ups (e.g., Max HP increases) using permanent currency. The Merchant sells upgrades and weaponry.
- **Hidden Object Interaction**: Objects flagged as "Hidden" become visible (alpha change or glow) when Lux's sensor collides with them. Interaction (via Interact_button) reveals loot or modifies the environment (e.g., walls destroyed, baskets drop loot). Nox cannot trigger these interactions.
- **Dialogue and Shop Interaction**: NPCs enable dialogue or shop access when interacted with via Interact_button, supporting quests and progression.

NPCs are central to progression, loot acquisition, and narrative delivery.


## Additional notes: npc (from 3-part.md)

### NPC Classificazione e Comportamento

- **Tipologie di NPC**:
  - **NPC Amichevoli**: Generati casualmente con nomi univoci; dotati di dialoghi AI per interazioni ambientali e narrative.
  - **NPC Nemici**: Creature vuote prive di intelligenza artificiale avanzata; agiscono come ostacoli dinamici nel Mu.

- **Meccaniche di Interazione**:
  - **Filtri a Schermo (Post FX)**: Effetti visivi applicati per evidenziare elementi interattivi o nascosti (es. oggetti "Hidden" che diventano visibili tramite cambiamento di alpha o glow quando Lux entra in collisione con essi).
  - **Interazione tramite Pulsante**: L’utente può interagire con gli NPC tramite il pulsante "Interact" per accedere a dialoghi, negozi o modificare l’ambiente (es. distruzione di muri, caduta di ceste con loot).

- **Dipendenze LibGDX**:
  - `aiVersion=1.8.2`
  - `box2dlightsVersion=1.5`
  - `gdxControllersVersion=2.2.3`
  - `lmlVersion=1.10.1.12.1`
  - `colorfulVersion=0.9.0`
  - `controllerUtilsVersion=2.2.1`
  - `gandVersion=0.3.5`
  - `cruxVersion=0.1.2`
  - `gdcruxVersion=0.1.1`
  - `jaciVersion=0.4.0`
  - `jdkgdxdsVersion=1.13.1`
  - `funderbyVersion=0.1.2`
  - `digitalVersion=0.9.5`
  - `jdkgdxdsInteropVersion=1.13.1.0`

- **Giullare**: Classe NPC con caratteristiche specifiche:
  - Velocità media
  - Vita media
  - Pool di armi variabili (distanza e mischia)
  - Danno variabile e randomico
