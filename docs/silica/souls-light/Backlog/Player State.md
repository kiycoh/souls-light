---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[Player]]"
  - "[[PlayerClass]]"
  - "[[getInventory]]"
  - "[[pickUpItem]]"
  - "[[Class Selector]]"
tags:
  - player-state
  - inventory
  - class-selection
  - game-mechanics
last modified: 2026-08-15
AI: true
sources:
  - "2-allineamento-metodologico.md"
type: Note
---

# Player State

The player state encompasses inventory contents, equipped items, selected class template, and current statistics (HP, damage modifiers, cooldowns, etc.).

- **Inventory**: A dedicated screen allows the player to view collected items and character stats during gameplay.
- **Class Selection**: At the start of a run, the player chooses a class (e.g., Warrior, Mage), which:
  - Sets base HP and initial equipment (e.g., Warrior starts with high HP and a basic melee weapon).
  - Disables mana/will expenditure for ranged attacks if class rules prohibit it.
  - Updates the player state with an empty base template reflecting class-specific attributes.
