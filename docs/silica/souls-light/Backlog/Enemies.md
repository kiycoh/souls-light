---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[Ambientazione]]"
  - "[[cooldown]]"
tags:
  - enemies
  - classes
  - mechanics
last modified: 2026-08-15
AI: true
sources:
  - "2-allineamento-metodologico.md"
type: Note
---

# Enemies

Enemy systems and classes in Soul’s Light:

- **Warrior Class (ID 5.1)**:
  - Base HP set to a high value (X).
  - Adds "Base Sword" to inventory.
  - Disables mana/will use for ranged attacks (class-specific rule).

- **Mage Class (ID 5.2)**:
  - Base HP set to a low value (Y).
  - Adds "Magic Staff/Projectile" to inventory.

- **Parry Mechanic (ID 8.2)**:
  - Success condition: Right stick direction matches incoming attack direction within a 0.2s window.
  - Failure: Full damage + parry cooldown activated.

- **Ranged Attack (ID 8.3)**:
  - Spawns "Projectile" entities with linear velocity and TTL.
  - Destruction on wall or enemy contact.
  - Auto-aim applied to projectile velocity vector.

- **Co-op Attacks (ID 9)**:
  - Activable only if not in cooldown. Visual cooldown timer displayed on icon.
