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
}
