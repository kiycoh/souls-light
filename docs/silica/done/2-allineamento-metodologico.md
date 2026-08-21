---
source_file: "/home/kiycoh/Documents/dev/souls-light/docs/silica/Inbox/Soul_s Light Docs.docx"
type: Note
---

## **Allineamento metodologico**


Architettura: MVC

Metodologia di sviluppo: Agile Scrum.

sprint, riunioni giornaliere, revisioni.

Strumenti: Google Workspace (Drive, Docs, Sheets, Presentation), GitHub, git, mermaid, draw.io

Team di sviluppo:

Alessandro: Scrum master, Developer

Dario: Designer, Developer

Giuseppe: Architetto software, Developer

Timeline e Milestones:

18 dic 2025: revisione 1, MVP:

Presentazione fasi del progetto

Mostrare MVP funzionante

22 gen 2026: revisione 2, software versionato:

Mostrare git history


**Schema logico architettura MVC**


Controller

Riceve gli input dell’utente e li manda al model


MODEL

Gestisce gli input modificando lo stato dell’utente secondo le logiche del gioco


View

Trasmette il cambiamento di stato all’utente


PRESENTATION LAYER


Render mappa

Visualizza movimenti

Visualizza barre varie


APPLICATION LAYER


Gestione Input

Gestione IA

Gestione logica di gioco (danni, cambiamento mappa, spawn nemici)

Gestione cambiamento di stato


DATA LAYER


Memorizza informazioni personaggi

Memorizza livelli superati


COPIONE SLIDE REV 1

RogueLite?

I roguelite sono un genere di videogiochi basati su partite brevi e ripetibili, in cui la morte non è una fine
definitiva ma parte dell’esperienza.

Ogni run è diversa dalla precedente: livelli, nemici o potenziamenti vengono spesso generati in modo
procedurale. Quando perdi, ricominci da capo… ma quasi sempre portandoti dietro qualcosa, come abilità
sbloccate, upgrade permanenti o nuove possibilità di gioco.

Il termine deriva da Rogue, un gioco degli anni ’80, molto punitivo, in cui la morte era permanente e non
c’erano progressi tra una partita e l’altra. I roguelite prendono quell’idea di rischio e imprevedibilità, ma la
rendono più accessibile, premiando il giocatore anche quando fallisce.

Alcuni esempi famosi sono The Binding of Isaac, Hades, Dead Cells e Slay the Spire: giochi diversi tra loro, ma
tutti basati sul ciclo “gioca, muori, migliora e riprova”.

In breve, i roguelite trasformano il fallimento in progresso, e fanno della ripetizione il cuore del gioco.

Benvenuti nel Mu

Soul's Light è ambientato nel Mu, un concetto che abbiamo sviluppato per giustificare le meccaniche roguelike
del gioco.

Per farla breve, dopo la morte non c'è Paradiso o inferno. Tutte le anime finiscono nel Mu, un limbo eterno
dove le anime sono costrette a vagare per l'eternità fino a perdere il senno.

Dal punto di vista del world-building, il Mu rappresenta una dimensione di confine: un limbo caratterizzato
dall'assenza di materia stabile, Il Mu è un luogo mutevole, plasmato dalla volontà dei giocatori di fuggirne. Le
"stanze" esistono perché i protagonisti vogliono scappare. tanto che le stanze già superate cessano di esistere al
momento in cui si esce.

