---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[GameModel]]"
  - "[[addProjectileListener]]"
  - "[[notifyProjectileRequest]]"
tags:
  - data-serialization
  - save-system
  - json-format
last modified: 2026-08-15
AI: true
sources:
  - "2-allineamento-metodologico.md"
type: Note
---

# JSON

The game uses JSON for persistent [[Data|data]] storage and serialization:

- **Save System**: Player progress is saved to a global JSON file. When purchasing upgrades (e.g., Max HP increase), the `BaseMaxHP` value in the global save file is updated. New runs instantiate the player with the updated base HP.

- **Shadow Loot Event**: On death, the player's inventory and stats are serialized to JSON. A "Shadow Enemy" may spawn in a special new run, using the saved weapon from the JSON. Defeating the shadow drops the old inventory.

- **Merchant Interaction**: The merchant UI displays the permanent currency balance stored in the global save file.
