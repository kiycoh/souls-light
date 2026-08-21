---
source_file: "/home/kiycoh/Documents/dev/souls-light/docs/silica/Inbox/Soul_s Light Docs.docx"
type: Note
---

È possibile combattere con boss e npcs fino al boss finale, Oblivion.


**Protagonista e dinamica di gioco**


Il nostro protagonista è un'anima che conserva dentro di sé una grande luce e una ferrea determinazione a
fuggire dal Mu. Essendo il Mu la dimensione del potenziale mai espresso, il "dungeon" prende forma in base
alla volontà del protagonista di evaderne, in concomitanza con quella dell'Oblivion (infatti ci sono trappole e
nemici). La struttura della mappa è dinamica, simile a quella di Hades: non è possibile tornare indietro, poiché
la stanza precedente collassa non appena viene superata.


All'inizio di ogni run, il protagonista tenta di ricordare la propria identità in vita, il che influenza la scelta della
classe.

Ogni morte in gioco riporta il giocatore al livello più basso.

Il personaggio ottiene affinamenti della sua volontà (potenziamenti ogni tot lvl).

È possibile affrontare il proprio personaggio della run precedente, trasformato in un ombra con la build usata
al momento della morte.

Sconfiggendolo, si recupera tutto l'equipaggiamento della run passata.


Lux interagisce con gli aspetti "eterei" e narrativi del Mu usando la sua luce (come i "Sussurri cristallizzati"),
Nox, interagisce con gli aspetti fisici e tangibili del mondo.


**Economia e meccaniche co-op**


La valuta di gioco è la determinazione o Will. Nel Mu, dove la maggior parte delle anime vive passivamente, la
determinazione è una risorsa rara e preziosa, utilizzabile come merce di scambio. I giocatori la acquisiscono
avanzando tra le stanze.


Ogni giocatore ha una barra HP e contatore di [soldi] separate, condividono una barra di Will (per attacchi
speciali, Co-op o rianimazioni).


**Finale**


- VEDI IDEE FINALE

Il gioco non prevede una vera e propria via d'uscita, poiché si è già morti. Il protagonista è destinato a
diventare il nuovo "Oblivion", decidendo come riformare il Mu. Inizia così un ciclo infinito: in ogni nuova run,
il boss finale sarà l'avventuriero precedente.

🖼️ Inspo


**M.C. Escher**


**Giovanni piranesi**


**AI generated**


Kling video

Gemini video


**Textures**


**dead cells**


**Soul Knight**


🧑🏻‍💻 Implementazione

**Implementazione**


Approccio: TDD (Test Driven Development)


**Specifiche implementative**


Tech stack: IntelliJ IDEA / Visual Studio Code

Linguaggio: Java + Gradle

Architettura: LibGDX + Autumn MVC framework + dipendenze

Versionamento: git (GitHub repo)

API (eventualmente)

Modalità: co-op, endless

World: tile map dinamica e randomica (generata da una mappa di rumore tramite)

Il Mu potrebbe essere un vuoto bianco/spaziale/grigio, con texture che ricordano un frattale statico o la neve
di un vecchio televisore. Quando il protagonista avanza, il mondo "si solidifica" con colori e forme davanti a
lui, per poi dissolversi di nuovo in nulla dietro di lui.

Meccaniche principali: vedere Requisiti funzionali

Players:

Nox & Lux interazioni con l’ambiente, rianimazione, abilità speciale, …

NPC:

friendly npcs (randomly generated names, ai dialogues),

enemy npcs (empty creatures).


Filtri a schermo (post fx)


**Dipendenze LibGDX**


aiVersion=1.8.2

box2dlightsVersion=1.5

gdxControllersVersion=2.2.3

lmlVersion=1.10.1.12.1

colorfulVersion=0.9.0

controllerUtilsVersion=2.2.1

gandVersion=0.3.5

cruxVersion=0.1.2

gdcruxVersion=0.1.1

jaciVersion=0.4.0

jdkgdxdsVersion=1.13.1

funderbyVersion=0.1.2

digitalVersion=0.9.5

jdkgdxdsInteropVersion=1.13.1.0

juniperVersion=0.8.5

makeSomeNoiseVersion=0.3

tenPatchVersion=5.2.3

textratypistVersion=2.2.3

regExodusVersion=0.1.19

gdxVfxCoreVersion=0.5.4

gdxVfxEffectsVersion=0.5.4

graalHelperVersion=2.0.1

gdxVersion=1.14.0


**Audio e Grafica:**


Sprite 2d pipeline:

Pixel art style con sprites 32x32 pixel base

Sprite Atlas optimization per riduzione draw (grande immagine che comprende tutti gli sprite)​


Audio Asset:

dynamic audio loading per memory management


**Rendering Pipeline**


2d Rendering Specifiche:

Sprite Renderer con batching automatico

2d Light system per effetti dinamici

Particle System integrato per VFX


**Physics Engine**


Fisica:

Box2d-based physics per collision detection

Rigid body physics per proiettili e oggetti

Particle systems per effetti speciali​


Collision detection:

Tilemap Collider 2d per level geometry

Composite Collider 2d per performance optimization

Trigger-based interactions per item pickup e damage zones

🧪 Testing


**Test Driven Development**


**Vedi anche Test tracker**


**Test e qualità:**


Strategia di Test: unit test, integration test, user acceptance testing, test di performance.

Strumenti di Test: strumenti specifici per l'automazione dei test

