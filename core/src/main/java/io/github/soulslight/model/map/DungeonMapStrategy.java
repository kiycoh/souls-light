package io.github.soulslight.model.map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import io.github.soulslight.manager.ResourceManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public record DungeonMapStrategy(long seed, int width, int height)
    implements MapGenerationStrategy {

  private static final int TILE_SIZE = 32;
  private static final int CORRIDOR_WIDTH = 3;
  private static final int GRID_ROWS = 3;
  private static final int GRID_COLS = 3;

  // Padding inside a grid cell to avoid rooms touching edges too closely
  private static final int CELL_PADDING = 2;

  @Override
  public TiledMap generate() {
    var map = new TiledMap();
    var layer = new TiledMapTileLayer(width, height, TILE_SIZE, TILE_SIZE);

    var wallTile = new StaticTiledMapTile(ResourceManager.getInstance().getWallTextureRegion());
    var floorTile = new StaticTiledMapTile(ResourceManager.getInstance().getFloorTextureRegion());

    wallTile.getProperties().put("type", "wall");
    floorTile.getProperties().put("type", "floor");

    // 1. Fill with walls
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        var cell = new TiledMapTileLayer.Cell();
        cell.setTile(wallTile);
        layer.setCell(x, y, cell);
      }
    }

    var rng = new Random(seed);

    // 2. Generate Grid Path
    // Calculate cell size
    int cellW = width / GRID_COLS;
    int cellH = height / GRID_ROWS;

    // Generate a path of grid coordinates (e.g. [0,0], [0,1], [1,1]...)
    // We want a linear path that visits most cells but doesn't cross itself.
    // Simple approach: Snake or random walk that doesn't revisit.
    List<GridPoint> path = generateGridPath(rng);

    // 3. Place Rooms in Path Cells
    List<Room> rooms = new ArrayList<>();
    for (GridPoint p : path) {
      // Define bounds for this cell
      int minX = p.x * cellW + CELL_PADDING;
      int maxX = (p.x + 1) * cellW - CELL_PADDING;
      int minY = p.y * cellH + CELL_PADDING;
      int maxY = (p.y + 1) * cellH - CELL_PADDING;

      // Random room size within these bounds
      // Ensure room is at least MIN_ROOM_SIZE
      int maxRoomW = maxX - minX;
      int maxRoomH = maxY - minY;

      // Safety clamp
      if (maxRoomW < 6) maxRoomW = 6;
      if (maxRoomH < 6) maxRoomH = 6;

      int w = rng.nextInt(6, Math.min(15, maxRoomW));
      int h = rng.nextInt(6, Math.min(15, maxRoomH));

      // Center-ish position
      int x = rng.nextInt(minX, maxX - w + 1);
      int y = rng.nextInt(minY, maxY - h + 1);

      Room room = new Room(x, y, w, h);
      rooms.add(room);
      createRoom(room, layer, floorTile);
    }

    // 4. Connect Rooms Sequentially
    for (int i = 0; i < rooms.size() - 1; i++) {
      Room r1 = rooms.get(i);
      Room r2 = rooms.get(i + 1);
      connectRooms(r1, r2, layer, floorTile, rng);
    }

    map.getLayers().add(layer);
    return map;
  }

  /**
   * Generates a simple path through the grid. Implementation: Start at random edge, move to
   * unvisited neighbor until stuck or long enough.
   */
  private List<GridPoint> generateGridPath(Random rng) {
    List<GridPoint> path = new ArrayList<>();
    boolean[][] visited = new boolean[GRID_COLS][GRID_ROWS];

    // Start at (0,0) or (0, random) or random. Let's start at bottom-left for
    // simplicity (0,0)
    int cx = 0;
    int cy = 0;

    path.add(new GridPoint(cx, cy));
    visited[cx][cy] = true;

    boolean stuck = false;
    while (!stuck) {
      List<GridPoint> neighbors = new ArrayList<>();
      // Check neighbors (Up, Down, Left, Right)
      int[] dx = {0, 0, -1, 1};
      int[] dy = {1, -1, 0, 0};

      for (int i = 0; i < 4; i++) {
        int nx = cx + dx[i];
        int ny = cy + dy[i];

        if (nx >= 0 && nx < GRID_COLS && ny >= 0 && ny < GRID_ROWS && !visited[nx][ny]) {
          neighbors.add(new GridPoint(nx, ny));
        }
      }

      if (neighbors.isEmpty()) {
        stuck = true;
      } else {
        // Pick random neighbor
        GridPoint next = neighbors.get(rng.nextInt(neighbors.size()));
        cx = next.x;
        cy = next.y;
        path.add(next);
        visited[cx][cy] = true;
      }
    }
    return path;
  }

  private void createRoom(Room room, TiledMapTileLayer layer, StaticTiledMapTile floorTile) {
    for (int x = room.x; x < room.x + room.w; x++) {
      for (int y = room.y; y < room.y + room.h; y++) {
        var cell = new TiledMapTileLayer.Cell();
        cell.setTile(floorTile);
        layer.setCell(x, y, cell);
      }
    }
  }

  private void connectRooms(
      Room r1, Room r2, TiledMapTileLayer layer, StaticTiledMapTile floorTile, Random rng) {
    int x1 = r1.x + r1.w / 2;
    int y1 = r1.y + r1.h / 2;
    int x2 = r2.x + r2.w / 2;
    int y2 = r2.y + r2.h / 2;

    if (rng.nextBoolean()) {
      createHCorridor(x1, x2, y1, layer, floorTile);
      createVCorridor(y1, y2, x2, layer, floorTile);
    } else {
      createVCorridor(y1, y2, x1, layer, floorTile);
      createHCorridor(x1, x2, y2, layer, floorTile);
    }
  }

  private void createHCorridor(
      int x1, int x2, int y, TiledMapTileLayer layer, StaticTiledMapTile floorTile) {
    int startX = Math.min(x1, x2);
    int endX = Math.max(x1, x2);
    // Draw corridor with width
    int halfWidth = CORRIDOR_WIDTH / 2;

    for (int x = startX; x <= endX; x++) {
      for (int offset = -halfWidth; offset <= halfWidth; offset++) {
        int cy = y + offset;
        if (cy >= 0 && cy < height) {
          var cell = new TiledMapTileLayer.Cell();
          cell.setTile(floorTile);
          layer.setCell(x, cy, cell);
        }
      }
    }
  }

  private void createVCorridor(
      int y1, int y2, int x, TiledMapTileLayer layer, StaticTiledMapTile floorTile) {
    int startY = Math.min(y1, y2);
    int endY = Math.max(y1, y2);
    int halfWidth = CORRIDOR_WIDTH / 2;

    for (int y = startY; y <= endY; y++) {
      for (int offset = -halfWidth; offset <= halfWidth; offset++) {
        int cx = x + offset;
        if (cx >= 0 && cx < width) {
          var cell = new TiledMapTileLayer.Cell();
          cell.setTile(floorTile);
          layer.setCell(cx, y, cell);
        }
      }
    }
  }

  private record Room(int x, int y, int w, int h) {}

  private record GridPoint(int x, int y) {}
}
