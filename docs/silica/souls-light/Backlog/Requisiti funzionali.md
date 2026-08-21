---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[Specifiche progettuali]]"
tags:
  - functional-requirements
  - game-design
  - user-stories
last modified: 2026-08-15
AI: true
type: Note
sources:
  - "2-allineamento-metodologico.md"
---

# Requisiti funzionali

## Classificazione dei requisiti funzionali

| ID | Epika | Funzionalità | Priorità | Story points | Descrizione |
|----|-------|--------------|----------|--------------|-------------|
| 1 | FALSE | Schermata titolo | ★ | M | Il giocatore può visualizzare la schermata del titolo per scegliere un'opzione di menu. |
| 2 | FALSE | Inizia/Continua Partita | ★ | S | Il giocatore può iniziare una nuova partita o riprendere quella precedente dal menu principale. |
| 3 | FALSE | [[Menu di pausa]] | ★★★☆☆ | S | Il giocatore può aprire il menu di pausa per mettere in pausa il gioco. |
| 4 | FALSE | Termina il gioco | ★ | XS | Il giocatore può chiudere il programma dal menu di pausa o dal menu principale. |
| 5 | FALSE | Selettore classe | ★ | ∞ | Il giocatore può scegliere una classe all'inizio della run per definire statistiche e abilità. |
| 5.1 | FALSE | Classe guerriero | ★ | S | Guerriero: HP elevati, arma base Melee per tankare i danni. |
| 5.2 | FALSE | Classe mago | ★ | M | Mago: attacchi a distanza e alto danno per colpire da lontano. |
| 6 | FALSE | Sistema di salvataggio | ★ | L | Il giocatore può salvare i propri progressi per continuare una partita precedentemente salvata. |
| 7 | FALSE | Movimento del giocatore | ★ | S | Il giocatore può muovere il proprio personaggio sulla mappa per esplorare e schivare gli attacchi. |
| 8 | TRUE | Attacco primario | ★ | ∞ | Il giocatore può eseguire un attacco primario per colpire e sconfiggere i nemici. |
| 8.1 | FALSE | Attacco melee | ★★★☆☆ | S | Attacco base con arma fisica. |

## Requisiti audio

- **Diegetic sounds (ID 22.3)**: Ogni tipo di attacco primario (Melee, Ranged, Skill) ha un suono unico. Ogni nemico ha un suono di "Morte". Interazioni ambientali (porte/casse) riproducono suoni contestuali.


## Additional notes: requisiti non funzionali (from 2-allineamento-metodologico.md)

Non-functional requirements (NFRs) define quality attributes and constraints the system must meet, distinct from functional user stories.

- **Performance**:
  - System responsiveness and speed under load.
  - Enemy AI reaction time to player entry in an area: < 0.5 seconds.
  - Pathfinding success rate on complex maps: > 95%.

- **Usability**:
  - Tutorial and first critical interactions (e.g., weapon swap, inventory open) completion time: < 10 minutes.
  - Critical user error rate during first user test: < 5% (e.g., failing to revive Nox).

- **Security**:
  - Protection from unauthorized access to save files and game state.

- **Interoperability**:
  - Save files from previous version (V-1) must load and function 100% in current version (V).
