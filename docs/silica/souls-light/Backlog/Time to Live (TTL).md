---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[Projectile]]"
  - "[[addProjectileListener]]"
  - "[[notifyProjectileRequest]]"
  - "[[Cooldown]]"
  - "[[takeDamage]]"
tags:
  - ttl
  - temporal-mechanics
  - projectile
  - parry
last modified: 2026-08-15
AI: true
sources:
  - "2-allineamento-metodologico.md"
type: Note
---

# Time to Live (TTL)

Time to Live (TTL) is a temporal mechanic governing entity lifespan in the game world.

- **Projectile TTL**: Ranged attacks instantiate entities with linear velocity and a TTL. Projectiles are destroyed upon contact with walls or [[Enemies|enemies]].
- **Parry Window**: A reduced temporal window (e.g., 0.2s) determines successful parry activation. Collisions within the window negate damage; timing errors result in full damage and parry cooldown activation.
