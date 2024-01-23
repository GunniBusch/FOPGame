package de.tum.cit.ase.editor.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Bresenham2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.ase.editor.screens.EditorCanvas;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static de.tum.cit.ase.maze.utils.CONSTANTS.DEBUG;

public class CanvasInputProcessor extends InputAdapter {
    private final EditorCanvas editorCanvas;
    private final Set<Integer> pressedKeys = new HashSet<>(10);
    private final Bresenham2 bresenham2 = new Bresenham2();
    private int activeButton = -1;
    private int numEvents = 0;
    private GridPoint2 lastDragEvent = new GridPoint2();

    public CanvasInputProcessor(EditorCanvas editorCanvas) {
        super();
        this.editorCanvas = editorCanvas;
    }

    // Todo Shortcuts
    @Override
    public boolean keyDown(int keycode) {
        //Gdx.app.debug("keyDown", String.format("keycode: %s, keycode_str: %s", keycode, Input.Keys.toString(keycode)));


        if (keycode == Input.Keys.SPACE) {
            editorCanvas.setSize(64, 64);
        }
        this.pressedKeys.add(keycode);
        this.editorCanvas.getEditor().handleLostUiFocus();
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {

        this.pressedKeys.remove(keycode);
        if (DEBUG && keycode == Input.Keys.SPACE) {
            editorCanvas.setSize(16, 16);
            return true;

        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //Gdx.app.debug("touchDown", String.format("screenX: %s, screenY: %s, pointer: %s", screenX, screenY, pointer));

        var current = calculateGridPoint(screenX, screenY, false);
        if (current != null) {
            if (lastDragEvent == null) {
                lastDragEvent = current;
            }
            if (Math.abs(current.dst(lastDragEvent)) >= 1) {
                lastDragEvent.set(current);
            }
        }

        this.activeButton = button;
        this.editorCanvas.getEditor().handleLostUiFocus();

        return editorCanvas.processMouseInput(screenX, screenY, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Gdx.app.debug("touchUp", String.format("screenX: %s, screenY: %s, pointer: %s", screenX, screenY, pointer));
        Gdx.app.debug("TouchesReg", numEvents + "");
        editorCanvas.registerEndOfTouch();
        this.lastDragEvent = null;

        this.numEvents = 0;
        activeButton = -1;
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public synchronized boolean touchDragged(int screenX, int screenY, int pointer) {
        //Gdx.app.debug("Dragged", String.format("screenX: %s, screenY: %s, pointer: %s", screenX, screenY, pointer));


        this.numEvents++;
        var currPos = calculateGridPoint(screenX, screenY, false);

        var savePos = true;

        if (currPos == null && lastDragEvent != null) {
            currPos = calculateGridPoint(screenX, screenY, true);
            savePos = false;
        }
        if (currPos != null && lastDragEvent != null) {

            if (Math.abs(currPos.dst(lastDragEvent)) >= 1) {
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
            }
            return true; //editorCanvas.processMouseInput(screenX, screenY, activeButton);

        } else {
            if (lastDragEvent == null) {
                lastDragEvent = currPos;
            }
            return false;
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
        if (isShortcut(KeyCombination.ZOOM)) {
            Gdx.app.debug("Scrolled", "Zoomed");

            this.editorCanvas.move(0, 0, amountY);

        } else {
            this.editorCanvas.move(amountX, amountY, 0);

        }
        return true;
    }

    private boolean isShortcut(KeyCombination keyCombination) {
        return this.pressedKeys.equals(keyCombination.requiredKeys);
    }

    private GridPoint2 calculateGridPoint(float x, float y, boolean clampToGrid) {
        return editorCanvas.getMouseGridPosition(editorCanvas.getViewport().unproject(new Vector2(x, y)), clampToGrid);
    }

    protected enum KeyCombination {
        ZOOM(Input.Keys.SHIFT_LEFT);
        public final Set<Integer> requiredKeys;

        KeyCombination(Integer... keys) {
            requiredKeys = new HashSet<>(keys.length);
            Collections.addAll(requiredKeys, keys);
        }
    }
}
