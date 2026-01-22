package io.github.soulslight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout; // Utile per centrare il testo
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.Player;
import java.util.List;

public class GameHUD {

  private final ShapeRenderer shapeRenderer;
  private final BitmapFont font;
  private final GlyphLayout layout; // Oggetto per calcolare la larghezza del testo

  public GameHUD() {
    this.shapeRenderer = new ShapeRenderer();
    this.font = new BitmapFont();
    this.font.getData().setScale(2);
    this.layout = new GlyphLayout();
  }

  public void render(SpriteBatch batch, List<Player> players, List<AbstractEnemy> enemies) {
    // Otteniamo dimensioni schermo attuali
    float screenW = Gdx.graphics.getWidth();
    float screenH = Gdx.graphics.getHeight();

    // Matrice mondo (Per le barre che seguono i nemici)
    Matrix4 worldMatrix = batch.getProjectionMatrix().cpy();

    // Matrice UI (Per l'interfaccia fissa sullo schermo)
    Matrix4 uiMatrix = new Matrix4().setToOrtho2D(0, 0, screenW, screenH);

    // =================================================================
    // FASE 1: BARRE VITA NEMICI (Coordinate MONDO)
    // =================================================================
    shapeRenderer.setProjectionMatrix(worldMatrix);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

    for (AbstractEnemy enemy : enemies) {
      if (enemy.isDead()) continue;

      float width = 32f;
      float x = enemy.getPosition().x - (width / 2);
      float y = enemy.getPosition().y + 20f;
      float hpPercent = enemy.getHealth() / enemy.getMaxHealth();

      shapeRenderer.setColor(Color.RED);
      shapeRenderer.rect(x, y, width, 4);

      shapeRenderer.setColor(Color.GREEN);
      shapeRenderer.rect(x, y, width * Math.max(0, hpPercent), 4);
    }
    shapeRenderer.end();

    // =================================================================
    // FASE 2: HUD GIOCATORI (Coordinate SCHERMO / PIXEL)
    // =================================================================
    shapeRenderer.setProjectionMatrix(uiMatrix);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

    // --- PLAYER 1 (Top Left) ---
    if (!players.isEmpty()) {
      Player p1 = players.get(0);
      float barX = 20;
      float barY = screenH - 30;
      float barW = 200;
      float barH = 20;

      // Sfondo
      shapeRenderer.setColor(Color.RED);
      shapeRenderer.rect(barX, barY, barW, barH);

      // Vita
      if (!p1.isDead()) {
        float hpPercent = p1.getHealth() / p1.getMaxHealth();
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(barX, barY, barW * Math.max(0, hpPercent), barH);
      }
    }

    // --- PLAYER 2 (Top Right) ---
    if (players.size() > 1) {
      Player p2 = players.get(1);
      float barW = 200;
      float barH = 20;
      float barX = screenW - barW - 20;
      float barY = screenH - 30;

      // Sfondo
      shapeRenderer.setColor(Color.RED);
      shapeRenderer.rect(barX, barY, barW, barH);

      // Vita (Blue/Cyan for P2 distinction?)
      if (!p2.isDead()) {
        float hpPercent = p2.getHealth() / p2.getMaxHealth();
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(barX, barY, barW * Math.max(0, hpPercent), barH);
      }
    }

    shapeRenderer.end();

    batch.setProjectionMatrix(uiMatrix);
    batch.begin();

    boolean allDead = true;
    for (Player p : players) {
      if (!p.isDead()) {
        allDead = false;
        break;
      }
    }

    if (allDead && !players.isEmpty()) {
      String text1 = "GAME OVER";
      font.setColor(Color.RED);
      layout.setText(font, text1);
      font.draw(batch, text1, (screenW - layout.width) / 2, (screenH / 2) + 50);
    } else {
      // Show "P1 Dead" or "P2 Dead" small text
      if (!players.isEmpty() && players.get(0).isDead()) {
        font.setColor(Color.RED);
        font.draw(batch, "P1 DEAD", 20, screenH - 40);
      }
      if (players.size() > 1 && players.get(1).isDead()) {
        font.setColor(Color.RED);
        String txt = "P2 DEAD";
        layout.setText(font, txt);
        font.draw(batch, txt, screenW - layout.width - 20, screenH - 40);
      }
    }

    batch.end();
  }

  public void dispose() {
    shapeRenderer.dispose();
    font.dispose();
  }
}
