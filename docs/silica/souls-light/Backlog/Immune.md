---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[GameModel]]"
  - "[[performSpecialAttack]]"
tags:
  - game-mechanics
  - enemy-behavior
  - invulnerability
  - arena-event
last modified: 2026-08-15
AI: true
sources:
  - "2-allineamento-metodologico.md"
type: Note
---

# Immune

Arena Event (ID 20.1): When entering a room marked with <!> (event), the room rules are modified so that [[Enemies|enemies]] are invulnerable for a limited time, allowing players to test evasion skills.

- **Invulnerability Flag**: Enemies spawn with `Invulnerable = true`; their HP does not decrease when hit.
- **Timer**: A visible on-screen timer (e.g., 30s) counts down the invulnerability period.
- **Door Lock**: Doors open only after the timer expires.
- **Feedback**: Visual feedback (e.g., metallic sound, "IMMUNE" text) is provided when attempting to hit an invulnerable enemy.
