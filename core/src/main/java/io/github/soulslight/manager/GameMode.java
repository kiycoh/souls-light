package io.github.soulslight.manager;

/**
 * Defines the game mode for campaign structure.
 * STORY: Linear 5-level progression with increasing difficulty.
 * CUSTOM: Sandbox mode with configurable parameters.
 */
public enum GameMode {
    /** 5-level campaign: Dungeon -> Cave -> Boss */
    STORY,

    /** Sandbox mode with custom configuration */
    CUSTOM
}
