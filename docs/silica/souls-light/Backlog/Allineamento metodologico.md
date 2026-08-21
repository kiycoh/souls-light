---
parent note: '[[Backlog]]'
related:
- '[[Backlog]]'
- '[[Specifiche progettuali]]'
tags:
- mvc
- agile-scrum
- development-methodology
- roguelite
last modified: 2026-08-15
AI: true
type: Note
- 2-allineamento-metodologico.md
aliases:
- mvp
sources:
  - "2-allineamento-metodologico.md"
  - "3-part.md"
---

# Allineamento metodologico

## Architettura e metodologia

- **Architettura**: MVC (Model-View-Controller) with strict separation of concerns.
- **Metodologia di sviluppo**: Agile Scrum (sprints, daily standups, reviews).

## Strumenti e team

- **Strumenti**: Google Workspace (Drive, Docs, Sheets, Presentation), GitHub, git, Mermaid, draw.io.
- **Team di sviluppo**:
  - Alessandro: Scrum master, Developer
  - Dario: Designer, Developer
  - Giuseppe: Software architect, Developer

## Timeline e milestone

- **18 dic 2025**: Revisione 1, MVP
  - Presentazione fasi del progetto
  - Mostrare MVP funzionante
- **22 gen 2026**: Revisione 2, software versionato
  - Mostrare git history

## Roguelite definizione

Roguelite: videogiochi basati su partite brevi e ripetibili in cui la morte non è definitiva ma parte dell’esperienza. Ogni run è procedurale (livelli, nemici, potenziamenti). Deriva da *Rogue* (anni ’80), ma più accessibile: premiando il giocatore anche in caso di fallimento. Esempi: *The Binding of Isaac*, *Hades*, *Dead Cells*, *Slay the Spire*.


## Additional notes: mvp (from 2-allineamento-metodologico.md)

### Schema logico architettura MVC

- **Controller**: Riceve gli input dell’utente e li manda al model
- **Model**: Gestisce gli input modificando lo stato dell’utente secondo le logiche del gioco
- **View**: Trasmette il cambiamento di stato all’utente

- **Presentation Layer**: Render mappa, Visualizza movimenti, Visualizza barre varie
- **Application Layer**:

## Da: 3-part

- [[Minimum Viable Product (MVP)]] — Definizione e Obiettivi


## Additional notes: mvc (from 3-part.md)

### Schema Logico Architettura MVC

- **Controller**: Componente che riceve gli input dell’utente e li trasmette al model per l’elaborazione.
- **Model**: Gestisce lo stato del gioco e le logiche di business, aggiornando lo stato dell’utente in base alle azioni intraprese.
- **View**: Trasmette all’utente i cambiamenti di stato del model, rendendo visibili le modifiche tramite la rappresentazione grafica.

- **Presentation Layer**: Responsabile del rendering della mappa, della visualizzazione dei movimenti del personaggio e delle barre di stato (es. salute, esperienza).
- **Application Layer**: Strato intermedio che gestisce la logica applicativa e la comunicazione tra model, view e controller.

### Implementazione Pratica

- **Separazione delle Responsabilità**: Il pattern MVC è stato scelto per garantire una chiara separazione tra logica di presentazione, logica applicativa e gestione dello stato del gioco.
- **Vantaggi**: Modularità, manutenibilità e scalabilità del codice, facilitando l’aggiunta di nuove funzionalità e la correzione di bug.