Bug Tracking

Aggiornamenti: Come verranno gestiti gli aggiornamenti del gioco?

📦 Classi e attributi


**Diagrammi UML**


`📒` Dev log

dev-log

Ogni settimana 7 ore


**Settimana 1 [05/11/2025 - 06/11/2025]**


**Obiettivi**


Ideare trama del videogioco

definire architettura logica

Creare un diagramma UML esemplificativo


**Progressi**


5 nov 2025 Costruito trama e caratteristiche generali del gioco:

Brainstorming e scrittura dell’ambientazione e contesto di gioco;

definito una linea logico-tematica coinvolgente su cui basare le caratteristiche funzionali e logiche del gioco.

6 nov 2025 definito bozza di architettura logica del gioco:

Requisiti funzionali;

Requisiti non funzionali;

diagramma UML per MVC design


**Sfide**


[Chore 1]: Scegliere e definire un diagramma UML ottimale

[Chore 2]: definire caratteristiche funzionali (user stories) e non funzionali (requisiti).


**Cambiamenti**


6 nov 2025 - Pattern architetturale Model View Controller:

Scelto il pattern architetturale Model View Controller (MVC) in modo da separare rappresentazione delle
informazioni dall’interazione dell’utente.


**Settimana 2 [17/11/2025 - 21/11/2025]**


**Obiettivi**


Finalizzare architettura logica

Identificare la baseline del software (MVP)

Classificare requisiti funzionali per importanza

Aggiungere Story Points (SP) alla lista dei requisiti funzionali


**Progressi effettuati**


17 nov 2025 - Finalizzato architettura logica:

Aggiunti requisiti non funzionali

Aggiunti ulteriori requisiti funzionali (user stories);

Abbozzato classi e attributi.

17 nov 2025 - Classificati requisiti funzionali per priorità:

È stato definito un MVP in vista della revisione di dicembre.

19 nov 2025 - Aggiunti SP alla tabella dei requisiti funzionali:

dopo una discussione approfondita abbiamo listato tutti gli story points per tutte le user story (log Planning
poker)

20 nov 2025 - Discussione sulle user story con grado infinito e revisione di tutti i documenti:

abbiamo discusso sulla gestione delle user story da suddividere in epiche

corretto e alleggerito alcune parti dei vari documenti

aggiunto ID alla tabella delle user story


**Problemi e sfide**


[Chore 1]: Aggiornare criteri di accettazione delle user stories;

[Chore 2]: definire meglio le misure dei requisiti non funzionali.

[Chore 3]: definire meglio SP interrogativi e infiniti


**Cambiamenti**


17 nov 2025 - [descrizione decisione]: Spiegazione del motivo e delle implicazioni.

Esempio: deciso di passare da libreria X a libreria Y per una migliore performance.

Alternative considerate.


**Settimana 3 [24/11/2025 - 28/11/2025]**


**Obiettivi**


Iniziare diagrammi con design pattern (1° sprint)

Completare user story epiche


**Progressi**


24 nov 2025 - Continuazione epiche, definizione primo sprint:

Continuato la divisione delle epiche → Epike

Definito lo scheletro del primo sprint (24 Nov - 7 Dic) → Sprint Backlog

28 nov 2025 - Creazione codebase

Creata repository e creata architettura MVC base → Codebase


**Sfide**


[Chore 1]: Scegliere design pattern adeguati

[Chore 2]: dimensionare il nostro sprint (2 / 4 settimane?)

[Chore 3]: creare sprint backlog (tabella) dal product backlog

Id

sp (story points)

assegnatario

…

[Chore 4]: prendere le prime stories dal product backlg e metterle nello sprint backlog

[Chore 5]: segmentare in macroaree le user stories per diagrammi UML

[Chore 6]: progettazione dei test (su file excel- input e output atteso)

[Chore 7]: Se non ho finito le stories entro lo sprint, tornano nel backlog (che si aggiorna nelle priorità e pesi
se serve)


**Cambiamenti**


24 nov 2025 - Modifica user stories, creazione classi giocatore e formattazione docs

Aggiunta una user story alla tabella

Definite classi giocatore

Documentazione riformattata


**Settimana 4 [01/11/2025 - 05/12/2025]**


**Obiettivi**


Finalizzare diagrammi di classi UML per sprint 1

Inizializzare tests (test oriented development)


**Progressi**


1 dic 2025 - Definite ulteriori user stories (epike)

Tutte le epiche sono state suddivise in user stories granulari


**Sfide**


[Chore 1]: Segmentare le user stories in macroaree

[Chore 2]:


**Cambiamenti**


3 dic 2025 - Aggiunte ulteriori user stories


**Settimana 5 [08/12/2025 - 12/12/2025]**


**Obiettivi**


[Vedi obiettivi precedenti]


**Progressi**


3 dic 2025 
Creazione entry test

Creazione sprite aggiuntive

Creazione ulteriori diagrammi uml


**Sfide**


[Chore 1]: Segmentare le user stories in macroaree

[Chore 2]:


**Cambiamenti**


???


`📝` Task manager


**Task manager**


Qui è possibile visualizzare la tabella Google Sheet formattata in modo da poter agevolmente tenere traccia dei
compiti. Alcuni accorgimenti:

Inserire nuovi dati nella tabella sottostante non aggiorna il la tabella in Google Sheet;

I dati possono essere inseriti come “bozza” in questo documento, in maniera finale nel Google Sheet

