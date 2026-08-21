---
parent note: "[[Backlog]]"
related:
  - "[[Backlog]]"
  - "[[GameModel]]"
  - "[[SoulsLightGame]]"
  - "[[GameController]]"
tags:
  - repository-structure
  - gradle
  - lwjgl3
  - java
  - build-system
last modified: 2026-08-15
AI: true
sources:
  - "3-part.md"
type: Note
---

# Paths from Repository Root

La struttura del repository di *Souls Light* organizza il codice sorgente e le risorse in una gerarchia standardizzata, facilitando la manutenzione e lo sviluppo collaborativo.

- **Directory principali**:
  - **`.gradle`**: Directory di configurazione Gradle.
  - **`.idea`**: Directory di configurazione per l’IDE IntelliJ IDEA.
  - **`build`**: Directory generata per i file di build.
  - **`core`**: Modulo principale del progetto contenente il codice sorgente e le risorse.
    - **`core/build`**: Directory di build per il modulo core.
    - **`core/src`**: Directory principale del codice sorgente.
 - **`core/src/main`**: Codice sorgente principale.
 - **`core/src/main/java/io/github/soulslight/controller`**: Package per i controller di gioco.
 - **`GameController.java`**: Controller principale per la gestione del gioco.
 - **`core/src/main/java/io/github/soulslight/model`**: Package per i modelli di gioco.
 - **`GameModel.java`**: Modello principale che rappresenta lo stato del gioco.
 - **`core/src/main/java/io/github/soulslight/view`**: Package per le viste di gioco.
 - **`GameScreen.java`**: Schermata principale del gioco.
 - **`core/src/main/java/io/github/soulslight/SoulsLightGame.java`**: Classe principale dell’applicazione.
    - **`core/build.gradle`**: File di configurazione Gradle per il modulo core.
  - **`lwjgl3`**: Modulo per l’integrazione con LWJGL3 (Libraries for Windowing and Graphics Library).
- **File di configurazione e script**:
  - **`.editorconfig`**: File di configurazione per la formattazione del codice.
  - **`.gitattributes`**: Attributi per la gestione dei file in Git.
  - **`.gitignore`**: File per escludere file e directory dal controllo di versione.
  - **`build.gradle`**: File di configurazione principale di Gradle.
  - **`gradle.properties`**: Proprietà di configurazione per Gradle.
  - **`gradlew`** e **`gradlew.bat`**: Script per l’esecuzione di Gradle su sistemi Unix e Windows.
