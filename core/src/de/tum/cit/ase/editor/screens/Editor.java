package de.tum.cit.ase.editor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.ase.editor.input.CanvasGestureListener;
import de.tum.cit.ase.editor.input.CanvasInputProcessor;
import de.tum.cit.ase.editor.utlis.FixedDesktopFileChooser;
import de.tum.cit.ase.maze.MazeRunnerGame;
import de.tum.cit.ase.maze.utils.CONSTANTS;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;
import games.spooky.gdx.nativefilechooser.NativeFileChooserIntent;

public class Editor extends InputAdapter implements Screen {
    private final MazeRunnerGame game;
    private final EditorUi editorUi;
    private final EditorCanvas editorCanvas;
    private final InputMultiplexer inputMultiplexer;
    public ShapeRenderer shapeRenderer;
    private final NativeFileChooser fileChooser;

    public Editor(MazeRunnerGame game) {
        this.game = game;
        this.fileChooser = new FixedDesktopFileChooser();
        this.shapeRenderer = new ShapeRenderer();
        this.editorCanvas = new EditorCanvas(this);
        this.editorUi = new EditorUi(this);
        var canvasInputProcessor = new CanvasInputProcessor(editorCanvas);
        this.inputMultiplexer = new InputMultiplexer(editorUi, new GestureDetector(new CanvasGestureListener(editorCanvas, canvasInputProcessor)), canvasInputProcessor, this);
        this.editorUi.setDebugAll(CONSTANTS.DEBUG);


    }

    public void chooseFile(String filter, String title, NativeFileChooserIntent dialogType, NativeFileChooserCallback callback) {


        NativeFileChooserConfiguration conf = new NativeFileChooserConfiguration();

// Starting from user's dir
        conf.directory = Gdx.files.absolute(System.getProperty("user.home"));

        conf.mimeFilter = filter;

// Add a nice title
        conf.title = title;
        conf.intent = dialogType;

        this.chooseFile(conf, callback);

    }

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
        Gdx.app.postRunnable(game::goToMenu);
    }
}