Se i dati non vengono visualizzati, aggiornare la tabella (icona in alto a destra quando hover sulla tabella).


ID

NOME TASK

NOTE

PRIORITÀ

DELIVERABLE

ASSEGNATARIO

ORE

STATO

DATA INIZIO

DATA FINE

1

Ideazione

- Trama

- Abbozzata una trama accattivante


★

- Documento concept

Gruppo

2

Completato

05/11/2025

26/11/2025

2

Creazione workspace

- Creare un ambiente di lavoro ordinato

- Sarà il riferimento principale per il team di sviluppo

- Le ore lavorate sono riferite al lavoro di concettualizzazione e non considerano il continuo aggiornamento dei
doc


★

- Cartella Google Drive di progetto

Gruppo

4

Completato

06/11/2025

07/11/2025

3

Definire architettura logica

- Definire diagramma architetturale UML


★

- Schema logico architetturale

Gruppo

5

Completato

06/11/2025

17/11/2025

4

Definire requisiti funzionali e non

- Definire requisiti funzionali

- Definire requisiti non funzionali


★

- Sheet requisiti funzionali

- Doc requisiti funzionali

- Requisiti non funzionali


Completato


5

Definire baseline

- Classificare la priorità

delle funzionalità (Planning Poker)

- Ordinarle per importanza

- Definire ulteriori user stories (epike)


★

- Planning Poker Log

Gruppo

5

Completato

17/11/2025

20/11/2025

6

Definire gli sprint

- Delineare durata sprint

- Approcciarsi all'implementazione

definendo ulteriormente le

caratteristiche tecniche


★

- Sprint backlog

Gruppo

4

Completato

24/11/2025

26/11/2025

7

Segmentare epiche

- Le epiche devono essere suddivise in

user stories ulteriori


★★★☆☆

- Epiche

Gruppo

2

Completato


8

Sprint 1

- Definire schemi UML implementativi

- Creare progetto LibGDX

- Implementare le prime classi con test


★★★★☆


Gruppo

6

Completato


8,1

Implementazione test e classi

- Scrivere tutti i test per MVP

- Seguire metodologia Test Driven Development

- Scrivere test --> Scrivere classe


★★★★☆

- Test tracker


In corso


4,555555556


28


`💨` Sprint Backlog


**Sprint backlog**


1️ Sprint 1


**Sprint 1 (MVP)**


Lun 24 - Dom 18


Id

Priorità

Story points

Funzionalità

Descrizione

Criteri di accettazione

Design pattern

1

Alta

M

Schermata Titolo

Il giocatore può visualizzare la schermata del titolo per scegliere un'opzione di menu.

Interagire con il menu per selezionare 'new game' o 'load game'. dal menu principale si possono aprire le
impostazioni.

Observer

2

Alta

S

Inizia/Continua Partita

Il giocatore può iniziare una nuova partita o riprendere quella precedente dal menu principale.

Iniziare una nuova run dal menu principale o riprendere quella precedente.


3

Alta

XS

Termina il Gioco

Il giocatore può chiudere il programma dal menu di pausa o dal menu principale.

Aprire il menu di pausa e selezionare 'Exit' senza perdere i progressi. Chiudere il programma dal menu
principale.


4

Alta

S

Movimento del Giocatore

Il giocatore può muovere il proprio personaggio sulla mappa per esplorare e schivare gli attacchi.

Il personaggio si muove fluidamente con il controllo direzionale. Il personaggio si ferma al rilascio del
controllo. Il personaggio non attraversa muri o ostacoli.


6

Alta

∞

Attacco Primario

Il giocatore può eseguire un attacco primario per colpire e sconfiggere i nemici.

Quando un nemico è a portata e si preme `attack_button`, il personaggio attacca. L'attacco ha auto-aim.
L'attacco entra in cooldown. Se nessun nemico è a portata, l'attacco è nella direzione del personaggio.


7

Alta

∞

Mappa Dinamica

Il giocatore avanzando deve incontrare stanze diverse per forma, trappole, nemici

Quando i giocatori scelgono una porta con un determinato marker al di sopra devono arrivare in una nuova
stanza che ha esattamente le caratteristiche descritte dal marker ma con un pattern randomico


10

Alta

∞

Ecosistema Nemici

Il giocatore deve incontrare una varietà di nemici

Man mano che i giocatori avanzano nel dungeon e diventano sempre più forti, nuovi nemici più potenti devono
ostacolarli


**TEST TRACKER**


ID

Titolo

Caso limite

Descrizione

Implementato

Assegnatario

1

testNewGame()

FALSE

Testa l'inizializzazione di una nuova partita in EventManager

FALSE

Alessandro

2

testLoadGame()

FALSE

Verifica la logica di caricamento di un salvataggio

FALSE

Alessandro

3

testSaveSizeLimit()

TRUE

Verifica che il salvataggio non sia troppo grosso

FALSE

Dario

4

testOption()

FALSE

Testa il passaggio alla schermata delle opzioni

FALSE

Alessandro

5

testExit()

FALSE

Testa la corretta chiusura del gioco

FALSE

Alessandro

6

testAddMenuListener()

FALSE

Testa che i listener vengano registrati correttamente nella TitleScreen

FALSE

Giuseppe

7

testButtonClick()

FALSE

Testa che il click di una delle opzioni nella TitleScreen avvisi correttamente tutti gli osservatori

FALSE

Dario

8

testAddMovementListener()

FALSE

Testa che i listener vengano registrati correttamente in MovementInput

