package de.tum.cit.ase.editor.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import de.tum.cit.ase.editor.screens.Editor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class EditorInputProcessor extends InputAdapter {
    private final Editor editor;
    private final Set<Integer> pressedKeys = new HashSet<>(10);
    private boolean isToched;

    public EditorInputProcessor(Editor editor) {
        super();
        this.editor = editor;
    }

    // Todo Shortcuts
    @Override
    public boolean keyDown(int keycode) {
        Gdx.app.debug("keyDown", String.format("keycode: %s, keycode_str: %s", keycode, Input.Keys.toString(keycode)));


        if (keycode == Input.Keys.SPACE) {
            editor.getEditorCanvas().setSize(64, 64);
        }
        this.pressedKeys.add(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        this.pressedKeys.remove(keycode);
        if (keycode == Input.Keys.SPACE) {
            editor.getEditorCanvas().setSize(16, 16);
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Gdx.app.debug("touchDown", String.format("screenX: %s, screenY: %s, pointer: %s", screenX, screenY, pointer));
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Gdx.app.debug("touchUp", String.format("screenX: %s, screenY: %s, pointer: %s", screenX, screenY, pointer));

        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        //Gdx.app.debug("Dragged", String.format("screenX: %s, screenY: %s, pointer: %s", screenX, screenY, pointer));

        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        amountX *= 1.5f;
        amountY *= 1.5f * -1;
        //Gdx.app.debug("Scrolled", String.format("amountX: %s, amountY: %s", amountX, amountY));
        if (isShortcut(KeyCombination.ZOOM)) {
            Gdx.app.debug("Scrolled", "Zoomed");

            this.editor.moveCanvas(0, 0, amountY);

            return true;

        } else {
            //Gdx.app.debug("Scrolled", "Scrolled");
            this.editor.moveCanvas(amountX, amountY, 0);

            return true;
        }
    }

    private boolean isShortcut(KeyCombination keyCombination) {
        return this.pressedKeys.equals(keyCombination.requiredKeys);
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
