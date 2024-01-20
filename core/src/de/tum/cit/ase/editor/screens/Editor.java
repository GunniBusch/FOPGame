package de.tum.cit.ase.editor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.editor.input.EditorGestureProcessor;
import de.tum.cit.ase.editor.input.EditorInputProcessor;
import de.tum.cit.ase.maze.MazeRunnerGame;
import de.tum.cit.ase.maze.utils.CONSTANTS;

public class Editor implements Screen {
    private final MazeRunnerGame game;
    private final EditorUi editorUi;
    private final ScreenViewport viewport;
    private final InputMultiplexer inputMultiplexer;

    public Editor(MazeRunnerGame game) {
        this.game = game;
        this.viewport = new ScreenViewport();
        this.editorUi = new EditorUi(this);
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


    }

    private void update(float dt) {
        this.editorUi.act(dt);
        this.updateCamera();
    }

    private void updateCamera() {
        this.viewport.apply(false);
    }

    @Override
    public void resize(int width, int height) {
        editorUi.resize(width, height);
        this.viewport.update(width, height);
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

    @Override
    public void dispose() {
        this.editorUi.dispose();
    }
}
