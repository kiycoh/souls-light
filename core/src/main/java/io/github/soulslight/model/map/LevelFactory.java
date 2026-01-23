package io.github.soulslight.model.map;

import io.github.soulslight.manager.GameMode;

/**
 * Pattern: Factory Method
 * Encapsulates the logic of creating levels based on level number and game
 * mode.
 * Selects appropriate MapGenerationStrategy and enemy configuration.
 */
public class LevelFactory {

    // Map dimensions
    private static final int DEFAULT_WIDTH = 50;
    private static final int DEFAULT_HEIGHT = 50;
    private static final int BOSS_ARENA_WIDTH = 60;
    private static final int BOSS_ARENA_HEIGHT = 60;

    // Cave generation parameters
    private static final float CAVE_WALL_FILL = 0.45f;
    private static final int CAVE_OCTAVES = 3;
    private static final float CAVE_FREQUENCY = 0.1f;

    // Dungeon generation is handled by DungeonMapStrategy defaults

    /**
     * Creates the appropriate map generation strategy for the given level.
     *
     * @param levelNumber 1-based level index (1-5 for Story Mode)
     * @param gameMode    The current game mode
     * @param seed        Random seed for generation
     * @return The appropriate MapGenerationStrategy
     */
    public static MapGenerationStrategy createStrategy(int levelNumber, GameMode gameMode, long seed) {
        if (gameMode == GameMode.CUSTOM) {
            // Custom mode: use noise-based caves by default
            return new NoiseMapStrategy(
                    seed, DEFAULT_WIDTH, DEFAULT_HEIGHT, CAVE_FREQUENCY, CAVE_OCTAVES, CAVE_WALL_FILL);
        }

        // Story Mode: level-based strategy selection
        return switch (levelNumber) {
            case 1, 2 -> new DungeonMapStrategy(seed, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            case 3, 4 -> new NoiseMapStrategy(
                    seed, DEFAULT_WIDTH, DEFAULT_HEIGHT, CAVE_FREQUENCY, CAVE_OCTAVES, CAVE_WALL_FILL);
            case 5 -> new BossArenaStrategy(seed, BOSS_ARENA_WIDTH, BOSS_ARENA_HEIGHT);
            default -> new DungeonMapStrategy(seed, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        };
    }

    /**
     * Enemy configuration record for a level.
     */
    public record EnemyConfig(int melee, int ranged, int tank, int ball, boolean spawnBoss) {
    }

    /**
     * Gets the enemy configuration for a given level.
     *
     * @param levelNumber 1-based level index
     * @param gameMode    The current game mode
     * @return Enemy spawn configuration
     */
    public static EnemyConfig getEnemyConfig(int levelNumber, GameMode gameMode) {
        if (gameMode == GameMode.CUSTOM) {
            // Custom mode: balanced enemy mix
            return new EnemyConfig(8, 4, 3, 2, false);
        }

        // Story Mode: progressive difficulty
        return switch (levelNumber) {
            case 1 -> new EnemyConfig(4, 2, 1, 0, false); // Easy: few enemies
            case 2 -> new EnemyConfig(6, 3, 2, 1, false); // Medium: more enemies
            case 3 -> new EnemyConfig(8, 4, 2, 2, false); // Harder: cave enemies
            case 4 -> new EnemyConfig(10, 5, 3, 3, false); // Hard: many enemies
            case 5 -> new EnemyConfig(0, 0, 0, 0, true); // Boss only
            default -> new EnemyConfig(4, 2, 1, 0, false);
        };
    }

    /**
     * Gets the total number of levels in Story Mode.
     *
     * @return Number of levels (5)
     */
    public static int getStoryModeLevelCount() {
        return 5;
    }

    /**
     * Gets a descriptive name for the level.
     *
     * @param levelNumber 1-based level index
     * @return Human-readable level name
     */
    public static String getLevelName(int levelNumber) {
        return switch (levelNumber) {
            case 1 -> "The Dungeon Entrance";
            case 2 -> "The Catacombs";
            case 3 -> "The Crystal Caves";
            case 4 -> "The Deep Caverns";
            case 5 -> "Oblivion's Lair";
            default -> "Unknown Level";
        };
    }
}
