---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[Test-Driven Development (TDD)]]"
  - "[[Codebase]]"
  - "[[GameModel]]"
tags:
  - implementazione
  - tdd
  - libgdx
  - tile-map-dinamica
  - ciclo-infinito
last modified: 2026-08-15
AI: true
sources:
  - "3-part.md"
type: Note
---

# Specifiche Implementative

Il gioco non prevede una vera e propria via d’uscita poiché il protagonista è già morto. Il protagonista diventa il nuovo **Oblivion**, decidendo come riformare il **Mu**. Si avvia così un **ciclo infinito**: in ogni nuova run, il boss finale sarà l’avventuriero precedente.

**Inspo**:
- M.C. Escher
- [[Giovanni Piranesi]]
- AI generated (Kling video, Gemini video)

**Textures**:
- Dead Cells
- Soul Knight

**Implementazione**:
- **Approccio**: **Test Driven Development (TDD)**
- **Tech stack**: IntelliJ IDEA / Visual Studio Code, Java + Gradle
- **Architettura**: LibGDX + Autumn MVC framework + dipendenze
- **Versionamento**: git (GitHub repo)
- **API** (eventualmente)
- **Modalità**: co-op, endless
- **World**: tile map dinamica e randomica generata da una mappa di rumore

**Aspetto visivo del Mu**:
- Un vuoto bianco/spaziale/grigio con texture che ricordano un frattale statico o la neve di un vecchio televisore. Quando il protagonista avanza, il mondo "si solidifica" con colori e forme davanti a lui, per poi dissolversi di nuovo in nulla dietro di lui.