FALSE

Alessandro

11

testSaveGame()

FALSE

Testa che il Player crei un Memente contenete tutte le informazioni suil giocatore

FALSE

Alessandro

19

testResetData()

FALSE

Testa che i dati di gioco vengano resettati quando richiesto

FALSE

Alessandro

32

testLoadTemplate()

FALSE

Testa che venga caricato un temeplate di mappa da quelli disponibili

FALSE

Alessandro

33

testCorruptTemplate()

TRUE

Testa come si comporta il progamma in caso di template corrotti o mancanti

FALSE

Alessandro

18

testGameManagerUniqueInstance()

FALSE

Testa che non si possa creare un'altra istanza di GameManager in quanto Singleton

FALSE

Alessandro

17

testDoAction()

FALSE

Testa l'esecuzione di un'azione generica alla pressione di un pulsante sul controller

FALSE

Giuseppe

16

testMovement()

FALSE

Testa che l'input della levetta si traduca in un cambiamento effettivo della posizione del player sulla mappa

FALSE

Alessandro

15

testAnalogicInput()

FALSE

Testa che il movimento della levetta sul controller venga rilevato

FALSE

Alessandro

14

testButtonInput()

FALSE

Testa che la pressione di un pulsante sul controller venga rilevata

FALSE

Dario

22

testAddAudioListener()

FALSE

Testa che un oggetto AudioManager possa essere aggiunto alla lista dei listener del Player senza errori.

FALSE

Giuseppe

9

testNewGameSound()

FALSE

Testa che parta il suono "NewGame" in AudioManager al click di NewGame

FALSE

Giuseppe

10

testLoadGameSound()

FALSE

Testa che parta il suono "LoadGame" in AudioManager al click di Continue

FALSE

Giuseppe

11

testOptionSound()

FALSE

Testa che Parta il suono "Option" in AudioManager all'apertura delle opzioni

FALSE

Giuseppe

23

testGetAttackSound()

FALSE

Testa che l'AudioManager prenda il giusto ID in base alla classe del giocatore

FALSE

Giuseppe

24

testNotificationContainsStrategy()

FALSE

Testa che l'oggetto AttackStrategy passato all'AudioManager sia esattamente lo stesso che il Player stava usando

TRUE

Giuseppe

31

testAudioStrategyIdentification()

FALSE

Testa che ricevendo una strategia completa, l'AudioManager estragga il soundID e riproduca il suono
corrispondente.

FALSE

Giuseppe

30

testOnPlayerAttackTriggered()

FALSE

Testa che quando il Player esegue un attacco, il metodo onPlayerAttack dell'Observer venga effettivamente
chiamato.

FALSE

Giuseppe

12

testExitSound()

FALSE

Testa che parta il suono "Exit" in AudioManager alla pressione del pulsante Exit

FALSE

Giuseppe

20

testPlayerInitialization()

FALSE

Testa che la classe scelta dal giocatore sia quella corrente

FALSE

Giuseppe

21

testDoAnAttack()

FALSE

Testa che invocando player.doAnAttack(), venga effettivamente chiamato il metodo .attack() della strategia
attualmente impostata.

FALSE

Dario

25

testDoAnAttackWithoutStrategy()

TRUE

Testa cosa succede se per errore nessuna strategia è contenuta nell'attacco

TRUE

Dario

26

testWarriorStats()

FALSE

Testa che le statistiche del guerriero vengano prese correttamente con i metodi getter

TRUE

Dario

27

testMageStats()

FALSE

Testa che le statistiche del mago vengano prese correttamente con i metodi getter

TRUE

Dario

28

testThiefStats()

FALSE

Testa che le statistiche del ladro vengano prese correttamente con i metodi getter

TRUE

Dario

29

testArcherStats()

FALSE

Testa che le statistiche del arciere vengano prese correttamente con i metodi getter

TRUE

Dario

34

testAddEnemies()

FALSE

Testa che vengano spawnati correttamente i nemici

FALSE

Dario

35

testAddTraps()

FALSE

Testa che vengano aggiunte delle trappole sulla mappa

FALSE

Dario

36

testSetDoors()

FALSE

Testa che vengano spawnate correttamente delle porte sulle pareti

FALSE

Dario

37

testSetPlayersSpawn()

FALSE

Testa che i giocatori vengano posizionati in posizioni accettabili

FALSE

Dario

38

setGetResult()

FALSE

Testa che il la stanza completa venga costruita correttamente con tutti gli elementi aggiunti

FALSE

Giuseppe

39

testReset()

FALSE

Testa che il programma pulisca il builder stanze

FALSE

Giuseppe

40

testEnemyRegistryCache()

FALSE

Verifica che il registry contenga i prototipi base

FALSE

Giuseppe

41

testCloneIndependence()

FALSE

Testa che clonando un nemico crei un oggetto in memoria diverso

FALSE

Giuseppe

42

testEnemyNotFound()

TRUE

Testa cosa succede se si vuole clonare? un nemico non esistente

FALSE

Dario

43

testChaserBehaviour()

FALSE

Testa che il Chaser cerchi di avvicinarsi al giocatore per attaccarlo

FALSE

Alessandro

44

testRangerBehaviour()

FALSE

Testa che il Ranger cerchi di stare a distanza dal giocatore per attaccare da lontano

FALSE

Alessandro

45

testPusherBehaviour()

FALSE

Testa che il Pusher cerchi di avvicinarsi al giocatore per spingerlo sulle trappole

