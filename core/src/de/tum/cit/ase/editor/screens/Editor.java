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
import de.tum.cit.ase.editor.utlis.TileTypes;
import de.tum.cit.ase.maze.MazeRunnerGame;
import de.tum.cit.ase.maze.utils.CONSTANTS;

public class Editor extends InputAdapter implements Screen {
    private final MazeRunnerGame game;
    private final EditorUi editorUi;
    private final EditorCanvas editorCanvas;
    private final InputMultiplexer inputMultiplexer;
    public ShapeRenderer shapeRenderer;
    private TileTypes activeTool;

    public Editor(MazeRunnerGame game) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();
        this.editorUi = new EditorUi(this);
        this.editorCanvas = new EditorCanvas(this);
        var canvasInputProcessor = new CanvasInputProcessor(editorCanvas);
        this.inputMultiplexer = new InputMultiplexer(editorUi, new GestureDetector(new CanvasGestureListener(editorCanvas, canvasInputProcessor)), canvasInputProcessor, this);
        this.editorUi.setDebugAll(CONSTANTS.DEBUG);


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

    public void reportActiveTool(TileTypes activeTool) {
        this.activeTool = activeTool;
    }

    public TileTypes getActiveTool() {
        return activeTool;
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
