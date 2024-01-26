package de.tum.cit.ase.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.maze.MazeRunnerGame;
import de.tum.cit.ase.maze.utils.CONSTANTS;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;


/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {

    private final Stage stage;


    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(MazeRunnerGame game) {
        var camera = new OrthographicCamera();
        camera.zoom = 1.25f; // Set camera zoom for a closer view

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // Add a label as a title
        table.add(new Label("Welcome to [Gamename]!", game.getSkin(), "title")).padBottom(80).row();

        // Create and add a button to go to the game screen
        TextButton goToGameButton = new TextButton("Start new journey", game.getSkin());
        table.add(goToGameButton).width(400).row();
        goToGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToGame(false); // Change to the game screen when button is pressed
            }
        });
        // additional buttons and functionality for menu
        //TODO: pausing game when pressing esc
        TextButton continueGameButton = new TextButton("Continue journey", game.getSkin());
        table.add(continueGameButton).width(400).row();
        continueGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.resume(); // Change to the game screen when button is pressed
            }
        });
        /*TextButton exitGameButton = new TextButton("Leave journey", game.getSkin());
        table.add(exitGameButton).width(400).row();
        continueGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu(); // Change to the game screen when button is pressed
            }
        });

         */

        //TODO fileChooser function
        // Create a button for file selection
        TextButton chooseMapButton = new TextButton("Choose map", game.getSkin());
        table.add(chooseMapButton).width(400).row();
        chooseMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Maps", "properties");
                fileChooser.setFileFilter(filter);
                int response = fileChooser.showOpenDialog(null);

                if (response == JFileChooser.APPROVE_OPTION) {
                    //MapLoader.loadMapFile(Gdx.files.internal(fileChooser.getSelectedFile().getPath()));
                    File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                    System.out.println(file);
                }
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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
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
