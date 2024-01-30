package de.tum.cit.ase.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.maze.MazeRunnerGame;
import de.tum.cit.ase.maze.utils.CONSTANTS;
import de.tum.cit.ase.maze.utils.MapLoader;
import de.tum.cit.ase.maze.utils.exceptions.MapLoadingException;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;
import games.spooky.gdx.nativefilechooser.NativeFileChooserIntent;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


/**
 * The MenuScreen class represents the main menu screen of the game.
 */
public class MenuScreen implements Screen {

    private final Stage stage;

    private Texture backgroundTexture; // Declare a Texture for the background
    private TextureRegionDrawable backgroundDrawable; // Declare a TextureRegionDrawable for the background

    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(MazeRunnerGame game) {
        var camera = new OrthographicCamera();
        camera.zoom = 1f; // Set camera zoom for a closer view

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        //set background for table
        backgroundTexture = new Texture("backgrd.png");
        backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));

        table.setBackground(backgroundDrawable);

        // Add a label as a title
        table.add(new Label("Welcome to Quantum Maze Quest!", game.getSkin(), "title")).padBottom(80).row();

        // Create and add a button to go to the game screen
        var prefs = Gdx.app.getPreferences("maze-game-general");
        var lastMapCont = prefs.contains("LastMap");

        TextButton quickStart = new TextButton("QuickStart", game.getSkin());
        FileHandle lstMapFile = null;
        if (!lastMapCont) {
            quickStart.setDisabled(true);

        }else {
            lstMapFile = new FileHandle(prefs.getString("LastMap"));
            try {
                MapLoader.loadMapFile(lstMapFile);
            } catch (MapLoadingException e) {
                quickStart.setDisabled(true);
            }
        }
        table.add(quickStart).width(400).row();

        FileHandle finalLstMapFile = lstMapFile;
        quickStart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (lastMapCont) {
                    MapLoader.loadMapFile(finalLstMapFile);

                }
                game.goToGame(false);
            }
        });
        // random map button
        TextButton randomMap = new TextButton("Random Map", game.getSkin());
        table.add(randomMap).width(400).spaceBottom(20).row();
        table.add(new Image(game.getSkin(), "divider-fade-000")).width(500).spaceBottom(20);
        table.row();


        // Create a Random object
        Random random = new Random();
        // Generate a random integer between 1 (included) and 5 (included)
        int randomInt = random.nextInt(5) + 1;


        randomMap.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                MapLoader.loadMapFile(Gdx.files.internal("level-" + randomInt + ".properties"));

                game.goToGame(false);
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

        TextButton editor = new TextButton("Map Editor", game.getSkin());
        table.add(editor).width(400).row();

        editor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToEditor();
            }
        });

        TextButton quitButt = new TextButton("Quit", game.getSkin());
        table.add(quitButt).width(400).spaceBottom(20).row();

        quitButt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
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
