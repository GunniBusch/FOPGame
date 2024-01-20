package de.tum.cit.ase.editor.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class EditorUi extends Stage {
    private final Editor editor;

    public EditorUi(Editor editor) {
        super(new ScreenViewport());
        this.editor = editor;
    }

    @Override
    public void draw() {
        this.getViewport().apply(true);
        super.draw();
    }

    public void resize(int width, int height) {
        this.getViewport().update(width, height, true);
    }
}
