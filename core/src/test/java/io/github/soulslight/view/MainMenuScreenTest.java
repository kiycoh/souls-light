package io.github.soulslight.view;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.github.soulslight.SoulsLightGame;
import io.github.soulslight.manager.EventManager;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MainMenuScreenTest {

  @BeforeAll
  public static void setup() {
    // Initialize Gdx with HeadlessApplication
    HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
    new HeadlessApplication(
        new ApplicationListener() {
          @Override
          public void create() {}

          @Override
          public void resize(int width, int height) {}

          @Override
          public void render() {}

          @Override
          public void pause() {}

          @Override
          public void resume() {}

          @Override
          public void dispose() {}
        },
        config);
    Gdx.gl = Mockito.mock(GL20.class);
    Gdx.gl20 = Mockito.mock(GL20.class);
  }

  @Test
  public void testAddMenuListener() {
    SoulsLightGame game = Mockito.mock(SoulsLightGame.class);
    SpriteBatch batch = Mockito.mock(SpriteBatch.class);
    MainMenuScreen screen = new MainMenuScreen(game, batch);

    screen.show();

    // Get the stage from the screen using reflection
    Stage stage = null;
    try {
      Field stageField = MainMenuScreen.class.getDeclaredField("stage");
      stageField.setAccessible(true);
      stage = (Stage) stageField.get(screen);
    } catch (Exception e) {
      fail("Could not access stage field");
    }

    assertNotNull(stage);

    // Find buttons
    // The structure is Stage -> Table -> Buttons
    // We know the first actor in stage is the Table
    Table table = (Table) stage.getActors().get(0);

    // Find "New Game" button. It's the first cell's actor.
    TextButton newGameButton = (TextButton) table.getCells().get(0).getActor();
    TextButton exitButton = (TextButton) table.getCells().get(3).getActor();

    // Mock Listener
    EventManager.GameListener listener = Mockito.mock(EventManager.GameListener.class);
    EventManager.getInstance().addListener(listener);

    // Simulate Click on New Game
    InputEvent event = new InputEvent();
    event.setType(InputEvent.Type.touchDown);
    newGameButton.fire(event);
    event.setType(InputEvent.Type.touchUp);
    newGameButton.fire(event);

    Mockito.verify(listener, times(1)).onNewGame();

    // Simulate Click on Exit
    event = new InputEvent();
    event.setType(InputEvent.Type.touchDown);
    exitButton.fire(event);
    event.setType(InputEvent.Type.touchUp);
    exitButton.fire(event);

    Mockito.verify(listener, times(1)).onExit();
  }
}
