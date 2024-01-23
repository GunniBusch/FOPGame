package de.tum.cit.ase.editor.tools;

import com.badlogic.gdx.utils.Pool;

public abstract sealed class EditorTool implements Tool, Pool.Poolable permits Eraser, Pen {
    @Override
    public void reset() {

    }
}