FALSE

Alessandro

46

testShielderBehaviour()


[COMPORTAMENTO SHIELDER DA DEFINIRE]

FALSE


47

testOblivionBehaviour()

FALSE

[COMPORTAMENTO OBLIVION DA DEFINIRE]

FALSE


**OLD Diagrammi UML**


**Diagrammi UML**


classDiagram %% --- COMPONENTS (SERVICES) --- class GameManager { <<Component>>
<<Singleton>> -Player player -Memento savedGame +startGame() +pauseGame() +saveGame()
+loadGame() } class AudioManager { <<Component>> <<Singleton>> +playMusic(track: String)
+playSound(sound: String) +mute(boolean) } class EventManager { <<Component>> <<Singleton>>
@Inject -GameManager gameManager +triggerEvent(eventId: String) } class MovementSystem {
<<Component>> @Inject -Player player +movePlayer(x: float, y: float) +doAction() } %% --CONTROLLERS (Gestione Input/UI) --- class MainMenuController { <<Controller>> @Inject -AudioManager
audioManager @Inject -GameManager gameManager +initiate() +onPlayClicked() +onOptionsClicked()
+onExitClicked() } class InputController { <<Controller>> @Inject -MovementSystem movementSystem
+keyDown(keycode: int) +touchDown(screenX: int, screenY: int) } %% --- MODEL & MEMENTO (Dati) --class Player { <<Component>> -float hp -float x -float y -int will +save(): Memento +restore(m: Memento)
+getHp() } class Memento { <<Interface>> +getDate() } class ConcreteMemento { -float savedHp -float
savedX -float savedY +getState() } %% --- RELAZIONI --- %% Dependency Injection (Autumn wiring)
MainMenuController ..> AudioManager : @Inject MainMenuController ..> GameManager : @Inject
InputController ..> MovementSystem : @Inject EventManager ..> GameManager : @Inject MovementSystem
..> Player : @Inject GameManager ..> Player : @Inject (manage state) %% Memento Pattern relationships
Player ..|> Memento : Creates ConcreteMemento --|> Memento : Implements Player ..> ConcreteMemento :
Uses GameManager o-- Memento : Holds (Caretaker)

m

classDiagram %% --- MAIN COMPONENT --- class Player { <<Component>> -currentStrategy:
AttackStrategy %% Autumn inietta l'AudioManager direttamente @Inject -audioManager: AudioManager %%
Autumn può iniettare tutte le strategie disponibili in una mappa o array @Inject -allStrategies:
ObjectMap~String, AttackStrategy~ +doAnAttack() +changeClass(className: String) } %% --- SERVICES /
SYSTEMS --- class AudioManager { <<Component>> <<Singleton>> +playAttackSound(soundId: String)
+playHitSound() } %% --- STRATEGY PATTERN (Components) --- class AttackStrategy { <<Interface>>
+attack() +getDamage(): float +getSoundID(): String } class AbstractAttack { <<Abstract>> #damage:
float #range: float #speed: int #soundId: String } %% Le strategie concrete diventano Componenti Singleton
class WarriorAttack { <<Component>> +attack() } class MageAttack { <<Component>> +attack() }
class ThiefAttack { <<Component>> +attack() } class ArcherAttack { <<Component>> +attack() } %%
--- RELAZIONI --- %% Inheritance AbstractAttack ..|> AttackStrategy : Implements WarriorAttack --|>
AbstractAttack MageAttack --|> AbstractAttack ThiefAttack --|> AbstractAttack ArcherAttack --|>
AbstractAttack %% Dependency Injection (Le frecce tratteggiate indicano "Injection") Player ..> AudioManager
: @Inject (Direct Call) Player o-- AttackStrategy : Selects/Uses


classDiagram %% --- COMPONENTS (SERVICES) --- class EnemyRegistry { <<Component>>
<<Singleton>> -cache: ObjectMap~String, AbstractEnemy~ +loadPrototypes() +createEnemy(type:
String): AbstractEnemy } class MapDirector { <<Component>> <<Singleton>> @Inject -enemyRegistry:
EnemyRegistry -builder: RoomBuilder +makeRandomRoom(difficulty: String) +makeBossRoom()
+makeSafeRoom() } %% --- PROTOTYPE PATTERN (Entities) --- class AbstractEnemy { <<Prototype>>
<<Abstract>> #hp: float #speed: float #damage: float +clone(): AbstractEnemy +updateBehavior(player:
Player) } class Pusher { +clone() +updateBehavior() } class Shielder { +clone() +updateBehavior() } class
SpikedBall { +clone() +updateBehavior() } class Chaser { +clone() +updateBehavior() } class Oblivion {
+clone() +updateBehavior() } class Ranger { +clone() +updateBehavior() } %% --- BUILDER PATTERN --class RoomBuilder { <<Interface>> +reset() +setShape(type: ShapeType) +addEnemies(type: EnemyType,
count: int) +addTraps(type: TrapType) +setDoors() +getResult(): Room } class ConcreteRoomBuilder { room: Room -enemyRegistry: EnemyRegistry +ConcreteRoomBuilder(registry: EnemyRegistry)
+addEnemies(type: EnemyType, count: int) +getResult(): Room } class Room { -enemies:
Array~AbstractEnemy~ -traps: Array~Trap~ -tiles: Array~Tile~ } %% --- RELAZIONI --- %% Inheritance
Enemies AbstractEnemy <|-- Pusher AbstractEnemy <|-- Shielder AbstractEnemy <|-- SpikedBall
AbstractEnemy <|-- Chaser AbstractEnemy <|-- Oblivion AbstractEnemy <|-- Ranger %% Inheritance Builder
RoomBuilder <|.. ConcreteRoomBuilder %% Dependency Injection MapDirector ..> EnemyRegistry : @Inject
%% Standard Associations EnemyRegistry o-- AbstractEnemy : Caches Prototypes MapDirector ..>
RoomBuilder : Uses to build ConcreteRoomBuilder ..> Room : Creates %% Flow: Director passes registry to
builder or uses it to fetch enemies to give to builder ConcreteRoomBuilder ..> AbstractEnemy : Instantiates
(via clones)


