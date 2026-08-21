---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[Mu]]"
  - "[[Nox]]"
  - "[[Lux]]"
  - "[[Game Design]]"
tags:
  - mappe
  - generazione-procedurale
  - meccaniche-di-gioco
  - interazione-ambientale
last modified: 2026-08-15
AI: true
sources:
  - "3-part.md"
type: Note
---

# Map Design

Il **Map Design** di **Souls Light** è ispirato al concetto di **Mu** come dimensione vuota e labirintica, in cui la mappa stessa è un elemento narrativo e meccanico. La mappa scompare man mano che il giocatore avanza, simboleggiando la crescita personale e il superamento delle paure.

- **Meccaniche di gioco**:
  - **Movimento**: controllo direzionale a 360° con sistema di camera dinamica.
  - **Screen Shake**: effetto visivo che scuote la camera quando il giocatore viene colpito da un colpo grosso, implementabile tramite manipolazione della camera nel livello View o tramite librerie come **gdx-vfx**.
  - **Attacchi**:
    - **Standard Attack**: attacco primario con auto-aim.
    - **Charged Attacks**: attacchi caricati tramite pressione prolungata del tasto di attacco (dipendenti dall’arma).
    - **Attacco co-op**: animazione in cui **Nox** e **Lux** diventano i punti dello yin e dello yang, rappresentando l’equilibrio tra opposti.
  - **Parry**: meccanica di schivata/parata (solo per armi melee), a basso sforzo di implementazione ma soddisfacente per il giocatore.

- **Design della mappa**:
  - **Tile map dinamica e randomica**: generata tramite mappe di rumore per creare ambienti unici in ogni run.
  - **Struttura**:
    - La mappa scompare man mano che il giocatore avanza, collegabile alla crescita e al superamento delle paure.
    - **Salvataggi pre-ending**: necessari per esplorare i diversi finali del gioco.
  - **Interazione ambientale**:
    - **Nox** interagisce con elementi ombra appartenenti al **Mu** (es. chest, porte, eventi).
    - **Lux** interagisce solo con elementi che appartengono all’etere (es. sussurri cristallizzati, altre luci).

- **Ispirazioni**:
  - **M.C. Escher**: per la complessità spaziale e i percorsi impossibili.
  - **Dead Cells** e **Soul Knight**: per la generazione procedurale e la varietà degli ambienti.

- **Note implementative**:
  - Utilizzo di **LibGDX** per la gestione delle tile map e della generazione procedurale.
  - Integrazione con sistemi di pathfinding A* per la navigazione dinamica.
