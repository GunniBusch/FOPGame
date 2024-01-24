package de.tum.cit.ase.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pools;
import de.tum.cit.ase.editor.drawing.Canvas;
import de.tum.cit.ase.editor.utlis.TileTypes;

public class ToolManager {


    public static <T extends EditorTool> T getTool(Class<T> toolClass, TileTypes[][] grid, Canvas canvas) {
        return toolClass.cast(Pools.obtain(toolClass).getInstance(canvas));
    }

    public static <T extends Tool> void freeTool(T toolObject) {
        Gdx.app.debug("Free", toolObject.toString() + " ; " + Pools.get(toolObject.getClass()).getFree());
        Pools.free(toolObject);
        Gdx.app.debug("Free2", toolObject + " ; " + Pools.get(toolObject.getClass()).getFree());

    }
}
