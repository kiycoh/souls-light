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

  public void render(SpriteBatch batch, Player player, List<AbstractEnemy> enemies) {
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
    // FASE 2: HUD GIOCATORE (Coordinate SCHERMO / PIXEL)
    // =================================================================
    shapeRenderer.setProjectionMatrix(uiMatrix);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

    // Barra Rossa Sfondo
    // Posizione: 20px da sinistra, 30px dal bordo alto
    float barX = 20;
    float barY = screenH - 30;
    float barW = 200;
    float barH = 20;

    shapeRenderer.setColor(Color.RED);
    shapeRenderer.rect(barX, barY, barW, barH);

    // Barra Verde Vita Player
    if (player != null && !player.isDead()) {
      float hpPercent = player.getHealth() / player.getMaxHealth();
      shapeRenderer.setColor(Color.GREEN);
      shapeRenderer.rect(barX, barY, barW * Math.max(0, hpPercent), barH);
    }
    shapeRenderer.end();

    batch.setProjectionMatrix(uiMatrix);
    batch.begin();

    if (player != null && player.isDead()) {
      String text1 = "HAI PERSO";
      // String text2 = "Premi 'R' per risorgere";

      // Usiamo GlyphLayout per centrare perfettamente il testo
      font.setColor(Color.RED);
      layout.setText(font, text1);
      font.draw(batch, text1, (screenW - layout.width) / 2, (screenH / 2) + 50);

      /* font.setColor(Color.WHITE);
      layout.setText(font, text2);
      font.draw(batch, text2, (screenW - layout.width) / 2, (screenH / 2) - 20);*/
    }
    batch.end();
  }

  public void dispose() {
    shapeRenderer.dispose();
    font.dispose();
  }
}
