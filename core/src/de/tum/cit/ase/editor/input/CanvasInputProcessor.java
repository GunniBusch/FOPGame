package de.tum.cit.ase.editor.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import de.tum.cit.ase.editor.data.EditorConfig;
import de.tum.cit.ase.editor.screens.EditorCanvas;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static de.tum.cit.ase.maze.utils.CONSTANTS.DEBUG;

/**
 * This class is responsible for processing input events on the canvas.
 * It extends the {@link InputAdapter} class and implements the {@link ShortcutAdapter} interface.
 */
public class CanvasInputProcessor extends InputAdapter implements ShortcutAdapter {
    private final EditorCanvas editorCanvas;
    private int numEvents = 0;

    /**
     * Constructs a CanvasInputProcessor object.
     *
     * @param editorCanvas the canvas to associate with the input processor
     */
    public CanvasInputProcessor(EditorCanvas editorCanvas) {
        super();
        this.editorCanvas = editorCanvas;
        System.out.println(Shortcuts.UI.ZOOM.toString());
    }

    // Todo Undo redo
    @Override
    public boolean keyDown(int keycode) {
        //Gdx.app.debug("keyDown", String.format("keycode: %s, keycode_str: %s", keycode, Input.Keys.toString(keycode)));
        this.addKey(keycode);

        if (keycode == Input.Keys.SPACE) {
            editorCanvas.resizeCanvas(640, 640);
        }
        if (keycode == Input.Keys.N) {
            editorCanvas.resizeCanvas(16, 16);
        }
        if (isShortcut(Shortcuts.UI.REDO)) {
            editorCanvas.getCanvas().redo();
            return true;
        } else if (isShortcut(Shortcuts.UI.UNDO)) {
            editorCanvas.getCanvas().undo();
            return true;
        }


        this.editorCanvas.getEditor().handleLostUiFocus();
        try {
            return (boolean) this.relocateToTool(ToolInputAdapter.class.getDeclaredMethod("keyDown", int.class), keycode);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean keyTyped(char character) {
        return super.keyTyped(character);
    }

    @Override
    public boolean keyUp(int keycode) {

        this.removeKey(keycode);
        if (DEBUG && keycode == Input.Keys.SPACE) {
            return true;

        }
        try {
            return (boolean) this.relocateToTool(ToolInputAdapter.class.getDeclaredMethod("keyUp", int.class), keycode);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //Gdx.app.debug("touchDown", String.format("screenX: %s, screenY: %s, pointer: %s", screenX, screenY, pointer));

        var current = calculateGridPoint(screenX, screenY, false);
        if (current != null) {

        }

        this.editorCanvas.getEditor().handleLostUiFocus();

        try {
            return (boolean) this.relocateToTool(ToolInputAdapter.class.getDeclaredMethod("touchDown", int.class, int.class, int.class, int.class), screenX, screenY, pointer, button);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Gdx.app.debug("touchUp", String.format("screenX: %s, screenY: %s, pointer: %s", screenX, screenY, pointer));
        Gdx.app.debug("TouchesReg", numEvents + "");

        this.numEvents = 0;
        try {
            return (boolean) this.relocateToTool(ToolInputAdapter.class.getDeclaredMethod("touchUp", int.class, int.class, int.class, int.class), screenX, screenY, pointer, button);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized boolean touchDragged(int screenX, int screenY, int pointer) {
        //Gdx.app.debug("Dragged", String.format("screenX: %s, screenY: %s, pointer: %s", screenX, screenY, pointer));


        this.numEvents++;
        var currPos = calculateGridPoint(screenX, screenY, false);

        var savePos = true;
        try {
            return (boolean) this.relocateToTool(ToolInputAdapter.class.getDeclaredMethod("touchDragged", int.class, int.class, int.class), screenX, screenY, pointer);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        //Gdx.app.debug("Moved", String.format("screenX: %s, screenY: %s", screenX, screenY));

        return super.mouseMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        amountX *= 1.5f;
        amountY *= 1.5f * -1;
        if (this.isShortcut(Shortcuts.UI.ZOOM.keys())) {
            Gdx.app.debug("Scrolled", "Zoomed");

            this.editorCanvas.move(0, 0, amountY);

        } else {
            this.editorCanvas.move(amountX, amountY, 0);

        }
        return true;
    }


    private GridPoint2 calculateGridPoint(float x, float y, boolean clampToGrid) {
        return editorCanvas.getMouseGridPosition(editorCanvas.getViewport().unproject(new Vector2(x, y)), clampToGrid);
    }

    private Object relocateToTool(Method method, Object... args) {
        try {
            return method.invoke(EditorConfig.selectedTool, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new GdxRuntimeException(e);
        }
    }

}
