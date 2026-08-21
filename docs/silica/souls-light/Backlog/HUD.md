---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[Specifiche progettuali]]"
tags:
  - power-up
  - hud
  - mechanics
last modified: 2026-08-15
AI: true
sources:
  - "2-allineamento-metodologico.md"
type: Note
---

# HUD

- **2x Damage (ID 11.1)**: When obtained, outgoing damage multiplied by 2, incoming damage multiplied by 2. Status icon visible in HUD; visual effect on character (red aura or cracked sprite).
- **Berserker Light (ID 11.2)**: Damage formula: $\text{BaseDmg} \times \left(1 + \left(1 - \frac{\text{CurrentHP}}{\text{MaxHP}}\right)\right)$. Damage multiplier updates dynamically on each hit or heal. Status icon "Berserk" visible in HUD when active (HP < 30%).
- **Arcane Resonance (ID 11.3)**: Projectiles bounce to a second nearby enemy, enabling crowd control for Mage/Lux class.
