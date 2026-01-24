package io.github.soulslight.view;

import com.badlogic.gdx.Screen;

/**
 * Pattern: State Sealed interface managing the screen transitions and states. Implementations must
 * be final because of sealed hierarchy.
 */
public sealed interface GameState extends Screen
    permits SplashScreen,
    MainMenuScreen,
    ClassSelectionScreen,
    GameScreen,
    SettingsScreen,
    IntroScreen {}