Spiegare brevemente che il dungeon è generato dalla volontà del protagonista di fuggire. Cita la dinamica
cooperativa asimmetrica tra Lux e Nox (uno combatte, l'altro interagisce/rianima).


Schema base dei controlli

La bozza dei controlli che useremo, dual stick, è usato da moltissimi giochi bulllet-hell/roguelike come Isaac,
Enter the Gungeon, Hades

Introduzione ad alcune delle classi

Il gioco è costruito per far si che nessuna classe sia un Jack of all traits. Molte delle classi sono strutturate in
modo da coprire le debolezze delle altre, ad esempio il Guerriero può proteggere il fragile mago dal fuoco dei
nemici mentre il mago pensa da lontano ai nemici più forti.

Esperienza Coop + Introduzione ad alcune delle classi

alcuni nemici e situazioni sono progettati per spingere attivamente alla collaborazione, affrontarli da soli
risulta più complesso o inefficiente, mentre il lavoro di squadra apre strategie alternative. Questo sistema
incentiva anche una scelta più consapevole della classe iniziale, favorendo ruoli di supporto o difensivi che
diventano fondamentali in determinate situazioni ma sono estremamente deboli da soli.

Inoltre ci sono alcune interazioni specifiche con il mondo che solo uno dei giocatori può fare.

Creazione mappa/sistema layers

Il programma utilizza un sistema di layer, ottimo per randomizzare l'estetica delle stanze di un rogue lite.

[Spiegazione layer e randomizzazione]

Sistema di sprite sheet

Il programma scorre e gestisce gli sprite che compongono un’animazione per costruire il risultato finale a
schermo.

In questo modo l’animazione non è un blocco rigido, ma una sequenza controllabile: è possibile modificare
facilmente le tempistiche, come la durata dei singoli frame o la velocità complessiva, senza dover intervenire
sul materiale grafico originale.

Inoltre, il sistema permette di richiamare frame specifici quando necessario — ad esempio per eventi
particolari, transizioni o reazioni del personaggio — indipendentemente da come o da dove l’animazione sia
stata importata.

Questo approccio rende l’animazione più flessibile, riutilizzabile e adattabile alle esigenze del gameplay


Workflow

Questo è uno spezzone della nostra gargantuesca tabella delle user story, organizzate per priorità in base al
planning poker

[breve spiegazione della tabella]

In base a questo abbiamo stabilito una base line e assegnato alcune delle task al primo sprint

Poi siamo passati al costruire i diagrammi e i test per il TDD

Passiamo ad un esempio specifico [spiegazione implementazione mappe]

[Far vedere da IntelliJ]

Spiega la distinzione tra economia "intra-run" (Gold) e "meta-game" (Determinazione).

💿 Requisiti non funzionali


**Requisiti non funzionali**


I Requisiti Non Funzionali (RNF) sono cruciali in quanto definiscono gli attributi di qualità e i vincoli che il
sistema deve rispettare, senza descrivere una funzionalità specifica come fanno le User Stories.


Questi requisiti stabiliscono come il gioco deve funzionare, coprendo aspetti fondamentali quali:

Prestazioni: Velocità e reattività.

Usabilità: Facilità d'uso e apprendimento.

Sicurezza: Protezione da accessi non autorizzati.

Manutenibilità: Facilità di correzione e miglioramento.

Scalabilità: Capacità di gestire future espansioni.

A ciascun requisito non funzionale sono associate le relative misure.


Il sistema deve garantire l'integrità dei file e prevenire gli accessi non autorizzati

Hashing ai file di mappa, tasso di violazione, tasso di errore. Tasso di corruzione dei file di salvataggio/mappa
inferiore al 5% (?) in condizioni di gioco normali. Resistenza alla manomissione dei file di salvataggio


Il gioco non deve occupare troppo spazio

Il file di installazione/gioco (esclusi salvataggi utente) deve occupare meno di 600 MB al momento del rilascio.


Il gioco deve essere fluido

La frequenza di aggiornamento (frame rate) deve essere di almeno 60 fps (Target) e non scendere sotto i 30 fps
(Minimo accettabile) per più di 1 secondo su hardware raccomandato.


Il gioco deve avere un'interfaccia utente facile da usare

Tempo medio di completamento del tutorial e delle prime interazioni critiche (es. cambio arma, apertura
inventario) inferiore a X minuti. Tasso di errori critici dell'utente (es. non capire come rianimare Nox) inferiore
al 5% durante il primo test utente.


Il gioco deve avere una UX comprensibile

Tempo medio di completamento del tutorial e delle prime interazioni critiche (es. cambio arma, apertura
inventario) inferiore a 10 minuti. Tasso di errori critici dell'utente (es. non capire come rianimare Nox)
inferiore al 5% durante il primo test utente.


I nemici devono poter attaccare il giocatore in maniera veloce ed efficiente

Tempo di reazione dell'IA nemica all'ingresso del giocatore in un'area inferiore a 0.5 secondi. Tasso di successo
del Pathfinding nemico su mappe complesse > 95%.

Affidabilità e stabilità, il gioco non deve bloccarsi o crashare

MTBF (Mean Time Between Failures - Tempo Medio tra i Fallimenti) > 10 ore di gioco ininterrotto. / Tasso di
crash dovuto a eccezioni non gestite < 0.01% delle sessioni di gioco.


Interoperabilità dei Salvataggi

I file di salvataggio di una versione precedente (V-1) devono essere caricabili e funzionanti al 100% nella
versione corrente (V).


`🙋` 🏻 Requisiti funzionali


**Requisiti funzionali con User Stories**


Vedi anche Sheet requisiti funzionali

Le User Stories sono una componente fondamentale della nostra metodologia Agile Scrum, poiché definiscono i
requisiti funzionali del gioco dal punto di vista dell'utente finale (il giocatore). In Scrum, una User Story deve
seguire il principio INVEST: Independent, Negotiable, Valuable, Estimable, Small e Testable.Ogni storia è
formulata in modo conciso per descrivere una funzionalità desiderata, seguendo il formato:


"As [user], I want [objective], so that [advantage]".


A ciascuna User Story sono associati i relativi criteri di accettazione, che stabiliscono le condizioni necessarie
per considerare una funzionalità completa e prevedibile, fungendo da base per i test e validazione del lavoro. I
casi d’uso in rosso sono requisiti essenziali senza cui il software non potrebbe funzionare correttamente.


**1. Basics & Menu**


Title Screen (ID 1): As a player, I want to view the screen menu so that I can choose a menu option.

Interagire con il menu per selezionare 'new game' o 'load game'.

Dal menu principale si possono aprire le impostazioni.

Start/Continue Run (ID 2): As a player, I want to start a new run from the main menu or resume the previous
one so that I can play the game.

Iniziare una nuova run dal menu principale.

Riprendere la partita precedente (se presente salvataggio).

Pause Menu (ID 3): As a player, I want to open the pause menu so that I can pause the game.

Durante la partita, è possibile mettere in pausa il gioco aprendo il menu di pausa.

Nemici e proiettili smettono di muoversi quando il gioco è in pausa.

End Game (ID 4): As a player, I want to end the program from the pause menu or main menu so that I can quit
the game.

Aprire il menu di pausa e selezionare 'Exit' senza perdere i progressi salvati.

Chiudere il programma dal menu principale.

Save System (ID 6): As a player, I want to save my progress so that I can load the previous game and continue
the previous run.

Durante la partita, aprire il menu di pausa e premere il tasto Savedata_button per salvare i dati di gioco.

Inventory (ID 19): As a player, I want to open the inventory screen so that I can check my items and stats.

Durante la partita, è possibile controllare le statistiche del personaggio e gli oggetti collezionati tramite un
menu dedicato.


**2. Character & Classes**


Class Selector (ID 5 - Epica): As players, we want to choose a class at the beginning of the run so that we can
decide our stats and future abilities.

L'UI mostra nome e descrizione della classe.

Il Controller riceve l'input di selezione.

Il Model aggiorna lo stato del giocatore con un "template" diawd base vuoto.

Warrior Class (ID 5.1): As a player choosing the Warrior, I want to start with high HP and the basic Melee
weapon so that I can tank incoming damage.

Selezione "Guerriero" imposta HP base a un valore elevato (X).

Aggiunge "Spada Base" all'inventario.

Disabilita uso di mana/will per attacchi range (se previsto dalle regole di classe).

Mage Class (ID 5.2): As a player choosing the Mage, I want to have ranged attacks and high damage so that I
can hit enemies from afar.

Selezione "Mago" imposta HP base a un valore ridotto (Y).

Aggiunge "Bastone/Proiettile Magico" all'inventario.


**3. Movement & Combat System**


Player Movement (ID 7): As a player, I want to move my character on the map so that I can explore the Mu and
dodge enemy attacks.

Il personaggio si muove fluidamente con il controllo direzionale.

Il personaggio si ferma al rilascio del controllo.

Il personaggio non attraversa muri o ostacoli (collision detection).

Primary Attack (ID 8 - Epica): As a player, I want to press the attack button so that I can perform a primary
attack to hit and defeat the shadows.

Quando un nemico è a portata e si preme attack_button, il personaggio attacca.

L'attacco ha auto-aim verso il nemico.

L'attacco entra in cooldown dopo l'uso.

Se nessun nemico è a portata, l'attacco è sferrato nella direzione del personaggio.

Melee Attack (ID 8.1): As a player (Nox), I want the character to attack in the direction I flick the stick to, so
that I can choose the attack’s direction.

L'attacco va a segno quanto hitbox del nemico e hurt-box

del giocatore si "intersecano".

Parry Mechanic (ID 8.2): As a player (Melee), I want to parry a hit by pressing a key at the right moment so
that I can negate incoming damage.

Se la direzione dello stick destro è la stessa della direzione in cui ti arriva l’attacco, l’attacco nemico viene
“schivato”.

Finestra temporale ridotta per l'attivazione (es. 0.2s).

Se avviene collisione durante la finestra -> Danno subito è 0.

Se timing errato -> Danno pieno + Cooldown parry attivato.

Ranged Attack (ID 8.3): As a player (Mage/Lux), I want to fire projectiles that travel across the map so that I
can damage enemies from a distance.

Istanzia entità "Projectile" con velocità lineare e TTL (Time to live).

Distruzione del proiettile su contatto con Muro o Nemico.

Auto-aim applicato al vettore velocità del proiettile.

Co-op Attacks (ID 9): As a player, I want to activate a special ability by pressing the dedicated button so that I
can gain a tactical advantage.

Se l'abilità non è in cooldown, premendo Skill_button si attiva l'effetto.

L'icona mostra visivamente il tempo di ricarica.

L'abilità non è attivabile se in cooldown.

Weapon Switch (ID 15): As a player, I want to quickly switch weapons so that I can adapt my strategy to the
situation.

Raccogliere un'arma da terra per sostituirla alla propria tramite tasto Interagisci.

Special Ability (ID 16 - Epica): As a player, I want to activate a special ability via the dedicated hotkey so that I
can obtain a tactical advantage.

Abilità speciali sbloccate e abbastanza volontà permettono di premere SpecialAttack_button.

È richiesta sufficiente volontà e cooldown a zero per l'attivazione.

Skill Activation (ID 16.1): As a player, I want to activate the special ability if I have enough "Will", so that I can
gain an immediate advantage.

Alla pressione di SpecialAttack_button, il sistema verifica se CurrentWill >= SkillCost.

Se vero: decrementa Will, esegue l'abilità, resetta il cooldown.

Se falso: riproduce suono "errore" o feedback visivo (UI lampeggiante).

Cooldown System (ID 16.2): As a player, I want to see when the ability is ready so that I don't waste inputs
uselessly.

Dopo l'uso, l'abilità entra in stato Cooldown per X secondi.

L'UI mostra un overlay scuro o un timer sull'icona della skill.

L'input viene ignorato dal Controller se Cooldown > 0.

"Collapse" Ability (ID 16.3): As Nox, I want to use the "Reality Collapse" ability so that I can damage enemies in
an area around me.

Genera un'area di danno circolare (Box2D Sensor) centrata sul player.

Applica danno immediato a tutti i nemici nell'area.

Applica un leggero Knockback ai nemici colpiti.


**4. Enemies**


Enemy Ecosystem (ID 10 - Epica): As players, we want to face different types of enemies so that the gameplay
is engaging.

Avanzando nel dungeon la varietà di nemici spawnati aumenta.

Basic Enemy "Chaser" (ID 10.1): As a player, I want to face enemies that actively chase me so that I am forced
to move continuously.

Stato Idle/Patrol se il player è lontano.

Stato Chase se il player entra in LineOfSight (uso A* Pathfinding).

Danno a contatto fisico + knockback.

Charging Enemy "Spiked Ball" (ID 10.2): As a player, I want to face enemies that perform fast but predictable
charges (telegraphed) so that I can dodge them.

"Blink" o segnale visivo di 1s prima della carica.

Impulso fisico lineare ad alta velocità (nessun cambio direzione durante la carica).

Stato "Fatigue" dopo la carica (il nemico diventa immobile/vulnerabile).

Pusher Enemy (ID 10.3): As a player, I want an enemy that pushes me away instead of killing me, so that
environmental traps become dangerous.

Il danno HP inflitto è 0.

Applica una forza fisica (Knockback) massiva al contatto.

L'AI cerca di spingere il player verso zone di pericolo.

Shield Enemy (ID 10.4): As a player, I want an enemy that protects other monsters so that I have to choose my
target strategically.

HP molto alti.

Non attacca il player.

Si interpone attivamente sulla linea di tiro tra player e altri nemici.

Muore automaticamente se è l'ultimo nemico rimasto nella stanza.

Sorcerer Enemy (ID 10.5): As a player, I want to avoid dangerous areas created by magic enemies so that I
have to manage my spatial positioning.

Fuoco: Crea indicatore a terra -> Esplosione dopo X secondi.

Ghiaccio: Lancia proiettile che applica status "Slow" (riduzione velocità nel Model).

Comportamento Kiting (mantiene la distanza dal giocatore).


**5. Progression & Power-ups**


Temporary Power-ups (ID 11 - Epica): As a player, I want to obtain power-up (and similar items) so that I can
go further in the current run attempt.

Dopo aver completato tre stanze, si può scegliere un potenziamento gratuito da una schermata con tre opzioni.

Selezionare 'obtain' applica il power-up alla run corrente.

2x Damage (ID 11.1): As a player, I want to accept a "double-edged" power-up that doubles both damage dealt
and received, so that I can gamble on my dodging skills.

Applicazione di un modificatore x2 al calcolo del danno in uscita.

Applicazione di un modificatore x2 al danno in entrata.

Icona di stato visibile nell'HUD.

Effetto visivo sul personaggio (es. aura rossa o sprite "crepato").

Berserker Light (ID 11.2): As a player, I want to deal more damage the lower my HP is, so that I can exploit
critical situations.

Formula danno: BaseDmg * (1 + (1 - CurrentHP/MaxHP)).

Aggiornamento dinamico del moltiplicatore danno ad ogni colpo subito/curato.

Icona di stato "Berserk" visibile nell'HUD quando attiva (HP < 30%).

Arcane Resonance (ID 11.3): As a Mage/Lux, I want my projectiles to bounce to a second nearby enemy, so
that I can manage large groups.

Quando un proiettile colpisce Nemico A, crea un raggio verso Nemico B (se entro raggio R).

Nemico B subisce il 50% del danno originale.

Effetto visivo di "catena" tra i due nemici.

Permanent Power-ups (ID 12 - Epica): As a player, I want to spend "Determination" at the start of a run so that
I can permanently increase the stats of my character.

Dopo essere morti e aver guadagnato valuta, si può interagire con un NPC con Interact_button.

L'NPC vende potenziamenti permanenti acquistabili con Buy_button.

Merchant Interaction(ID 12.1): As players, we want to interact with the Merchant NPC so that we can see and
buy available upgrades.

L'NPC spawna nella scena di "Start/Hub" dopo una morte.

L'interazione apre una UI dedicata (Scene2D Window).

Mostra il saldo della valuta permanente accumulata.

Stats Purchase (ID 12.2): As a player, I want to buy a permanent Max HP increase so that I can survive longer
in the next run.

Premendo "Buy" su "Max HP Up", verifica il saldo valuta.

Deduce il costo e incrementa BaseMaxHP nel file di salvataggio globale (JSON).

La prossima New Run istanzia il player con i nuovi HP base.


**6. Death, Interaction & Events**


Death & Respawn (ID 13 - Epica): As a player, I want to restart from the beginning of the dungeon when my
HP reaches 0 so that I can start a new run.

Gli HP a zero terminano la run corrente.

Schermata di dialogo per tornare alla stanza iniziale del dungeon o alla schermata iniziale.

Mappa, nemici ed eventi vengono generati casualmente per la nuova run.

Revive (ID 13.1): As Lux, I want to use part of Will’s energy so that I can revive Nox.

Se il compagno (Nox) è a terra e la barra di Will è sufficiente, interagendo con lui si consuma parte della barra
di Will.

Il compagno torna in vita con HP predefiniti.

Se la barra di Will non è sufficiente, l'azione non è disponibile.

Next Room Choice (ID 14): As players, we want to choose the door of the next room once we have completed
the current one, so that we can continue the dungeon before the current room collapses.

Tutti i nemici sconfitti attivano le porte di uscita.

Entrambi i giocatori devono posizionarsi sulla stessa porta per confermare.

Un evento negativo si verifica se il tempo scade prima della scelta.

Environment Interaction (ID 17 - Epica): As Lux, I want to use my Will to interact with the scenario, so that I
can discover secret passages or stashed loot.

Controllando Lux, un oggetto interagibile si illumina.

Attivando l'interazione si apre un nuovo passaggio o appare un frammento di lore.

Nox non può eseguire questa interazione da solo.

Passive Reveal (ID 17.1): As Lux, I want hidden objects to become visible when I approach, so that I can
identify secrets.

Implementare un Sensor circolare attorno a Lux.

Se il sensore collide con un oggetto flaggato Hidden, l'oggetto cambia alpha da 0 a 1 (o diventa luminoso).

Se Lux si allontana, l'oggetto torna nascosto.

Active Interaction (ID 17.2): As players, we want to interact with (revealed) objects, so that we can obtain loot.

Se l'oggetto è "Rivelato" e il giocatore preme Interact_button.

Verifica se Will > Cost.

Modifica lo stato dell'oggetto (es. Muro -> distruzione, Cesta -> Drop Loot).

Nox non deve poter attivare questo trigger.

NPC Interaction (ID 18 - Epica): As a player, I want to interact with NPCs so that I can speak with them.

Incontrando NPC si può interagire con loro tramite Interact_button.

Gestione dialoghi o apertura shop.

Merchant (ID 18.1): As a player, I want to spend gold gathered in the current run in a shop so that I can
acquire power-ups and/or weaponry.

Accumulando valuta spendibile, si può premere il tasto 'acquista' nell'UI del mercante.

Verifica valuta sufficiente e aggiunge item.

Scammer (ID 18.2): As players, we want to speak with a scammer and spend gathered money so that we can
choose to remove a temporary power-up.

I giocatori possono parlare con uno scammer.

Spendendo denaro raccolto, possono rimuovere un power-up temporaneo indesiderato.


**7. Random Events (Room Modifiers)**


Room Modifiers (ID 20 - Epica): As players, we want to be surprised by random events that can happen when
entering a room so that the exploration is more exciting.

Quando si entra in una stanza segnalata con <!> (evento), avviene una modifica alle regole della stanza.

Arena Event (ID 20.1): As players, we want to face a room where enemies are invulnerable for a limited time,
so that we can test our evasion skills.

Timer visibile a schermo (es. 30s).

Nemici spawnati con flag Invulnerable = true (HP non scendono).

Le porte si aprono solo allo scadere del timer.

Feedback visivo (es. suono metallico, scritta "IMMUNE") quando si colpisce un nemico invulnerabile.

Wind Event (ID 20.2): As a player, I want to compensate for a constant physical force pushing me in one
direction, so that movement becomes more challenging.

Applicazione di una forza costante (applyForceToCenter Box2D) su tutti i corpi dinamici (Player).

Particelle visive (foglie/polvere) indicano la direzione del vento.

La forza è sufficiente a spostare un giocatore fermo, ma superabile camminando controvento.

Darkness Event (ID 20.3): As players, we want to explore completely dark rooms illuminated only by our light,
so that tension and cooperation are increased.

AmbientLight della mappa settato a quasi zero (0.1f).

Rendering di PointLight (Box2DLights) agganciate ai player (Lux e Nox).

I nemici fuori dal cono di luce sono invisibili o parzialmente oscurati.

Lux ha un raggio di luce leggermente maggiore di Nox.

Gravitational Anomaly Event (ID 20.4): As a player, I want to experience sudden gravity changes, so that I can
exploit physics in combat.

Modifica temporanea del vettore Gravity nel World Box2D.

I proiettili e i nemici subiscono la stessa alterazione.

Effetto di "fluttuazione" (damping ridotto) quando ci si muove.

Time Glitch Event (ID 20.5): As a player, I want the game speed to randomly accelerate or slow down (diegetic
"Lag"), so that the instability of the "Mu" is represented.

Modifica dinamica del moltiplicatore deltaTime nel Game Loop.

Effetto visivo "Chromatic Aberration" (gdx-vfx) che si intensifica quando il tempo rallenta/accelera.

La musica subisce un leggero pitch shift coerente con la velocità.

Shadow Loot Event (ID 20.6): As Nox, I want to encounter an enemy with the equipment of my previous run so
that I can recover lost loot.

Alla morte, serializzare inventario/stats su JSON.

Probabilità di spawn "Shadow Enemy" in una nuova run speciale.

Lo Shadow Enemy usa l'arma salvata nel JSON.

Drop del vecchio inventario alla sconfitta dell'ombra.

Serenity Event (ID 20.7): As players, we want to occasionally find a safe room where time does not flow and a
benevolent entity heals us, so that we can plan the next move without anxiety.

Il timer di collasso della stanza è disabilitato/invisibile.

Nessun nemico spawna nella stanza.

NPC 'Serene' presente: Interazione con tasto dedicato ripristina HP e Scudo al 100%.

L'interazione può avvenire una sola volta per run della stanza


**8. Economy**


Economy (ID 21): As players, we want to spend currencies to upgrade character abilities and face enemies with
more ease.

All'inizio di una run la Will aumenta con l'uccisione di nemici.

Ogni volta che attraversi una porta la Determinazione aumenta.

I nemici morti, le interazioni e gli eventi random possono droppare Oro da spendere con i mercanti.

Gold economy (ID 21.1): As players, we can collect gold to spend at merchants for temporary power-ups.

L'Oro droppa randomicamente da nemici sconfitti.

L'Oro è aggiunto al saldo temporaneo del giocatore.

L'Oro può essere speso presso un mercante.

Will economy (ID 21.2): As players, we want to use 'Will' as a temporary resource to activate special abilities
and environmental interactions.

La Will si ricarica uccidendo nemici.

La Will ha un tetto massimo.

Azzerata ad ogni run.

Determination Economy (ID 21.3): As players we want to earn determination by making progress in the
dungeon (killing all the enemies).

La determinazione aumenta ad ogni passaggio di stanza

La determinazione permette di sbloccare bonus o eventi al raggiungimento di checkpoints.

La determinazione viene azzerata ad ogni run.


**9. Audio management**


Audio Management (ID 22): As players, we want the game audio to be dynamic and react to our actions so that
we can feel more immersed in the game.

Tutti i requisiti audio specifici (22,1; 22,2; 22,3) sono implementati e funzionanti.

I volumi di gioco sono regolabili tramite menu di pausa.

Dynamic music (ID 22.1): As players, we want the background music to become more intense when we are in
combat to increase the perceived intensity of the encounter.

La musica cambia da "Esplorazione" a "Combattimento" entro 1 secondo dall'ingaggio del primo nemico.

La musica torna a "Esplorazione" 3 secondi dopo la sconfitta dell'ultimo nemico nella stanza.

Menu sounds (ID 22.2): As a player, I want to hear short sound bites when interacting with the game menus so
that I can receive feedback on my inputs.

Un suono Menu_Select viene riprodotto alla pressione di un pulsante.

Un suono Menu_Back viene riprodotto quando si torna indietro.

Un suono Menu_Error viene riprodotto in caso di input non valido.

Diegetic sounds (ID 22.3): As players, we want to hear specific audio cues based on our actions and enemy
actions so that we can better immerse ourselves in the game's atmosphere.

Ogni tipo di attacco primario (Melee, Ranged, Skill) ha un suo sound design unico.

Ogni nemico ha un suono di "Morte".

Le interazioni ambientali (es. apertura porta/cassa) riproducono suoni contestuali.

↕️ Classificazione requisiti funzionali


**Classificazione dei requisiti funzionali**


ID

Epika

Funzionalità

Priorità

Story points

Descrizione

1

FALSE

Schermata titolo


★

M

Il giocatore può visualizzare la schermata del titolo per scegliere un'opzione di menu.

2

FALSE

Inizia/Continua Partita


★

S

Il giocatore può iniziare una nuova partita o riprendere quella precedente dal menu principale.

3

FALSE

Menu di pausa


★★★☆☆

S

Il giocatore può aprire il menu di pausa per mettere in pausa il gioco.

4

FALSE

Termina il gioco


★

XS

Il giocatore può chiudere il programma dal menu di pausa o dal menu principale.

5

TRUE

Selettore classe


★

∞

Come giocatore, voglio vedere una schermata (o un pannello nel menu Start) che mi permetta di scorrere tra le
opzioni disponibili.

Il giocatore può scegliere una classe all'inizio della run per definire statistiche e abilità.

5,1

FALSE

Classe guerriero


★

S

Come giocatore che sceglie il Guerriero, voglio iniziare con HP elevati e l'arma base Melee per poter tankare i
danni.

5,2

FALSE

Classe mago


★

M

Come giocatore che sceglie il Mago, voglio avere attacchi a distanza e alto danno per colpire da lontano.

6

FALSE

Sistema di salvataggio


★

L

Il giocatore può salvare i propri progressi per continuare una partita precedentemente salvata.

7

FALSE

Movimento del giocatore


★

S

Il giocatore può muovere il proprio personaggio sulla mappa per esplorare e schivare gli attacchi.

8

TRUE

Attacco primario


★

∞

Il giocatore può eseguire un attacco primario per colpire e sconfiggere i nemici.

8,1

FALSE

Attacco melee


★★★☆☆

S

Come giocatore (Nox), voglio che il personaggio attacchi nella direzione in cui inclino lo stick

8,2

FALSE

Meccanica parry


★★☆☆☆

L

Come giocatore (Melee), voglio parare un colpo premendo un tasto al momento giusto per negare il danno.

8,3

FALSE

Attacco a distanza


★★★★☆

M

Come giocatore (Mage/Lux), voglio sparare proiettili che attraversano la mappa per danneggiare i nemici da
lontano.

9

FALSE

Attacchi co-op


★★☆☆☆

L

I giocatori può attivare un'abilità speciale premendo il pulsante dedicato per ottenere un vantaggio tattico.

10

TRUE

Ecosistema nemici


★

∞

Il giocatore deve incontrare una varietà di nemici

10,1

FALSE

Nemico base (Chaser)


★

S

Come giocatore, voglio affrontare nemici che mi inseguono attivamente per essere costretto a muovermi.

10,2

FALSE

Nemico carica (Spiked Ball)


★★☆☆☆

L

Come giocatore, voglio affrontare nemici che effettuano cariche veloci ma prevedibili (telegrafate).

10,3

FALSE

Nemico pusher


★★★☆☆

XL

Come giocatore, voglio un nemico che mi spinga via invece di uccidermi, per rendere pericolose le trappole.

10,4

FALSE

Nemico scudo


★★☆☆☆

S

Come giocatore, voglio un nemico che protegga gli altri mostri per dover scegliere strategicamente il target.

10,5

FALSE

Stregone


★★★★☆

L

Come giocatore, voglio evitare aree pericolose create da nemici magici per gestire il posizionamento spaziale.

11

TRUE

Power-up temporanei


★★★★☆

∞

Il giocatore può ottenere power-up per progredire nella run corrente.

11,1

FALSE

2x Danno


★★☆☆☆

M

Come giocatore, voglio accettare un potenziamento "doppio taglio" che raddoppia i danni inflitti e subiti, per
scommettere sulla mia abilità di schivata.

11,2

FALSE

Berserker


★★☆☆☆

M

Come giocatore, voglio infliggere più danni quanto più bassi sono i miei HP, per sfruttare le situazioni critiche.

11,3

FALSE

Risonanza Arcana


★★☆☆☆

L

Come Mago/Lux, voglio che i miei proiettili rimbalzino su un secondo nemico vicino, per gestire gruppi
numerosi.

12

TRUE

Power-up permanenti


★

∞

Il giocatore può spendere valuta all'inizio di una run per aumentare permanentemente le statistiche.

12,1

FALSE

Interazione mercante


★★☆☆☆

M

Come giocatore, voglio interagire con l'NPC Mercante per vedere i potenziamenti disponibili.

12,2

FALSE

Acquisto potenziamenti

permanenti


★★☆☆☆

L

Come giocatore, voglio comprare un aumento di Max HP permanente per sopravvivere di più nella prossima
run.

13

TRUE

Morte & respawn


★

∞

Il giocatore riparte dall'inizio del dungeon quando gli HP raggiungono 0 a meno che non venga rianimato o
respawni

13,1

FALSE

Rianimazione (Lux)


★★☆☆☆

M

Lux può usare parte dell'energia di Will per rianimare Nox.

14

FALSE

Scelta stanza successiva


★

L

I giocatori scelgono la porta della stanza successiva dopo aver completato quella attuale.

15

FALSE

Cambio arma


★☆☆☆☆

M

Il giocatore può cambiare arma per adattare la strategia.

16

TRUE

Usa abilità speciali


★★★☆☆

∞

Il giocatore può attivare un'abilità speciale tramite hotkey per ottenere un vantaggio tattico.

16,1

FALSE

Attivazione Skill


★★★★☆

M

Come giocatore, voglio attivare l'abilità speciale se ho abbastanza "Will", per ottenere un vantaggio immediato.

16,2

FALSE

Cooldown System


★★★★☆

S

Come giocatore, voglio vedere quando l'abilità è pronta, per non sprecare input inutilmente.

16,3

FALSE

Abilità "Collapse"


★★★☆☆

L

Come Nox, voglio usare l'abilità "Reality Collapse" per danneggiare nemici in un'area attorno a me.

17

TRUE

Interazione con ambiente


★★★☆☆

∞

Lux può usare la sua Will per interagire con lo scenario e scoprire passaggi segreti o bottino.

17,1

FALSE

Rivelazione Passiva


★★☆☆☆

M

Come Lux, voglio che oggetti nascosti diventino visibili quando mi avvicino, per identificare segreti.

17,2

FALSE

Interazione Attiva


★★★☆☆

L

Come giocatori, voglio poter interagire con un oggetto (rivelato) per ottenere loot.

18

TRUE

Interazione con NPC


★★☆☆☆

∞

Il giocatore può interagire con gli NPC generici per saperne di più sulla lore del gioco.

18,1

FALSE

Mercante


★★☆☆☆

M

I giocatori possono parlare con un mercante e spendere denaro raccolto per acquisire power-up e/o armi.

18,2

FALSE

Scammer


☆

S

I giocatori possono parlare con uno scacmmer e spendere denaro raccolo per poter scegliere di togliere un
power up

temporaneo.

19

FALSE

Inventario


★★☆☆☆

M

Il giocatore può aprire la schermata dell'inventario per controllare oggetti e statistiche.

20

TRUE

Room modifiers

(eventi random)


★★☆☆☆

∞

I giocatori possono incontrare uno tra tanti eventi randomici una volta entrati in una porta segnalata con un
indicatore secondario <!>

20,1

FALSE

Evento Arena


★★☆☆☆

S

Come giocatori, vogliamo affrontare una stanza dove i nemici sono invulnerabili per un tempo limitato, per
testare la nostra capacità di evasione.

20,2

FALSE

Evento vento


★☆☆☆☆

M

Come giocatore, voglio dover compensare una forza fisica costante che mi spinge in una direzione, per rendere
il movimento più impegnativo.

20,3

FALSE

Evento oscurità


★★☆☆☆

?

Come giocatori, vogliamo esplorare stanze completamente buie illuminate solo dalla nostra luce, per
aumentare la tensione e la cooperazione.

20,4

FALSE

Evento

anomalia gravitazionale


★☆☆☆☆

M

Come giocatore, voglio sperimentare cambi improvvisi di gravità, per sfruttare la fisica nei combattimenti.

20,5

FALSE

Evento glitch temporale


★☆☆☆☆

?

Come giocatore, voglio che la velocità di gioco acceleri o rallenti casualmente (effetto "Lag" diegetico),
rappresentando l'instabilità del Mu.

20,6

FALSE

Evento loot ombra


★★☆☆☆

XL

Come Nox, voglio incontrare un nemico con l'equipaggiamento della mia run precedente per recuperare il loot
perso.

20,7

FALSE

Evento serenità


★★☆☆☆

?

Come giocatori, vogliamo trovare occasionalmente una stanza sicura dove il tempo non scorre e un'entità
benevola ci cura, per pianificare la prossima mossa senza ansia.

21

TRUE

Economy


★★★★☆

∞

Come giocatori vogliamo spendere valute per migliorare le abilità dei personaggi e affrontare i nemici con più
facilità.

21,1

FALSE

Gold economy


★★☆☆☆

S

Come giocatori possiamo raccogliere ora da spendere presso i mercanti per potenziamenti temporanei.

21,2

FALSE

Will economy


★★★★☆

S

La Will è una risorsa temporanea che i giocatori usano per attivare abilità speciali e interazioni ambientali.

21,3

FALSE

Determination economy


★★★★☆

S

Come giocatori possiamo guadagnare determinazione man mano che progrediamo nel dungeon.

22

TRUE

Audio managment


★★☆☆☆

∞

Come giocatori vogliamo avere un audio di gioco dinamico che reagisce alle nostre azioni nel gioco così
possiamo immergerci meglio nel gioco.

22,1

FALSE

Dynamic music


☆

L

Come giocatori volgiamo che la musica di sottofondo si faccia più intensa se siamo in un combattimento per
aumentare l'intensità percepita dello scontro.

22,2

FALSE

Menu sounds


★☆☆☆☆

S

Come giocatore voglio sentire dei brevi soundbite quando interagisco con i menu del gioco così che possa avere
un feedback dei miei input.

22,3

FALSE

Diegetic sounds


★☆☆☆☆

L

Come giocatori vogliamo sentire determinati audio a seconda delle azioni nostre e dei nemici per meglio
immedesimarci nell'atmosfera del gioco.

58


2,948275862


🃏 Log planning poker

**Log Planning Poker**


La discussione su ciascuna user story è stata contenuta in 5 minuti. Nonostante la limitazione, il gruppo ha
mostrato un coinvolgimento naturale e una valutazione attenta dei requisiti funzionali, sia a livello concettuale
che tecnico.


È importante notare che la valutazione (i voti) è stata "normalizzata": prima di iniziare la votazione specifica, il
team ha avuto una visione d'insieme di tutti i requisiti.


Grazie a questa panoramica generale e all'esperienza del team, è stato possibile farsi un'idea complessiva della
difficoltà progettuale.


Per ogni user story sono stati contati i turni e i voti con ordine di tabellamento 1. Alessandro, 2. Dario, 3.
Giuseppe


Movimento del giocatore:

S, M, XL

S, S, M

S, S, S

Schermata titolo (discusso approfonditamente):

M, M, S

M, M, M

Inizia/continua partita:

S, S, M

S, S, S

Termina partita:

S, XS, S

XS, XS, XS

Salvataggio:

M, ?, L

L, L, L

Attacco primario:

L, ∞, L

∞, ∞, ∞

Ottieni power-up:

∞, XL, ?

∞, ∞, ∞

Scelta della classe:

?, L, M

M, M, M

Morte e respawn:

L, S, ?

∞, ∞, ∞

Scelta stanza successiva:

?, ?, ?

L, M, L

L, L, L

Cambio arma:

XS, XS, ∞

M, S, S

M, M, M

Menu di pausa:

M, L, M

?, ?, ?

Power-up permanenti:

∞, L, M

M, M, M

Usa abilità speciali:

∞, ∞, ∞

Rianimazione:

L, M, M

M, M, M

Interazione area:

XL, ∞, L

∞, ∞, ∞

Inventario:

?, S, ?

M, M, M

NPC:

L, L, ∞

∞, ∞, ∞

Negozio:

L, M, ?

M, M, M

Attacchi co-op:

M, L, ∞

L, L, L

Recupero equipaggiamento precedente:

XL, L, L

XL, XL, XL

Eventi random:

∞, L,∞

∞, ∞, ∞


`💭` Concept


**Concept originale**


Piattaforma: PC

Genere: roguelite action-shooter rpg

Stile: top-down, pixel art

Modalità: Co-op


**Soul’s Light lore**


**Ambientazione**


La mappa di gioco si sviluppa nel Mu, una dimensione vacua, un limbo infinito dimorato anime oscure, perse.
Queste vagano senza meta. Il Mu è attesa perenne. La sua è una matrice di puro potenziale: ricordi mai
realizzati, opere mai compiute, luoghi mai esistiti, scale che non conducono a nulla.

Il Mu è un piano di esistenza in continua variazione, con mappe, strutture, eventi e nemici generati
casualmente.

Le stanze si generano proceduralmente davanti al giocatore.


dopo la morte, le anime sono condannate ad un'esistenza eterna nel Mu, prive di scopo. Le anime non possono
nuovamente “morire” perché si rigenerano dalla stessa materia del Mu, sono intrappolate. Queste condizioni
portano quasi tutte le anime a perdere la ragione, trasformandosi in ombre. Al livello più profondo del Mu si
trova Oblivion, un essere la cui natura è affievolire ulteriormente la luce delle anime che persistono e arrivano
fino a lui fino a spegnerle. Chiunque riesca a mantenere il proprio senno dovrà inevitabilmente affrontarlo.

I nemici spawnano, le trappole appaiono, la mappa cambia per ostacolare il progresso

In-game, la morte comporta il respawn all'inizio del dungeon per una nuova run


Non tutte le creature del Mu sono senza scopo. Alcune sono amichevoli, altre sono scese a compromessi con il
Mu. Oblivion contrasta il duo, inviando potenti manifestazioni oscure.

NPC che offrono potenziamenti, commerciano o forniscono indizi.

Nemici comuni, ambientazione, oggetti di lore da raccogliere.