`💡` Idee


**Idee**


L'obiettivo di questo documento è categorizzare delle idee realizzabili. Un’idea viene inserita nella lista se:

Segue le specifiche progettuali;

È nuova (non già inserita);

È realisticamente implementabile.


Le idee potranno essere abbozzate nella sezione “Bozze”, le idee adottate ed elaborate saranno inserite nella
sezione “Accettate”.


**FX**


distorsione/Onda d'Urto: gdx-vfx include shader di distorsione che possono simulare l'onda d'urto dell'attacco
"Collasso della Realtà," deformando i pixel dello schermo attorno al punto di impatto.

CRT/Scanlines: Per ottenere l'estetica "Vecchia TV" menzionata nella sezione concept 1, un filtro CRT o Old TV
da gdx-vfx può essere applicato globalmente o a specifiche zone "glitch" della mappa.

Aberrazione Cromatica: Questo effetto può essere legato alla sanità o alla salute del giocatore. Man mano che il
giocatore subisce danni o si avvicina a "Oblivion," l'aberrazione cromatica può aumentare, rappresentando
visivamente la "luce che svanisce" dell'anima descritta nella lore.1 Il documento richiede esplicitamente lo
"Screen shake quando si viene colpiti da un grosso colpo".1 Sebbene questo possa essere fatto manualmente,
gdx-vfx o una semplice manipolazione della camera nel livello View consente un sistema di "trauma" coerente
in cui il danno aggiunge un valore di trauma che decade nel tempo, scuotendo la camera usando il rumore
Perlin (un altro uso per Joise) per una sensazione naturale.6. Consegna Narrativa e Localizzazione6.1.
TextraTypist per la Tipografia Avanzata

Stili di Testo Ricco (Rich Text): La lore descrive il Mu come "silenzio prima di una nota." Per trasmettere questo
tono poetico, il motore di testo deve supportare effetti sottili—testo ondeggiante per i "sussurri," testo
tremolante per gli "urli" o l'"instabilità," e gradienti di colore per termini magici come "Volontà" o
"determinazione." BitmapFont standard richiede codice personalizzato complesso per ottenere questo;
TextraTypist lo fa out of the box. + effetto macchian da scrivere per dialoghi


**Idea Lore**


[05/11/2025] Light's soul: un'anima tenebrosa che decide di passare alla luce. La mappa rappresenta una
dimensiona “vuota”, il “Mu” in cui le anime oscure vagano senza scopo. Il "Mu" non è oscurità, ma assenza. È
un limbo infinito, una vuota dimora di anime sconfitte, vagano come fumo senza meta. L'architettura è fatta di
ricordi spezzati: rovine fluttuanti di luoghi mai esistiti, scale che non portano a nulla, sussurri cristallizzati
nell'aria. Il Mu è silente, ma perennemente in attesa.


dopo la morte, le anime vengono condannate a vagare nel Mu, senza scopo. Solo coscienza di sé all'infinito,
visto che sono già morti, non possono ri-morire (in game se muori respawn all'inizio del dungeon e cominci
una nuova run). Per via di questa condizione quasi tutte le anime perdono il senno, diventando d'ombra (dark
souls sh). All'ultimo livello del Mu, c'è l’ ”Oblivion”, essere che eccelle nello spegnere la luce nelle anime.
Se non perdi il senno prima, sarai costretto a vedertela con lui.


Il nostro/nostri protagonista/i è un'anima che ancora conserva una flebile luce, deciso ad evadere dal Mu. VIsto
che il Mu è dimensione del nulla, il “dungeon” inizia ad esistere perché il protagonista ha volontà di uscirne,
ma il Mu viene plasmato sia dalla tua volontà che quella del boss (Oblivion). Quindi la mappa ha struttura di
quella di Hades, non puoi tornare indietro perchè la stanza precedente smette di esistere appena la “passi”.

All'inizio della run il protagonista cerca di ricordare chi era in vita (scelta della classe). Il Mu è un piano di
esistenza in continua variazione (mappa, eventi, nemici random). Se muori, in game, devi ricominciare dal
livello più basso. Inoltre puoi scegliere di scontrarti con il tuo personaggio della scorsa run (con la build che
avevi quando sei morto) come anima d'ombra, se riesci a sconfiggerlo, droppa tutto quello che avevi nella run
passata.


La valuta in game è determinazione. Visto che nel Mu tutti vivono passivamente, è una cosa rara è può essere
usata come merce di scambio. I giocatori acquisiscono determinazione passando stanze.

Meccaniche co-op: Ogni player ha una barra di HP separata, ma condividono la barra di Luce?. Risorsa che può
essere usata per rianimare il compagno caduto o attivare attacchi co-op.

