package de.tum.cit.ase.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.maze.MazeRunnerGame;
import de.tum.cit.ase.maze.utils.CONSTANTS;
import de.tum.cit.ase.maze.utils.MapLoader;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;
import games.spooky.gdx.nativefilechooser.NativeFileChooserIntent;


public class PauseScreen implements Screen {
    private final MazeRunnerGame game;
    private final Stage stage;
    private String playedMapPath;


    /**
     * Constructor for PauseScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public PauseScreen(MazeRunnerGame game) {
        var camera = new OrthographicCamera();
        this.game = game;
        camera.zoom = 1.25f; // Set camera zoom for a closer view

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // Add a label as a title
        table.add(new Label("Hello World from the Menu!", game.getSkin(), "title")).padBottom(80).row();

        // Create and add a button to go to the game screen

        // additional buttons and functionality for menu
        //TODO: pausing game when pressing esc
        TextButton continueGameButton = new TextButton("Continue journey", game.getSkin());
        table.add(continueGameButton).width(400).row();
        continueGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToGame(true);
                // Change to the game screen when button is pressed
            }
        });
        TextButton exitGameButton = new TextButton("Leave journey", game.getSkin());
        table.add(exitGameButton).width(400).row();
        exitGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu(); // Change to the game screen when button is pressed
            }
        });

        TextButton chooseMapButton = new TextButton("Choose map", game.getSkin());
        table.add(chooseMapButton).width(400).row();
        chooseMapButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                var config = new NativeFileChooserConfiguration();
                config.directory = Gdx.files.absolute(System.getProperty("user.home"));
                config.mimeFilter = "Maps/properties";
                config.intent = NativeFileChooserIntent.OPEN;
                var callBack = new NativeFileChooserCallback() {
                    @Override
                    public void onFileChosen(FileHandle file) {
                        MapLoader.loadMapFile(file);
                        game.goToGame(false);

                    }

                    @Override
                    public void onCancellation() {

                    }

                    @Override
                    public void onError(Exception exception) {
                        Gdx.app.error("load map", "Error loading map", exception);

                    }
                };
                game.getFileChooser().chooseFile(config, callBack);
            }
        });

        TextButton playAgainButton = new TextButton("Restart", game.getSkin());
        table.add(playAgainButton).width(400).row();
        playAgainButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // MapLoader.loadMapFile(Gdx.files.internal(playedMapPath));
                game.goToGame(false);
            }
        });


        stage.setDebugAll(CONSTANTS.DEBUG);
    }

    @Override
    public void show() {
        // Set the input processor so the stage can receive input events

        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen
        stage.getViewport().apply(true);
        stage.act(delta); // Update the stage
        stage.draw(); // Draw the stage
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
    }

    // The following methods are part of the Screen interface but are not used in this screen.
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();
    }
}
