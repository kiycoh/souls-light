package io.github.soulslight.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import io.github.soulslight.manager.GameManager;
import io.github.soulslight.manager.SaveManager; // <--- NUOVO IMPORT
import io.github.soulslight.model.FireDamageDecorator;
import io.github.soulslight.model.GameModel;
import io.github.soulslight.model.Player;

public class GameController extends InputAdapter {

  private final GameModel model;
  private final SaveManager saveManager; // <--- Delegato al salvataggio
  private static final float SPEED = 160f;

  public GameController(GameModel model) {
    this.model = model;
    this.saveManager = new SaveManager(); // Inizializziamo il gestore
  }

  @Override
  public boolean keyDown(int keycode) {
    Player player = model.getPlayer();
    if (player == null) return false;

    switch (keycode) {
      // spazio per attaccare
      case Input.Keys.SPACE:
        if (!player.isDead()) player.attack(model.getActiveEnemies());
        return true;

      case Input.Keys.O:
        player.setAttackStrategy(new FireDamageDecorator(player.getAttackStrategy()));
        Gdx.app.log("Controller", "Danno Fuoco Attivato!");
        return true;

      // non funziona al momento
      /* case Input.Keys.R:
      restartGame();
      return true;*/

      // f5 crea un json con un salvataggio
      case Input.Keys.F5:
        saveManager.saveGame(model);
        // Il log è gestito dentro SaveManager, ma puoi aggiungerne uno qui se vuoi
        return true;

      // f9 ricarica il salvataggio
      case Input.Keys.F9:
        if (saveManager.hasSaveFile()) {
          saveManager.loadGame(model);
        } else {
          Gdx.app.log("Controller", "Nessun file di salvataggio trovato (savegame.json)!");
        }
        return true;

      // 0 per attivare la debug mode per vedere le collisioni
      case Input.Keys.NUM_0:
        GameManager.DEBUG_MODE = !GameManager.DEBUG_MODE;
        Gdx.app.log("Controller", "Debug Mode: " + GameManager.DEBUG_MODE);
        return true;
    }
    return false;
  }

  public void update(float delta) {
    Player player = model.getPlayer();

    if (player == null || player.isDead()) {
      // Se siamo morti il corpo si ferma
      if (player != null && player.getBody() != null) {
        player.getBody().setLinearVelocity(0, 0);
      }
      return; // Non legge wasd da morti
    }

    float velX = 0;
    float velY = 0;

    if (Gdx.input.isKeyPressed(Input.Keys.W)) velY = SPEED;
    if (Gdx.input.isKeyPressed(Input.Keys.S)) velY = -SPEED;
    if (Gdx.input.isKeyPressed(Input.Keys.A)) velX = -SPEED;
    if (Gdx.input.isKeyPressed(Input.Keys.D)) velX = SPEED;

    player.move(velX, velY);
  }

  // Al momento commentata perche non funziona e fa crashare appena si preme R
  /* private void restartGame() {
      Gdx.app.log("Controller", "Esecuzione Restart...");

      // Eseguiamo nel prossimo frame grafico per evitare conflitti durante il render attuale
      Gdx.app.postRunnable(() -> {
          Game gameApp = (Game) Gdx.app.getApplicationListener();

          // 1. RECUPERA I RIFERIMENTI NECESSARI
          GameScreen oldScreen = (GameScreen) gameApp.getScreen();
          SpriteBatch sharedBatch = oldScreen.getBatch(); // Salviamo il batch condiviso

          // 2. DISTRUGGI IL VECCHIO (PULIZIA TOTALE)
          // È fondamentale farlo ORA.
          // Se crei il nuovo mondo Box2D mentre il vecchio è attivo, crasha.
          this.model.dispose(); // Distrugge Box2D World
          oldScreen.dispose();  // Distrugge MapRenderer e HUD

          // 3. CREA IL NUOVO
          // Ora la memoria è pulita e possiamo caricare il nuovo livello senza conflitti.
          GameModel newModel = new GameModel();
          GameController newController = new GameController(newModel);

          // Il batch è salvo perché abbiamo tolto batch.dispose() da GameScreen
          GameScreen newScreen = new GameScreen(sharedBatch, newModel, newController);

          // 4. IMPOSTA LA NUOVA SCHERMATA
          gameApp.setScreen(newScreen);

          Gdx.app.log("Controller", "Restart effettuato con successo.");
      });
  }*/
}
