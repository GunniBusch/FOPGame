package de.tum.cit.ase.editor.input;

import com.badlogic.gdx.input.GestureDetector;
import de.tum.cit.ase.editor.screens.EditorCanvas;

/**
 * The CanvasGestureListener class handles gesture events on a canvas.
 * It extends the GestureDetector.GestureAdapter class.
 *
 * @see GestureDetector.GestureAdapter
 */
public class CanvasGestureListener extends GestureDetector.GestureAdapter {
    private final EditorCanvas editor;
    private final CanvasInputProcessor inputProcessor;

    /**
     * NOT IN USE!!
     * The CanvasGestureListener class handles gesture events on a canvas.
     * It extends the GestureDetector.GestureAdapter class.
     *
     * @see GestureDetector.GestureAdapter
     */
    public CanvasGestureListener(EditorCanvas editor, CanvasInputProcessor inputProcessor) {
        this.editor = editor;
        this.inputProcessor = inputProcessor;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        //Gdx.app.debug("Fling", String.format("velocityX: %s, velocityY: %s, button: %s", velocityX, velocityY, button));
        return super.fling(velocityX, velocityY, button);
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        //Gdx.app.debug("Pan", String.format("x: %f, y: %f, deltaX: %f, deltaY: %f", x, y, deltaX, deltaY));
        //this.editor.processMouseInput(x, y, Input.Buttons.LEFT);
        return super.pan(x, y, deltaX, deltaY);
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        // Gdx.app.debug("PanStop", String.format("x: %f, y: %f, pointer: %d, button: %d", x, y, pointer, button));
        return super.panStop(x, y, pointer, button);
    }
}