Non c'è via di uscita, sei già morto. Il/i protagonista/i diventano il nuovo “Oblivion” e sceglie come riformare il
Mu. Se avvii una nuova run il boss finale è l'avventuriero precedente.


La valuta in game è determinazione. Visto che nel Mu tutti vivono passivamente, è una cosa rara è può essere
usata come merce di scambio. I giocatori acquisiscono determinazione passando stanze.


**Aggiornamento lore**


L’avanzare nel dungeon corrisponde ad un viaggio introspettivo del giocatore. I due personaggi giocabili non
sono altro che due facce della personalità di un individuo. Il viaggio riguarda il cambiamento. Nox è la parte di
ciascuno legato al passato e alle abitudini (dove vanno tutte le cose che non sono state fatte?) la parte di noi
che vuole evitare i cambiamenti per rimanere nella comfort zone.


All’inizio della divisione lo immagino con un aspetto molto scuro che va via via schiarendosi più si ascende
verso Oblivion. d’altra parte Lux è la parte di noi più aperta al cambiamento. All’inizio del viaggio ha come
aspetto una luce affievolita che va man mano diventando sempre più ad illuminarsi rappresentando la crescita
del giocatore.


**Rivisitazione dell’oblivion.**


L’Oblivion è il giocatore stesso, o meglio la rappresentazione stessa di tutte le paure (il peso delle cose
irrealizzate) che ciascuno di noi può avere. Nello scontro finale vedo più alternative:


(^ dahaka di Prince of Persia: WW ^) quanto è forte

1)Oblivion è troppo forte e si può battere soltanto con un attacco combo finale che vede il sacrificio di Nox

che viene completamente assorbito da Lux. Indica che l’attaccamento al passato viene superato e nasce un Lux
più forte che riesce ad uscire dal Mu -> sblocca la run infinita perchè ormai si è così forti da poter superare
qualsiasi difficoltà (quindi la run infinita)

2) Come prima ma con la resa all’oscurità. L’attacco finale vedrà la scomparsa di Lux e il prevalere di Nox che
diventa il nuovo oblivion (ciclicità, il protagonista diventa l’oblivion della run successiva)

Per fare sta cosa servirebbero i classici salvataggi pre ending dei videogiochi per poter fare i due finali.


Questo per giustificare la presenza dei due giocatori. All’inizio al posto di chiedere chi eri nella vecchia vita
potremmo semplicemente chiedere quale arcano interpretare durante il viaggio.


I vendor potremmo vederli come parenti e/o amici che nella vita potrebbero supportarci -> quindi ci aiutano
ad avanzare nel dungeon.


La mappa che scompare man mano è collegabile alla crescita e al superamento delle paure, etc.


**Map design**


**Meccaniche di gioco**


Movimento per controllo direzionale a 360°

Screen shake quando vieni colpito da un colpo grosso

Standard Attack attacco primario con auto-aim

meccanica di parry (magari solo per le armi melee), low effort da implementare e piuttosto soddisfacente come
giocatore

Charged attacks tramite hold del tasto attack (dipende dalle armi)

Attacco co-op con animazione dove Lux e Nox diventano i punti dello yin e dello yang?

cooldown

costo

Skill attack (icona speciale) abilità speciali con cooldown

Weapon Switch (icona scambio) [ ] cambio arma tra 2 slot equipaggiabili

Quando sconfiggo nemici, una piccola quantità di "Determinazione" viene aggiunta al mio inventario.

Quando completo una stanza, ricevo un bonus di "Determinazione".

La "Determinazione" accumulata è visibile nell'interfaccia utente.


**Personaggi**


Nox (Il Giocatore): L'involucro oscuro, l'eco dell'anima originale. Nox non è "cattivo", è sacrificio.

Lux (La Scintilla): L'anima "di luce" appena nata. All'inizio è solo un debole barlume, è la chiave. La sua luce è
l'unica cosa che può interagire con certi aspetti del Mu, aprendo passaggi e rivelando la verità.


**Classi**


Ognuno ha una pool di power up e armi diverse.

Guerriero (tanky, attacco melee. Il suo scopo è attirare gli attacchi se)

Assassino (veloce, ma relativamente poca vita. Stile hit and run)

Arciere (veloce, Attacchi a distanza sigolo bersgaglio)

Mago (Attacchi a distanza ad area, poca vita, molto danno)


Giullare (velocità media, vita media, ha nella pool armi sia a distanza che melee. danno variabile e randomico)
???


Abilità del Protagonista: Il personaggio potrebbe avere potenziamenti (rogue-like) che non sono magia, ma
affinamenti della sua volontà. Invece di "lanciare una palla di fuoco", impara a "Collassare la Realtà" in un
punto, creando un'esplosione di esistenza che danneggia le creature del nulla.

es abilità. “Reality collapse” collassa un punto della mappa, creando un'esplosione di esistenza che danneggia
le shadow creatures.


**AI/NPC Behavior Systems**


Il gioco implementa sistemi AI basati su Finite State Machine (FSM) con pathfinding A*:

Il boss: l’Oblivion

Sistema AI Nemici:

FSM States: Idle, Search, Attack, death​

A Pathfinding* per navigazione dungeon

Behavioral patterns differenziati per tipo di nemico

Line of sight detection per aggro system


Complessità AI Scalabile:

Basic AI: movement patterns semplici per nemici base

Advanced AI: boss con pattern attack complessi e fasi multiple

Swarm intelligence per gestione gruppi di nemici​


