---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[Specifiche progettuali]]"
tags:
  - requirements
  - audio
  - functional
last modified: 2026-08-15
AI: true
type: Note
sources:
  - "2-allineamento-metodologico.md"
  - "3-part.md"
---

# Classificazione dei requisiti funzionali

Functional requirements classification for Soul’s Light:

- **ID 1**: Title Screen — Player can view the title screen to select a menu option.
- **ID 2**: Start/Continue Game — Player can start a new game or resume a previous one from the main menu.
- **ID 3**: Pause Menu — Player can open a pause menu to pause the game.
- **ID 4**: Terminate Game — Player can exit the game from the title screen or pause menu.

- **Audio Design Requirements**:
  - Menu_Error sound plays on invalid input.
  - Diegetic sounds (ID 22.3): Specific audio cues for player and enemy actions to enhance immersion.
    - Unique sound design per primary attack type (Melee, Ranged, Skill).
    - Each enemy has a "Death" sound.
    - Environmental interactions (e.g., opening doors/chests) trigger contextual sounds.


## Additional notes: classi (from 3-part.md)

### Classi e Attributi nel Contesto del Gioco

- **Tilemap Collider 2D**: Utilizzato per la geometria dei livelli, garantisce collisioni precise tra entità e ambiente.

- **Composite Collider 2D**: Ottimizzazione delle prestazioni per gestire geometrie complesse dei livelli.

- **Interazioni Basate su Trigger**: Meccaniche per il pickup degli oggetti e le zone di danno (es. trappole, aree di danno ambientale).

- **Strategia di Test**:
  - **Unit Test**: Verifica delle singole unità di codice (es. classi, metodi).
  - **Integration Test**: Validazione dell’interazione tra componenti del sistema.
  - **User Acceptance Testing**: Test di accettazione da parte degli utenti finali per garantire che il prodotto soddisfi i requisiti.
  - **Test di Performance**: Valutazione delle prestazioni del gioco sotto carico.

- **Strumenti di Test**: Strumenti specifici per l’automazione dei test (es. framework di testing, suite di strumenti per la generazione di dati di test).

- **Gestione degli Aggiornamenti**: Strategia per la distribuzione degli aggiornamenti del gioco, inclusi meccanismi di patching e versionamento.

- **Diagrammi UML**: Utilizzati per modellare l’architettura del sistema e le relazioni tra classi. Esempio di progressi:
  - **Settimana 1 [05/11/2025 - 06/11/2025]**:
    - Costruzione della trama e delle caratteristiche generali del gioco.
    - Definizione di una linea logico-tematica coinvolgente per le caratteristiche funzionali e logiche.
  - **Settimana 2 [17/11/2025 - 21/11/2025]**:
    - Finalizzazione dell’architettura logica.
    - Identificazione della baseline del software ([[Allineamento metodologico|MVP]]).
    - Classificazione dei requisiti funzionali per importanza.
    - Aggiunta di Story Points (SP) alla lista dei [[Requisiti funzionali|requisiti funzionali]].

- **Pattern Architetturale**:
  - **Model View Controller (MVC)**: Scelto per separare la rappresentazione delle informazioni dall’interazione dell’utente. Il pattern è stato implementato per garantire modularità e manutenibilità del codice.
