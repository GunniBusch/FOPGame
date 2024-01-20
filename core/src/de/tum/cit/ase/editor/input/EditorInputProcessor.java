package de.tum.cit.ase.editor.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.IntSet;
import de.tum.cit.ase.editor.screens.Editor;

public class EditorInputProcessor extends InputAdapter {
    private final Editor editor;
    private final IntSet pressedKeys = new IntSet(20);
    private boolean isToched;

    public EditorInputProcessor(Editor editor) {
        super();
        this.editor = editor;
    }

    @Override
    public boolean keyDown(int keycode) {
        this.pressedKeys.add(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        this.pressedKeys.remove(keycode);
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
        Gdx.app.debug("Scrolled", String.format("amountX: %s, amountY: %s", amountX, amountY));

        return super.scrolled(amountX, amountY);
    }
}
