package io.github.soulslight.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import io.github.soulslight.manager.ResourceManager;
import io.github.soulslight.model.enemies.AbstractEnemy;
import io.github.soulslight.model.entities.Player;
import io.github.soulslight.model.inventory.IPickable;
import io.github.soulslight.model.inventory.IStackable;
import io.github.soulslight.model.inventory.Inventory;
import io.github.soulslight.model.inventory.InventorySlot;
import io.github.soulslight.model.items.IRenderableItem;

public class GameHUD {

  private final ShapeRenderer shapeRenderer;
  private final BitmapFont font;
  private final GlyphLayout layout;
  private final Matrix4 uiMatrix;

  public GameHUD() {
    this.shapeRenderer = new ShapeRenderer();
    this.font = new BitmapFont();
    this.font.getData().setScale(2);
    this.font.setUseIntegerPositions(false); // Smooth movement if needed
    this.layout = new GlyphLayout();

    // Setup UI Matrix for Virtual Resolution
    this.uiMatrix =
        new Matrix4()
            .setToOrtho2D(
                0,
                0,
                io.github.soulslight.model.Constants.V_WIDTH,
                io.github.soulslight.model.Constants.V_HEIGHT);
  }

  public void render(SpriteBatch batch, io.github.soulslight.model.GameModel model) {
    if (model == null) return;

    // Use Virtual Dimensions
    float screenW = io.github.soulslight.model.Constants.V_WIDTH;
    float screenH = io.github.soulslight.model.Constants.V_HEIGHT;

    // Matrice mondo (Per le barre che seguono i nemici)
    Matrix4 worldMatrix = batch.getProjectionMatrix().cpy();

    // Matrice UI (Per l'interfaccia fissa sullo schermo) - Now a class field

    // BARRE VITA NEMICI (Coordinate MONDO)
    shapeRenderer.setProjectionMatrix(worldMatrix);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

    java.util.List<Player> players = model.getPlayers();
    java.util.List<AbstractEnemy> enemies = model.getActiveEnemies();

    for (AbstractEnemy enemy : enemies) {
      if (enemy.isDead()) continue;

      float width = 32f;
      float x = enemy.getPosition().x - (width / 2);
      float y = enemy.getPosition().y + 20f;
      float hpPercent = enemy.getHealth() / enemy.getMaxHealth();

      // Sfondo (Nero/Grigio scuro)
      shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1f);
      shapeRenderer.rect(x - 1, y - 1, width + 2, 6);

      // Vita (Rosso)
      shapeRenderer.setColor(0.8f, 0.1f, 0.1f, 1f);
      shapeRenderer.rect(x, y, width * Math.max(0, hpPercent), 4);
    }
    shapeRenderer.end();

