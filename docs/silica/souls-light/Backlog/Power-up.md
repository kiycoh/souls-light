---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[Merchant]]"
  - "[[NPC Types and Interactions]]"
  - "[[Player Movement Mechanics]]"
tags:
  - power-up
  - temporary-advantage
  - player-progression
last modified: 2026-08-15
AI: true
sources:
  - "2-allineamento-metodologico.md"
type: Note
---

# Power-up

A power-up is a temporary or permanent item that grants the player a gameplay advantage when collected or activated.

- **Temporary Power-ups** (ID 11 - Epica):
  - Obtained after completing three rooms; player selects one from three options.
  - Examples: 2x Damage (ID 11.1) — doubles outgoing damage but also incoming damage.
  - Applied via modifier x2 to outgoing damage calculation.

- **Removal via Scammer NPC** (ID 18.2):
  - Players can interact with a Scammer NPC to spend accumulated currency and remove an unwanted temporary power-up.

- **Enemy-created Power-ups**:
  - Sorcerer Enemy (ID 10.5): Creates ground indicator → explosion after X seconds.
  - Ice Enemy: Fires projectile applying "Slow" status (reduces movement speed in Model).
  - Kiting behavior: Maintains distance from the player.
