package de.tum.cit.ase.editor.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Bresenham2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import de.tum.cit.ase.editor.data.EditorConfig;
import de.tum.cit.ase.editor.screens.EditorCanvas;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static de.tum.cit.ase.maze.utils.CONSTANTS.DEBUG;

public class CanvasInputProcessor extends InputAdapter implements ShortcutAdapter {
    private final EditorCanvas editorCanvas;
    private final Bresenham2 bresenham2 = new Bresenham2();
    private int activeButton = -1;
    private int numEvents = 0;
    private GridPoint2 lastDragEvent = null;
    private GridPoint2 lastPosition = new GridPoint2();

    public CanvasInputProcessor(EditorCanvas editorCanvas) {
        super();
        this.editorCanvas = editorCanvas;
        System.out.println(Shortcuts.UI.ZOOM.toString());
    }

    // Todo Undo redo
    @Override
    public boolean keyDown(int keycode) {
        //Gdx.app.debug("keyDown", String.format("keycode: %s, keycode_str: %s", keycode, Input.Keys.toString(keycode)));


        if (keycode == Input.Keys.SPACE) {
            editorCanvas.resizeCanvas(64, 64);
        }
        this.addKey(keycode);
        this.editorCanvas.getEditor().handleLostUiFocus();
        try {
            return (boolean) this.relocateToTool(ToolInputAdapter.class.getDeclaredMethod("keyDown", int.class), keycode);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean keyUp(int keycode) {

        this.removeKey(keycode);
        if (DEBUG && keycode == Input.Keys.SPACE) {
            editorCanvas.resizeCanvas(16, 16);
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

        lastPosition = calculateGridPoint(screenX, screenY, true);
        var current = calculateGridPoint(screenX, screenY, false);
        if (current != null) {

            lastDragEvent = current;

        }

        this.activeButton = button;
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
        editorCanvas.registerEndOfTouch();
        this.lastDragEvent = null;

        this.numEvents = 0;
        activeButton = -1;
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

/*
        Gdx.app.debug("Pos", currPos + " : " + lastDragEvent);
        if (currPos == null && lastDragEvent == null) {
            lastPosition = calculateGridPoint(screenX, screenY, true);
            return false;
        } else if (currPos == null && lastDragEvent != null) {
            currPos = calculateGridPoint(screenX, screenY, true);
            savePos = false;

        } else if (currPos != null && lastDragEvent == null) {
            lastDragEvent = lastPosition;
            savePos = true;
        }
        if (currPos != null && lastDragEvent != null) {


            var l = bresenham2.line(lastDragEvent, currPos);
            for (GridPoint2 gridPoint2 : l) {
                System.out.println(gridPoint2);
                editorCanvas.makeInput(gridPoint2.x, gridPoint2.y);
            }


            if (savePos) {
                lastDragEvent.set(currPos);
            } else {
                lastDragEvent = null;
            }
            lastPosition = calculateGridPoint(screenX, screenY, true);

            return true; //editorCanvas.processMouseInput(screenX, screenY, activeButton);

        } else {
            if (savePos) {
                lastDragEvent = currPos;
            } else {
                lastDragEvent = null;
            }
            lastPosition = calculateGridPoint(screenX, screenY, true);

            return false;
        }
*/

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
