package io.github.soulslight.model;

public class Constants {
  public static final float PPM = 32.0f;

  // Collision Filter Bits
  public static final short BIT_WALL = 1;
  public static final short BIT_PLAYER = 2;
  public static final short BIT_ENEMY = 4;
  public static final short BIT_SENSOR = 8;
  public static final short BIT_DOOR = 16;
  public static final short BIT_PROJECTILE = 32;
  public static final short BIT_ITEM = 64;

  // Virtual Screen Size
  public static final float V_WIDTH = 720f;
  public static final float V_HEIGHT = 480f;

  // Game Balance
  public static final float MAX_WILL = 100f;
  public static final float REVIVE_DISTANCE = 40f;
  public static final float REVIVE_TIME = 5.0f;

  // Projectile Types
  public static final String PROJ_ARROW = "arrow";
  public static final String PROJ_FAST_ARROW = "fast_arrow";
  public static final String PROJ_FIREBALL = "fireball";
  public static final String PROJ_HOMING_FIREBALL = "homing_fireball";
  public static final String PROJ_ENEMY_ARROW = "enemy_arrow";
  public static final String PROJ_ENEMY_FIREBALL = "enemy_fireball";

  // Projectile Speeds
  public static final float SPEED_ARROW = 400f;
  public static final float SPEED_FAST_ARROW = 700f;
  public static final float SPEED_ENEMY_ARROW = 300f;

  // Debug Menu UI
  public static final float DEBUG_MENU_WIDTH = 380f;
  public static final float DEBUG_ITEM_HEIGHT = 28f;
  public static final float DEBUG_PADDING = 12f;
  public static final float DEBUG_TITLE_HEIGHT = 35f;
  public static final float DEBUG_FOOTER_HEIGHT = 25f;

  // Debug Stats UI
  public static final float DEBUG_STATS_WIDTH = 250f;
  public static final float DEBUG_STATS_HEIGHT = 160f;
  public static final float DEBUG_STATS_PADDING = 10f;
  public static final float DEBUG_STATS_LINE_HEIGHT = 20f;

  // Entity Rendering & Physics
  public static final float IDLE_VELOCITY_EPS = 0.05f;
  public static final float ENEMY_FLIP_EPS = 0.35f;
  public static final float OBLIVION_HEIGHT = 96f * 5f;
  public static final float OBLIVION_WIDTH = 173f * 5f;
  public static final float OBLIVION_Y_OFFSET = 80f;
}
