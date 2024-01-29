package de.tum.cit.ase.editor.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.ase.editor.data.Map;
import de.tum.cit.ase.editor.input.CanvasGestureListener;
import de.tum.cit.ase.editor.input.CanvasInputProcessor;
import de.tum.cit.ase.editor.utlis.MapGenerator;
import de.tum.cit.ase.editor.utlis.exceptions.InvalidMapFile;
import de.tum.cit.ase.maze.MazeRunnerGame;
import de.tum.cit.ase.maze.screens.GameScreen;
import de.tum.cit.ase.maze.utils.CONSTANTS;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;
import games.spooky.gdx.nativefilechooser.NativeFileChooserIntent;

/**
 * The Editor class is responsible for managing the editor screen of the Maze Runner game.
 * It extends InputAdapter and implements Screen.
 */
public class Editor extends InputAdapter implements Screen {
    private final MazeRunnerGame game;
    private final EditorUi editorUi;
    private final EditorCanvas editorCanvas;
    private final InputMultiplexer inputMultiplexer;
    public ShapeRenderer shapeRenderer;
    private final NativeFileChooser fileChooser;
    public boolean saved = false;

    /**
     * Editor class is responsible for managing the editor functionality of the Maze Runner game.
     */
    public Editor(MazeRunnerGame game) {
        this.game = game;
        this.fileChooser = game.getFileChooser();
        this.shapeRenderer = new ShapeRenderer();
        this.editorCanvas = new EditorCanvas(this);
        this.editorUi = new EditorUi(this);
        var canvasInputProcessor = new CanvasInputProcessor(editorCanvas);
        this.inputMultiplexer = new InputMultiplexer(editorUi, new GestureDetector(new CanvasGestureListener(editorCanvas, canvasInputProcessor)), canvasInputProcessor, this);
        this.editorUi.setDebugAll(CONSTANTS.DEBUG);


    }

    /**
     * Chooses a file using a native file chooser dialog.
     *
     * @param filter     The MIME filter for the file chooser dialog.
     * @param title      The title of the file chooser dialog.
     * @param dialogType The type of file chooser dialog to show.
     * @param dir        The initial directory of the file chooser dialog.
     * @param callback   The callback to be called when a file is chosen or an error occurs.
     */
    public void chooseFile(String filter, String title, NativeFileChooserIntent dialogType, FileHandle dir, NativeFileChooserCallback callback) {


        NativeFileChooserConfiguration conf = new NativeFileChooserConfiguration();

// Starting from user's dir
        conf.directory = dir == null ? Gdx.files.absolute(System.getProperty("user.home")) : dir;

        conf.mimeFilter = filter;

// Add a nice title
        conf.title = title;
        conf.intent = dialogType;

        this.chooseFile(conf, callback);

    }

    /**
     * Chooses a file using a native file chooser dialog.
     *
     * @param configuration The configuration options for the file chooser dialog.
     * @param callback      The callback to be called when a file is chosen or an error occurs.
     */
    public void chooseFile(NativeFileChooserConfiguration configuration, NativeFileChooserCallback callback) {
        getFileChooser().chooseFile(configuration, callback);
    }

    public NativeFileChooser getFileChooser() {
        return fileChooser;
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        this.update(delta);
        ScreenUtils.clear(0.5f, 0.5f, 0.5f, 1f, true);

        /* --- CANVAS PART --- */
        this.editorCanvas.render(delta);

        /* --- UI PART -- */
        this.editorUi.draw();


    }

    private void update(float dt) {

        /* --- UI PART -- */
        this.editorUi.act(dt);
        /* --- CANVAS PART --- */
        this.editorCanvas.update(dt);
    }

    @Override
    public void resize(int width, int height) {
        editorUi.resize(width, height);
        editorCanvas.resize(width, height);
    }

    @Override
    public void dispose() {
        this.editorUi.dispose();
        this.editorCanvas.dispose();
        this.shapeRenderer.dispose();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        ScreenUtils.clear(0f, 0f, 0f, 1f, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    public void moveCanvas(float x, float y, float z) {
        this.editorCanvas.move(x, y, z);
    }

    public Vector2 getCanvasPosition() {
        return this.editorCanvas.getCameraPosition();
    }

    public MazeRunnerGame getGame() {
        return game;
    }

    public Vector2 getCanvasMousePosition() {
        return this.editorCanvas.getMousePosition();
    }

    public EditorCanvas getEditorCanvas() {
        return editorCanvas;
    }

    public void handleLostUiFocus() {
        this.editorUi.hideAllPopups();
    }

    public final void exit() {
        Gdx.app.postRunnable(game::quitEditor);

    }

    /**
     * Executes the test for a given map.
     *
     * @param map The map to be tested.
     */
    public final void testMap(Map map) {

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Map test"); // Set the window title

        // Get the display mode of the current monitor
        Graphics.DisplayMode displayMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
        // Set the window size to 80% of the screen width and height
        config.setWindowedMode(Math.round(0.8f * displayMode.width), Math.round(0.8f * displayMode.height));
        config.useVsync(true); // Enable vertical sync
        config.setForegroundFPS(60); // Set the foreground frames per second


        try {
            MapGenerator.loadMapIntoGame(map);
            new MazeRunnerGame(fileChooser) {
                public final Lwjgl3Window window = ((Lwjgl3Application) Gdx.app).newWindow(this, config);

                @Override
                public void create() {
                    super.create();

                    var s = new GameScreen(this, false);

                    this.setScreen(s);
                }

                @Override
                public void goToMenu() {
                    window.closeWindow();
                }

                @Override
                public void goToPause() {
                    window.closeWindow();
                }
            };

        } catch (RuntimeException e) {
            try {
                MapGenerator.validateExport(map);
            } catch (InvalidMapFile ex) {
                editorUi.showMessage("Error", ex.getMessage());
                Gdx.app.error("Test map", "Error testing map", e);

            }
        }
    }
}
