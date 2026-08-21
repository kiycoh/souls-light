---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[Game Design]]"
  - "[[Shader]]"
  - "[[Player State]]"
  - "[[Mu]]"
tags:
  - filtro-visivo
  - crt
  - effetti-post-processing
  - gdx-vfx
last modified: 2026-08-15
AI: true
sources:
  - "3-part.md"
type: Note
---

# CRT (Cathode Ray Tube) Filter

Filtro visivo che simula l’estetica delle vecchie televisioni CRT tramite scanlines e distorsioni analogiche. Può essere applicato globalmente o limitato a zone "glitch" della mappa per enfatizzare effetti di instabilità percettiva o danno al giocatore.

- **Effetti principali**:
  - Scanlines orizzontali che ricordano la scansione raster dei tubi catodici.
  - Aberrazione cromatica progressiva legata alla salute del giocatore: aumento dell’effetto man mano che il giocatore subisce danni o si avvicina a "Oblivion", rappresentando la "luce che svanisce" dell’anima.
  - Distorsione a onda d’urto (shader di distorsione) per simulare l’impatto di attacchi come "Collasso della Realtà".

- **Implementazione**:
  - Utilizzo di librerie come **gdx-vfx** per gli shader di distorsione e l’applicazione dei filtri post-processing.
  - Integrazione con sistemi di "trauma" della camera: il danno aggiunge un valore di trauma che decade nel tempo, scuotendo la camera tramite rumore Perlin (es. tramite libreria **Joise**) per una sensazione naturale.
