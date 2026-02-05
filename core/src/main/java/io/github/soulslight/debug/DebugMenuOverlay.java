package io.github.soulslight.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import io.github.soulslight.model.Constants;
import io.github.soulslight.model.GameModel;

/**
 * Renders the debug menu overlay as a semi-transparent panel. Displays registered commands and
 * highlights the selected one.
 */
public class DebugMenuOverlay implements Disposable {

  private final DebugMenuController controller;
  private final ShapeRenderer shapeRenderer;
  private final BitmapFont font;

  private final GameModel model;
  private final Matrix4 uiMatrix;
  private final StringBuilder sb = new StringBuilder();

  public DebugMenuOverlay(DebugMenuController controller, GameModel model) {
    this.controller = controller;
    this.model = model;
    this.shapeRenderer = new ShapeRenderer();
    this.font = new BitmapFont();
    this.font.getData().setScale(1.1f);
    this.uiMatrix = new Matrix4();
  }

  /**
   * Renders the debug menu overlay.
   *
   * @param batch The SpriteBatch to use for text rendering
   */
  public void render(SpriteBatch batch) {
    if (!controller.isVisible()) return;

    float screenW = Gdx.graphics.getWidth();
    float screenH = Gdx.graphics.getHeight();

    int commandCount = controller.getCommands().size();
    float menuHeight =
        (commandCount * Constants.DEBUG_ITEM_HEIGHT)
            + Constants.DEBUG_TITLE_HEIGHT
            + Constants.DEBUG_FOOTER_HEIGHT
            + (2 * Constants.DEBUG_PADDING);

    float menuX = (screenW - Constants.DEBUG_MENU_WIDTH) / 2f;
    float menuY = (screenH - menuHeight) / 2f;

    // Use screen-space projection
    uiMatrix.setToOrtho2D(0, 0, screenW, screenH);

    // Draw semi-transparent background
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    shapeRenderer.setProjectionMatrix(uiMatrix);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

    // Main background
    shapeRenderer.setColor(0.05f, 0.05f, 0.1f, 0.92f);
    shapeRenderer.rect(menuX, menuY, Constants.DEBUG_MENU_WIDTH, menuHeight);

    // Title bar
    shapeRenderer.setColor(0.15f, 0.15f, 0.25f, 1f);
    shapeRenderer.rect(
        menuX,
        menuY + menuHeight - Constants.DEBUG_TITLE_HEIGHT,
        Constants.DEBUG_MENU_WIDTH,
        Constants.DEBUG_TITLE_HEIGHT);

    // Selection highlight
    int selectedIndex = controller.getSelectedIndex();
    if (commandCount > 0) {
      float highlightY =
          menuY
              + menuHeight
              - Constants.DEBUG_TITLE_HEIGHT
              - Constants.DEBUG_PADDING
              - ((selectedIndex + 1) * Constants.DEBUG_ITEM_HEIGHT);
      shapeRenderer.setColor(0.2f, 0.4f, 0.6f, 0.7f);
      shapeRenderer.rect(
          menuX + 4, highlightY, Constants.DEBUG_MENU_WIDTH - 8, Constants.DEBUG_ITEM_HEIGHT);
    }

    // --- Statistics Panel Background ---
    float statsX = menuX + Constants.DEBUG_MENU_WIDTH + 20f; // To the right of the menu
    float statsY = menuY + menuHeight - Constants.DEBUG_STATS_HEIGHT;

    // Ensure stats panel stays on screen (move to left if too far right)
    if (statsX + Constants.DEBUG_STATS_WIDTH > screenW) {
      statsX = menuX - Constants.DEBUG_STATS_WIDTH - 20f;
    }

    shapeRenderer.setColor(0.05f, 0.05f, 0.1f, 0.85f);
    shapeRenderer.rect(statsX, statsY, Constants.DEBUG_STATS_WIDTH, Constants.DEBUG_STATS_HEIGHT);

    // Stats Title Bar
    shapeRenderer.setColor(0.15f, 0.25f, 0.15f, 1f);
    shapeRenderer.rect(
        statsX, statsY + Constants.DEBUG_STATS_HEIGHT - 25f, Constants.DEBUG_STATS_WIDTH, 25f);

    shapeRenderer.end();

    // Draw text
    batch.setProjectionMatrix(uiMatrix);
    batch.begin();

    // Title
    font.setColor(Color.GOLD);
    font.draw(
        batch,
        "[DEBUG MENU]",
        menuX + Constants.DEBUG_PADDING,
        menuY + menuHeight - Constants.DEBUG_PADDING);

    // Command list
    float itemY = menuY + menuHeight - Constants.DEBUG_TITLE_HEIGHT - Constants.DEBUG_PADDING - 6f;

    for (int i = 0; i < commandCount; i++) {
      DebugCommand cmd = controller.getCommands().get(i);
      boolean isSelected = (i == selectedIndex);

      font.setColor(isSelected ? Color.CYAN : Color.WHITE);
      String prefix = isSelected ? "> " : "  ";
      font.draw(batch, prefix + cmd.getName(), menuX + Constants.DEBUG_PADDING, itemY);
      itemY -= Constants.DEBUG_ITEM_HEIGHT;
    }

    // Footer instructions
    font.setColor(Color.GRAY);
    font.draw(
        batch,
        "[UP/DOWN] Navigate  [ENTER] Execute  [F1] Close",
        menuX + Constants.DEBUG_PADDING,
        menuY + Constants.DEBUG_FOOTER_HEIGHT);

    // --- Statistics Text ---
    float currentStatsY = statsY + Constants.DEBUG_STATS_HEIGHT - 8f;
    float statsXText = statsX + Constants.DEBUG_STATS_PADDING;

    font.setColor(Color.GREEN);
    font.draw(batch, "GAME STATISTICS", statsXText, currentStatsY);
    currentStatsY -= 25f;

    font.setColor(Color.WHITE);

    sb.setLength(0);
    sb.append("FPS: ").append(Gdx.graphics.getFramesPerSecond());
    font.draw(batch, sb, statsXText, currentStatsY);
    currentStatsY -= Constants.DEBUG_STATS_LINE_HEIGHT;

    sb.setLength(0);
    sb.append("Level: ")
        .append(io.github.soulslight.manager.GameManager.getInstance().getCurrentLevelIndex());
    font.draw(batch, sb, statsXText, currentStatsY);
    currentStatsY -= Constants.DEBUG_STATS_LINE_HEIGHT;

    sb.setLength(0);
    sb.append("Active Enemies: ")
        .append(model.getActiveEnemies() != null ? model.getActiveEnemies().size() : 0);
    font.draw(batch, sb, statsXText, currentStatsY);
    currentStatsY -= Constants.DEBUG_STATS_LINE_HEIGHT;

    sb.setLength(0);
    sb.append("Enemies Killed: ").append(model.getTotalEnemiesKilled());
    font.draw(batch, sb, statsXText, currentStatsY);
    currentStatsY -= Constants.DEBUG_STATS_LINE_HEIGHT;

    if (!model.getPlayers().isEmpty()) {
      Vector2 pPos = model.getPlayers().get(0).getPosition();
      sb.setLength(0);
      sb.append("Player: (").append((int) pPos.x).append(", ").append((int) pPos.y).append(")");
      font.draw(batch, sb, statsXText, currentStatsY);
      currentStatsY -= Constants.DEBUG_STATS_LINE_HEIGHT;
    }

    long javaHeap = Gdx.app.getJavaHeap();
    sb.setLength(0);
    sb.append("Heap: ").append(javaHeap / 1024 / 1024).append(" MB");
    font.draw(batch, sb, statsXText, currentStatsY);

    batch.end();

    Gdx.gl.glDisable(GL20.GL_BLEND);
  }

  @Override
  public void dispose() {
    shapeRenderer.dispose();
    font.dispose();
  }
}