    // BAR REVIVE

    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    for (Player p : players) {
      if (p.getReviveAttemptTimer() > 0) {
        float width = 40f;
        float x = p.getPosition().x - (width / 2);
        float y = p.getPosition().y + 35f; // Above head
        float progress = Math.min(1.0f, p.getReviveAttemptTimer() / 5.0f);

        // Background
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1f);
        shapeRenderer.rect(x - 1, y - 1, width + 2, 6);

        // Progress (Yellow/Gold)
        shapeRenderer.setColor(1f, 0.8f, 0.2f, 1f);
        shapeRenderer.rect(x, y, width * progress, 4);
      }
    }
    shapeRenderer.end();

    // HUD GIOCATORI (Coordinate SCHERMO / PIXEL)

    shapeRenderer.setProjectionMatrix(uiMatrix);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

    // --- GLOBAL WILL BAR (Bottom Center) ---
    /*
     * float willW = 325; // Wider
     * float willH = 20; // Thickness 20
     * float willX = (screenW - willW) / 2;
     * float willY = 10;
     *
     * // Sfondo Will
     * shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1f);
     * shapeRenderer.rect(willX - 2, willY - 2, willW + 4, willH + 4);
     *
     * // Will Bar (Purple/Blue)
     * float willPercent = model.getCurrentWill() /
     * io.github.soulslight.model.GameModel.MAX_WILL;
     * shapeRenderer.setColor(0.4f, 0.2f, 0.9f, 1f); // Purple
     * shapeRenderer.rect(willX, willY, willW * Math.max(0, willPercent), willH);
     */

    // --- PLAYER 1 (Bottom Left) ---
    if (!players.isEmpty()) {
      Player p1 = players.get(0);
      drawPlayerHealthBar(p1, 20, 10, 140, 20, "P1"); // Same thickness (20), narrower width to fit
    }

    // --- PLAYER 2 (Bottom Right) ---
    if (players.size() > 1) {
      Player p2 = players.get(1);
      drawPlayerHealthBar(p2, screenW - 160, 10, 140, 20, "P2"); // Same thickness (20)
    }

    // --- MINIMAP (Top Left) ---
    drawMinimap(model, screenW, screenH);

    // --- INVENTORY HIGHLIGHTS (ShapeRenderer) ---
    drawValuesHighlights(players, screenW);

    shapeRenderer.end();

    // --- TEXT LAYER ---
    batch.setProjectionMatrix(uiMatrix);
    batch.begin();

    // Will Label
    /*
     * font.setColor(Color.WHITE);
     * font.getData().setScale(1.0f); // Smaller font for HUD
     * String willText = "WILL";
     * layout.setText(font, willText);
     * font.draw(batch, willText, screenW / 2 - layout.width / 2, willY + willH +
     * 15);
     */

    // P1 Label
    if (!players.isEmpty()) {
      font.draw(batch, "P1", 20, 10 + 20 + 15);
    }

    // P2 Label
    if (players.size() > 1) {
      String p2Label = "P2";
      layout.setText(font, p2Label);
      font.draw(batch, p2Label, screenW - 160 + 140 - layout.width, 10 + 20 + 15);
    }

    // Game Over / Dead Labels
    checkDeadLabels(batch, screenW, screenH, players);

    // Inventory Rendering
    drawInventories(batch, players, screenW);

    batch.end();
    font.getData().setScale(2); // Reset scale
  }

  private void drawInventories(SpriteBatch batch, java.util.List<Player> players, float screenW) {
    if (players.isEmpty()) return;

    // P1 Inventory (Bottom Left, above Health Bar)
    // HP Bar Y=10, H=20. Y_Inv = 10 + 20 + 20 = 50.
    float p1X = 20;
    float p1Y = 50;
    if (!players.isEmpty()) {
      drawInventory(batch, players.get(0).getInventory(), p1X, p1Y);
    }

    // P2 Inventory (Bottom Right, above Health Bar)
    if (players.size() > 1) {
      // HP Bar X = screenW - 160. Width 140. Ends at screenW - 20.
      // Inventory Width = 3*32 + 2*4 = 104.
      // Right Align: screenW - 20 - 104 = screenW - 124.
      float p2X = screenW - 124;
      float p2Y = 50;
      drawInventory(batch, players.get(1).getInventory(), p2X, p2Y);
    }
  }

  private void drawInventory(SpriteBatch batch, Inventory inventory, float startX, float startY) {
    if (inventory == null) return;

    float slotSize = 32f;
    float gap = 4f;

    com.badlogic.gdx.utils.Array<InventorySlot> slots = inventory.getItemSlots();
    for (int i = 0; i < slots.size; i++) {
      InventorySlot slot = slots.get(i);
      float x = startX + i * (slotSize + gap);
      float y = startY;

      // Background
      batch.draw(ResourceManager.getInstance().getInventorySlotTexture(), x, y, slotSize, slotSize);

      if (!slot.isEmpty()) {
        IPickable item = slot.peek();

        // Item Texture
        if (item instanceof IRenderableItem) {
          IRenderableItem renderable = (IRenderableItem) item;
          if (renderable.getTexture() != null) {
            batch.draw(renderable.getTexture(), x, y, slotSize, slotSize);
          }
        }

        // Stack Count
        if (item instanceof IStackable && slot.getAmount() > 1) {
          font.getData().setScale(1.0f);
          font.setColor(Color.WHITE);
          // Draw bottom-right corner of slot
          font.draw(batch, String.valueOf(slot.getAmount()), x + slotSize - 12, y + 12);
        }
      }
    }
  }

  private void drawPlayerHealthBar(Player p, float x, float y, float w, float h, String label) {
    // Sfondo (Border)
    shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
    shapeRenderer.rect(x - 2, y - 2, w + 4, h + 4);

    // Background Bar (Empty portion)
    shapeRenderer.setColor(0.1f, 0.0f, 0.0f, 1f); // Dark Red
    shapeRenderer.rect(x, y, w, h);

    // Vita (Red)
    if (!p.isDead()) {
      float hpPercent = p.getHealth() / p.getMaxHealth();
      shapeRenderer.setColor(0.9f, 0.1f, 0.1f, 1f);
      shapeRenderer.rect(x, y, w * Math.max(0, hpPercent), h);

      // Shine/Highlight
      shapeRenderer.setColor(1f, 0.3f, 0.3f, 0.3f);
      shapeRenderer.rect(x, y + h / 2, w * Math.max(0, hpPercent), h / 2);
    }
  }

  private void drawMinimap(
      io.github.soulslight.model.GameModel model, float screenW, float screenH) {
    if (model.getMap() == null) return;

    com.badlogic.gdx.maps.MapProperties prop = model.getMap().getProperties();
    int mapWidth = prop.get("width", Integer.class);
    int mapHeight = prop.get("height", Integer.class);
    int tileWidth = prop.get("tilewidth", Integer.class);
    int tileHeight = prop.get("tileheight", Integer.class);

    float worldW = mapWidth * tileWidth;
    float worldH = mapHeight * tileHeight;

    float mapSize = 100f; // Smaller minimap for 720p scale
    float mapX = 20;
    float mapY = screenH - mapSize - 20;

    // Background
    shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
    shapeRenderer.rect(mapX, mapY, mapSize, mapSize);

    // Border
    shapeRenderer.setColor(Color.GRAY);
    // Simple border
    // Better border with lines
    shapeRenderer.rect(mapX, mapY, mapSize, 2); // Bottom
    shapeRenderer.rect(mapX, mapY + mapSize - 2, mapSize, 2); // Top
    shapeRenderer.rect(mapX, mapY, 2, mapSize); // Left
    shapeRenderer.rect(mapX + mapSize - 2, mapY, 2, mapSize); // Right

    // Dots
    float scaleX = mapSize / worldW;
    float scaleY = mapSize / worldH;

    // Players (Green)
    shapeRenderer.setColor(Color.GREEN);
    for (Player p : model.getPlayers()) {
      if (!p.isDead()) {
        shapeRenderer.circle(
            mapX + p.getPosition().x * scaleX, mapY + p.getPosition().y * scaleY, 2);
      }
    }

    // Enemies (Red)
    shapeRenderer.setColor(Color.RED);
    for (AbstractEnemy e : model.getActiveEnemies()) {
      if (!e.isDead()) {
        shapeRenderer.circle(
            mapX + e.getPosition().x * scaleX, mapY + e.getPosition().y * scaleY, 2);
      }
    }

    // Portal (Purple)
    if (model.getLevel() != null && model.getLevel().getCavePortal() != null) {
      io.github.soulslight.model.room.Portal portal = model.getLevel().getCavePortal();
      shapeRenderer.setColor(Color.PURPLE);
      shapeRenderer.circle(
          mapX + portal.getPosition().x * scaleX, mapY + portal.getPosition().y * scaleY, 3);
    }

    // Dungeon Portal Room
    if (model.getLevel() != null && model.getLevel().getRoomManager() != null) {
      io.github.soulslight.model.room.PortalRoom pr =
          model.getLevel().getRoomManager().getPortalRoom();
      if (pr != null && pr.getPortal() != null) {
        io.github.soulslight.model.room.Portal portal = pr.getPortal();
        shapeRenderer.setColor(Color.PURPLE);
        shapeRenderer.circle(
            mapX + portal.getPosition().x * scaleX, mapY + portal.getPosition().y * scaleY, 3);
      }
    }
  }

  private void checkDeadLabels(
      SpriteBatch batch, float screenW, float screenH, java.util.List<Player> players) {
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
      font.draw(batch, text1, (screenW - layout.width) / 2, (screenH / 2) + 20);
    } else {
      // Show "P1 Dead" or "P2 Dead" small text
      font.getData().setScale(1.5f);
      if (!players.isEmpty() && players.get(0).isDead()) {
        font.setColor(Color.RED);
        font.draw(batch, "DEAD", 20 + 20, 10 + 20 + 15);
      }
      if (players.size() > 1 && players.get(1).isDead()) {
        font.setColor(Color.RED);
        String txt = "DEAD";
        layout.setText(font, txt);
        font.draw(batch, txt, screenW - 160 + 120 - layout.width, 10 + 20 + 15);
      }
      font.getData().setScale(2f);
    }
  }

  private void drawValuesHighlights(java.util.List<Player> players, float screenW) {
    if (players.isEmpty()) return;

    // P1 Inventory Highlight
    float p1X = 20;
    float p1Y = 50;
    if (!players.isEmpty()) {
      drawSelectionBox(players.get(0), p1X, p1Y);
    }

    // P2 Inventory Highlight
    if (players.size() > 1) {
      float p2X = screenW - 124;
      float p2Y = 50;
      drawSelectionBox(players.get(1), p2X, p2Y);
    }
  }

  private void drawSelectionBox(Player p, float startX, float startY) {
    if (p.getInventory() == null) return;
    int selected = p.getSelectedSlotIndex();

    float slotSize = 32f;
    float gap = 4f;

    float x = startX + selected * (slotSize + gap);
    float y = startY;

    // Draw Yellow Box BEHIND the slot
    // Slightly larger than slot
    shapeRenderer.setColor(Color.YELLOW);
    shapeRenderer.rect(x - 2, y - 2, slotSize + 4, slotSize + 4);
  }

  public void dispose() {
    shapeRenderer.dispose();
    font.dispose();
  }
}
