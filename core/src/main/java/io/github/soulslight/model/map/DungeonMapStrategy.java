package io.github.soulslight.model.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import io.github.soulslight.manager.ResourceManager;
import io.github.soulslight.model.room.DoorPosition;
import io.github.soulslight.model.room.RoomData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public record DungeonMapStrategy(long seed, int width, int height)
    implements MapGenerationStrategy {

  private static final int TILE_SIZE = 32;
  private static final int CORRIDOR_WIDTH = 3;
  private static final int GRID_ROWS = 3;
  private static final int GRID_COLS = 3;

  // Padding inside a grid cell to avoid rooms touching edges too closely
  private static final int CELL_PADDING = 2;

  /** Key for storing room data in map properties. */
  public static final String ROOM_DATA_KEY = "roomData";

  @Override
  public TiledMap generate() {
    var map = new TiledMap();
    map.getProperties().put("width", width);
    map.getProperties().put("height", height);
    map.getProperties().put("tilewidth", TILE_SIZE);
    map.getProperties().put("tileheight", TILE_SIZE);

    var layer = new TiledMapTileLayer(width, height, TILE_SIZE, TILE_SIZE);

    ResourceManager rm = ResourceManager.getInstance();

    // --- FLOOR TILES ---
    TextureRegion[] floorRegions = rm.getFloorTextureRegions();
    StaticTiledMapTile[] floorTiles;

    if (floorRegions != null && floorRegions.length > 0) {
      floorTiles = new StaticTiledMapTile[floorRegions.length];
      for (int i = 0; i < floorRegions.length; i++) {
        StaticTiledMapTile t = new StaticTiledMapTile(floorRegions[i]);
        t.getProperties().put("type", "floor");
        floorTiles[i] = t;
      }
    } else {
      // fallback
      StaticTiledMapTile singleFloor = new StaticTiledMapTile(rm.getFloorTextureRegion());
      singleFloor.getProperties().put("type", "floor");
      floorTiles = new StaticTiledMapTile[] { singleFloor };
    }

    // --- WALL TILES ---
    TextureRegion[] wallRegions = rm.getWallMaskRegions();
    StaticTiledMapTile[] wallMaskTiles = new StaticTiledMapTile[16];
    TextureRegion fallbackWallRegion = rm.getWallTextureRegion();

    for (int i = 0; i < 16; i++) {
      TextureRegion region = null;
      if (wallRegions != null && i < wallRegions.length) {
        region = wallRegions[i];
      }
      if (region == null) {
        region = fallbackWallRegion;
      }
      StaticTiledMapTile t = new StaticTiledMapTile(region);
      t.getProperties().put("type", "wall");
      wallMaskTiles[i] = t;
    }

    // Generic wall used during initial fill
    StaticTiledMapTile genericWallTile = wallMaskTiles[15];

    // Inner Corner Tiles
    StaticTiledMapTile innerNeTile = new StaticTiledMapTile(rm.getInnerCornerWallNE());
    innerNeTile.getProperties().put("type", "wall");

    StaticTiledMapTile innerNwTile = new StaticTiledMapTile(rm.getInnerCornerWallNW());
    innerNwTile.getProperties().put("type", "wall");

    StaticTiledMapTile innerSeTile = new StaticTiledMapTile(rm.getInnerCornerWallSE());
    innerSeTile.getProperties().put("type", "wall");

    StaticTiledMapTile innerSwTile = new StaticTiledMapTile(rm.getInnerCornerWallSW());
    innerSwTile.getProperties().put("type", "wall");

    // 1. Fill with generic walls
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        setTile(layer, x, y, genericWallTile);
      }
    }

    var rng = new Random(seed);

    // 2. Generate Grid Path
    int cellW = width / GRID_COLS;
    int cellH = height / GRID_ROWS;
    List<GridPoint> path = generateGridPath(rng);

    // 3. Place Rooms in Path Cells
    List<Room> rooms = new ArrayList<>();
    for (GridPoint p : path) {
      int minX = p.x * cellW + CELL_PADDING;
      int maxX = (p.x + 1) * cellW - CELL_PADDING;
      int minY = p.y * cellH + CELL_PADDING;
      int maxY = (p.y + 1) * cellH - CELL_PADDING;

      int maxRoomW = Math.max(6, maxX - minX);
      int maxRoomH = Math.max(6, maxY - minY);

      // Safety check to ensure bounds are valid
      if (maxRoomW < 6)
        maxRoomW = 6;
      if (maxRoomH < 6)
        maxRoomH = 6;

      int w = rng.nextInt(6, Math.min(15, maxRoomW));
      int h = rng.nextInt(6, Math.min(15, maxRoomH));

      int x = rng.nextInt(Math.min(minX, maxX - w + 1), Math.max(minX + 1, maxX - w + 1));
      int y = rng.nextInt(Math.min(minY, maxY - h + 1), Math.max(minY + 1, maxY - h + 1));

      Room room = new Room(x, y, w, h);
      rooms.add(room);
      createRoom(room, layer, floorTiles, rng);
    }

    // 4. Connect Rooms Sequentially
    Map<Integer, List<DoorPosition>> roomDoors = new HashMap<>();
    for (int i = 0; i < rooms.size(); i++) {
      roomDoors.put(i, new ArrayList<>());
    }

    for (int i = 0; i < rooms.size() - 1; i++) {
      Room r1 = rooms.get(i);
      Room r2 = rooms.get(i + 1);
      connectRoomsWithDoors(r1, r2, layer, floorTiles, rng, i, i + 1, roomDoors);
    }

    // 5. Apply Wall Auto-tiling
    applyWallAutotiling(layer, wallMaskTiles, innerNeTile, innerNwTile, innerSeTile, innerSwTile);

    // 6. Store room data
    List<RoomData> roomDataList = new ArrayList<>();
    for (int i = 0; i < rooms.size(); i++) {
      Room r = rooms.get(i);
      float worldX = r.x * TILE_SIZE;
      float worldY = r.y * TILE_SIZE;
      float worldW = r.w * TILE_SIZE;
      float worldH = r.h * TILE_SIZE;

      boolean isPortal = (i == rooms.size() - 1);
      String roomId = "room-" + i;
      List<DoorPosition> doors = roomDoors.getOrDefault(i, Collections.emptyList());

      if (isPortal) {
        roomDataList.add(RoomData.portal(roomId, worldX, worldY, worldW, worldH, doors));
      } else {
        roomDataList.add(RoomData.standard(roomId, worldX, worldY, worldW, worldH, doors));
      }
    }
    map.getProperties().put(ROOM_DATA_KEY, roomDataList);

    map.getLayers().add(layer);
    return map;
  }

  // --- Helper Methods ---

  @SuppressWarnings("unchecked")
  public static List<RoomData> extractRoomData(TiledMap map) {
    Object data = map.getProperties().get(ROOM_DATA_KEY);
    if (data instanceof List<?>) {
      return (List<RoomData>) data;
    }
    return Collections.emptyList();
  }

  private List<GridPoint> generateGridPath(Random rng) {
    List<GridPoint> path = new ArrayList<>();
    boolean[][] visited = new boolean[GRID_COLS][GRID_ROWS];
    int cx = 0;
    int cy = 0;
    path.add(new GridPoint(cx, cy));
    visited[cx][cy] = true;

    boolean stuck = false;
    while (!stuck) {
      List<GridPoint> neighbors = new ArrayList<>();
      int[] dx = { 0, 0, -1, 1 };
      int[] dy = { 1, -1, 0, 0 };

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
        GridPoint next = neighbors.get(rng.nextInt(neighbors.size()));
        cx = next.x;
        cy = next.y;
        path.add(next);
        visited[cx][cy] = true;
      }
    }
    return path;
  }

  private void createRoom(Room room, TiledMapTileLayer layer, StaticTiledMapTile[] floorTiles, Random rng) {
    for (int x = room.x; x < room.x + room.w; x++) {
      for (int y = room.y; y < room.y + room.h; y++) {
        StaticTiledMapTile tile = floorTiles[rng.nextInt(floorTiles.length)];
        setTile(layer, x, y, tile);
      }
    }
  }

  private void connectRoomsWithDoors(
      Room r1, Room r2, TiledMapTileLayer layer, StaticTiledMapTile[] floorTiles, Random rng,
      int room1Idx, int room2Idx, Map<Integer, List<DoorPosition>> roomDoors) {

    int x1 = r1.x + r1.w / 2;
    int y1 = r1.y + r1.h / 2;
    int x2 = r2.x + r2.w / 2;
    int y2 = r2.y + r2.h / 2;

    if (rng.nextBoolean()) {
      createHCorridor(x1, x2, y1, layer, floorTiles, rng);
      createVCorridor(y1, y2, x2, layer, floorTiles, rng);
      DoorPosition door1 = calculateDoorForRoom(r1, x1, y1, x2, y2, true);
      roomDoors.get(room1Idx).add(door1);
    } else {
      createVCorridor(y1, y2, x1, layer, floorTiles, rng);
      createHCorridor(x1, x2, y2, layer, floorTiles, rng);
      DoorPosition door1 = calculateDoorForRoom(r1, x1, y1, x2, y2, false);
      roomDoors.get(room1Idx).add(door1);
    }
  }

  private DoorPosition calculateDoorForRoom(
      Room room, int roomCenterX, int roomCenterY, int targetX, int targetY, boolean isHorizontalFirst) {
    float centerPixelY = roomCenterY * TILE_SIZE + TILE_SIZE / 2f;
    float centerPixelX = roomCenterX * TILE_SIZE + TILE_SIZE / 2f;

    if (isHorizontalFirst) {
      if (targetX > roomCenterX) {
        float doorX = (room.x + room.w) * TILE_SIZE;
        return DoorPosition.of(doorX, centerPixelY, DoorPosition.Direction.EAST);
      } else {
        float doorX = room.x * TILE_SIZE;
        return DoorPosition.of(doorX, centerPixelY, DoorPosition.Direction.WEST);
      }
    } else {
      if (targetY > roomCenterY) {
        float doorY = (room.y + room.h) * TILE_SIZE;
        return DoorPosition.of(centerPixelX, doorY, DoorPosition.Direction.NORTH);
      } else {
        float doorY = room.y * TILE_SIZE;
        return DoorPosition.of(centerPixelX, doorY, DoorPosition.Direction.SOUTH);
      }
    }
  }

  private void createHCorridor(
      int x1, int x2, int y, TiledMapTileLayer layer, StaticTiledMapTile[] floorTiles, Random rng) {
    int startX = Math.min(x1, x2);
    int endX = Math.max(x1, x2);
    int halfWidth = CORRIDOR_WIDTH / 2;

    for (int x = startX; x <= endX; x++) {
      for (int offset = -halfWidth; offset <= halfWidth; offset++) {
        int cy = y + offset;
        if (cy >= 0 && cy < height) {
          setTile(layer, x, cy, floorTiles[rng.nextInt(floorTiles.length)]);
        }
      }
    }
  }

  private void createVCorridor(
      int y1, int y2, int x, TiledMapTileLayer layer, StaticTiledMapTile[] floorTiles, Random rng) {
    int startY = Math.min(y1, y2);
    int endY = Math.max(y1, y2);
    int halfWidth = CORRIDOR_WIDTH / 2;

    for (int y = startY; y <= endY; y++) {
      for (int offset = -halfWidth; offset <= halfWidth; offset++) {
        int cx = x + offset;
        if (cx >= 0 && cx < width) {
          setTile(layer, cx, y, floorTiles[rng.nextInt(floorTiles.length)]);
        }
      }
    }
  }

  // --- Auto-tiling Logic ---

  private void applyWallAutotiling(
      TiledMapTileLayer layer,
      StaticTiledMapTile[] wallMaskTiles,
      StaticTiledMapTile innerNeTile,
      StaticTiledMapTile innerNwTile,
      StaticTiledMapTile innerSeTile,
      StaticTiledMapTile innerSwTile) {

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if (!isWall(layer, x, y))
          continue;

        int mask = computeWallMask(layer, x, y);
        StaticTiledMapTile tile;

        if (mask == 15) {
          boolean neHole = !isWall(layer, x + 1, y + 1);
          boolean nwHole = !isWall(layer, x - 1, y + 1);
          boolean seHole = !isWall(layer, x + 1, y - 1);
          boolean swHole = !isWall(layer, x - 1, y - 1);
          int holes = (neHole ? 1 : 0) + (nwHole ? 1 : 0) + (seHole ? 1 : 0) + (swHole ? 1 : 0);

          if (holes == 1) {
            if (neHole)
              tile = innerNeTile;
            else if (nwHole)
              tile = innerNwTile;
            else if (seHole)
              tile = innerSeTile;
            else
              tile = innerSwTile;
          } else {
            tile = wallMaskTiles[15];
          }
        } else {
          if (mask < 0 || mask >= wallMaskTiles.length)
            mask = 15;
          tile = wallMaskTiles[mask];
          if (tile == null)
            tile = wallMaskTiles[15];
        }

        setTile(layer, x, y, tile);
      }
    }
  }

  private int computeWallMask(TiledMapTileLayer layer, int x, int y) {
    int mask = 0;
    if (isWall(layer, x, y + 1))
      mask |= 1; // up
    if (isWall(layer, x + 1, y))
      mask |= 2; // right
    if (isWall(layer, x, y - 1))
      mask |= 4; // down
    if (isWall(layer, x - 1, y))
      mask |= 8; // left
    return mask;
  }

  private void setTile(TiledMapTileLayer layer, int x, int y, StaticTiledMapTile tile) {
    if (layer.getCell(x, y) == null) {
      layer.setCell(x, y, new TiledMapTileLayer.Cell());
    }
    layer.getCell(x, y).setTile(tile);
  }

  private boolean isWall(TiledMapTileLayer layer, int x, int y) {
    if (x < 0 || x >= width || y < 0 || y >= height)
      return true;
    var cell = layer.getCell(x, y);
    return cell != null
        && cell.getTile() != null
        && "wall".equals(cell.getTile().getProperties().get("type"));
  }

  private record Room(int x, int y, int w, int h) {
  }

  private record GridPoint(int x, int y) {
  }
}
