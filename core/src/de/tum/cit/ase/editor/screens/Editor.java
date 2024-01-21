package de.tum.cit.ase.editor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.ase.editor.input.EditorGestureProcessor;
import de.tum.cit.ase.editor.input.EditorInputProcessor;
import de.tum.cit.ase.maze.MazeRunnerGame;
import de.tum.cit.ase.maze.utils.CONSTANTS;

public class Editor implements Screen {
    private final MazeRunnerGame game;
    private final EditorUi editorUi;
    private final EditorCanvas editorCanvas;
    private final InputMultiplexer inputMultiplexer;

    public Editor(MazeRunnerGame game) {
        this.game = game;
        this.editorUi = new EditorUi(this);
        this.editorCanvas = new EditorCanvas(this);
        var editorInputProcessor = new EditorInputProcessor(this);
        this.inputMultiplexer = new InputMultiplexer(editorUi, new GestureDetector(new EditorGestureProcessor(this, editorInputProcessor)), editorInputProcessor);
        this.editorUi.setDebugAll(CONSTANTS.DEBUG);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        this.update(delta);
        ScreenUtils.clear(0, 0, 0, 1, true);

        /* --- UI PART -- */
        this.editorUi.draw();

        /* --- CANVAS PART --- */
        this.editorCanvas.render(delta);


    }

    private void update(float dt) {
        this.editorUi.act(dt);
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
}