**dialoghi degli NPC:**


Un'anima antica potrebbe dire: "Qui, la pietra sotto i tuoi piedi è reale solo finché credi che lo sia. Ci si può
svegliare da questa illusione?"

Un mostro compromesso potrebbe borbottare: "Luce... la tua luce brucia! Spegnila!"

Un’altra anima in uno dei livelli bassi "Perché lottare? E tutto inutile. Arrenditi. Io l’ho fatto”


**Valuta**


Serve una valuta per run che serve per power up, armi e simili all’interno di una run e una che rimane da una
run all’altra per potenziamenti permanenti.

Will


**Meccaniche co-op**


Ogni player ha una barra di HP e scudo separata,

condividono la barra di Volontà o “Will”: Risorsa che può essere usata per rianimare il compagno caduto o
attivare attacchi co-op.

Nox può interagire con elementi ombra appartenenti al Mu (chests, porte, eventi)

Lux può interagire solo con elementi che appartengono all’etere (sussurri cristallizzati, altre luci)


**Idee sul finale**


Non c'è via di uscita, sei già morto. Il/i protagonista/i diventano il nuovo “Oblivion” e sceglie come riformare il
Mu. Se avvii una nuova run il boss finale è l'avventuriero precedente.

(Multiple) Ending

Ending A: Giocatore si divide in Nox e Lux per poi tornare ad uno stato di equilibrio

Ending B: Punto di vista di Nox

Ending C: Punto di vista di Lux

Ending d: Punti di vista dell’Oblivion


**Soundtrack**


Musica semi dinamica (idea presa da Hades): quando sconfiggi tutti i nemici della stanza si abbassa il volume e
si silenzia la batteria. Torna completa quando entri in un’altra stanza.

Idea per realizzarlo: o due stem della stessa traccia (instrumental w/out drums + drums only) sincronizzate o
direttamente due istanze della canzone sempre in corso (una completa, una senza batteria) ma che si
scambiano in cross-fade. Poi si vede meglio, dipende dalla struttura del codice

Stinger finale della musica a fine bossfight (poi rimane il silenzio). è difficile da spiegare a testo.


`📁` Codebase


**Codebase**


**Paths from repository root**


.gradle

.idea

build

core

core/build

core/src

core/src/main

core/src/main/java/io/github/soulslight/controller/GameController.java

core/src/main/java/io/github/soulslight/model/GameModel.java

core/src/main/java/io/github/soulslight/view/GameScreen.java

core/src/main/java/io/github/soulslight/SoulsLightGame.java

core/build.gradle

gradle

lwjgl3

.editorconfig

.gitattributes

.gitignore

build.gradle

gradle.properties

gradlew

gradlew.bat

README.md

settings.gradle


**Classi**


**SoulsLightGame**


package io.github.soulslight; import com.badlogic.gdx.Game; import
com.badlogic.gdx.graphics.g2d.SpriteBatch; import io.github.soulslight.controller.GameController; import
io.github.soulslight.model.GameModel; import io.github.soulslight.view.GameScreen; /** {@link
com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */ public class SoulsLightGame
extends Game { private SpriteBatch batch; private GameModel model; private GameController controller;
@Override public void create() { batch = new SpriteBatch(); model = new GameModel(); controller = new
GameController(model); this.setScreen(new GameScreen(batch, model, controller)); } @Override public void
dispose() { batch.dispose(); super.dispose(); } }


**GameController**


package io.github.soulslight.view; import com.badlogic.gdx.Gdx; import com.badlogic.gdx.Screen; import
com.badlogic.gdx.graphics.OrthographicCamera; import com.badlogic.gdx.graphics.g2d.SpriteBatch; import
com.badlogic.gdx.utils.ScreenUtils; import com.badlogic.gdx.utils.viewport.FitViewport; import
com.badlogic.gdx.utils.viewport.Viewport; import io.github.soulslight.controller.GameController; import
io.github.soulslight.model.GameModel; public class GameScreen implements Screen { // --- MVC
DEPENDENCIES --- private final SpriteBatch batch; private final GameModel model; private final
GameController controller; // --- PIXEL ART RENDERING --- private static final float WORLD_WIDTH = 480;
private static final float WORLD_HEIGHT = 270; private final OrthographicCamera camera; private final
Viewport viewport; public GameScreen(SpriteBatch batch, GameModel model, GameController controller) {
this.batch = batch; this.model = model; this.controller = controller; // Setup Camera e Viewport for Pixel Art
this.camera = new OrthographicCamera(); // FitViewport maintains aspect ratio while scaling to fit the
screen. this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera); } @Override public void
show() { // Activate controller as input processor Gdx.input.setInputProcessor(controller); } @Override public
void render(float delta) { // MVC Update Loop controller.update(delta); model.update(delta); // Rendering
ScreenUtils.clear(0, 0, 0, 1); // (black background) // Update camera and apply batch camera.update();
batch.setProjectionMatrix(camera.combined); batch.begin(); // to be added: // viewRenderer.render(batch,
model); // tests batch.end(); // Debug Renderer for Box2D (to be added) } @Override public void resize(int
width, int height) { // 5. IMPORTANT: Window resizing handling true pixel viewport.update(width, height,
true); // true = centra la camera } @Override public void pause() { } @Override public void resume() { }
@Override public void hide() { } @Override public void dispose() { // NOTE: No batch.dispose() because
batch is in SoulsLightGame } }


**GameModel**

