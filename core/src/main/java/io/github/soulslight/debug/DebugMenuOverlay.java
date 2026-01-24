package io.github.soulslight.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;

/**
 * Renders the debug menu overlay as a semi-transparent panel. Displays registered commands and
 * highlights the selected one.
 */
public class DebugMenuOverlay implements Disposable {

  private final DebugMenuController controller;
  private final ShapeRenderer shapeRenderer;
  private final BitmapFont font;

  private static final float MENU_WIDTH = 380f;
  private static final float ITEM_HEIGHT = 28f;
  private static final float PADDING = 12f;
  private static final float TITLE_HEIGHT = 35f;
  private static final float FOOTER_HEIGHT = 25f;

  public DebugMenuOverlay(DebugMenuController controller) {
    this.controller = controller;
    this.shapeRenderer = new ShapeRenderer();
    this.font = new BitmapFont();
    this.font.getData().setScale(1.1f);
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
    float menuHeight = (commandCount * ITEM_HEIGHT) + TITLE_HEIGHT + FOOTER_HEIGHT + (2 * PADDING);

    float menuX = (screenW - MENU_WIDTH) / 2f;
    float menuY = (screenH - menuHeight) / 2f;

    // Use screen-space projection
    Matrix4 uiMatrix = new Matrix4().setToOrtho2D(0, 0, screenW, screenH);

    // Draw semi-transparent background
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    shapeRenderer.setProjectionMatrix(uiMatrix);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

    // Main background
    shapeRenderer.setColor(0.05f, 0.05f, 0.1f, 0.92f);
    shapeRenderer.rect(menuX, menuY, MENU_WIDTH, menuHeight);

    // Title bar
    shapeRenderer.setColor(0.15f, 0.15f, 0.25f, 1f);
    shapeRenderer.rect(menuX, menuY + menuHeight - TITLE_HEIGHT, MENU_WIDTH, TITLE_HEIGHT);

    // Selection highlight
    int selectedIndex = controller.getSelectedIndex();
    if (commandCount > 0) {
      float highlightY =
          menuY + menuHeight - TITLE_HEIGHT - PADDING - ((selectedIndex + 1) * ITEM_HEIGHT);
      shapeRenderer.setColor(0.2f, 0.4f, 0.6f, 0.7f);
      shapeRenderer.rect(menuX + 4, highlightY, MENU_WIDTH - 8, ITEM_HEIGHT);
    }

    shapeRenderer.end();

    // Draw text
    batch.setProjectionMatrix(uiMatrix);
    batch.begin();

    // Title
    font.setColor(Color.GOLD);
    font.draw(batch, "[DEBUG MENU]", menuX + PADDING, menuY + menuHeight - PADDING);

    // Command list
    float itemY = menuY + menuHeight - TITLE_HEIGHT - PADDING - 6f;

    for (int i = 0; i < commandCount; i++) {
      DebugCommand cmd = controller.getCommands().get(i);
      boolean isSelected = (i == selectedIndex);

      font.setColor(isSelected ? Color.CYAN : Color.WHITE);
      String prefix = isSelected ? "> " : "  ";
      font.draw(batch, prefix + cmd.getName(), menuX + PADDING, itemY);
      itemY -= ITEM_HEIGHT;
    }

    // Footer instructions
    font.setColor(Color.GRAY);
    font.draw(
        batch,
        "[UP/DOWN] Navigate  [ENTER] Execute  [F1] Close",
        menuX + PADDING,
        menuY + FOOTER_HEIGHT);

    batch.end();

    Gdx.gl.glDisable(GL20.GL_BLEND);
  }

  @Override
  public void dispose() {
    shapeRenderer.dispose();
    font.dispose();
  }
}
