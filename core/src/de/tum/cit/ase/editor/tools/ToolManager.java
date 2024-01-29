package de.tum.cit.ase.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pools;
import de.tum.cit.ase.editor.drawing.Canvas;
import de.tum.cit.ase.editor.utlis.TileTypes;

/**
 * The ToolManager class provides utility methods for managing tools.
 */
public class ToolManager {

    /**
     * Retrieves an instance of the specified tool class using a generic type parameter.
     *
     * @param <T>       the type of the tool class
     * @param toolClass the class of the tool to retrieve an instance of
     * @param grid      the 2D array representing the grid
     * @param canvas    the canvas to associate the tool instance with
     * @return an instance of the specified tool class
     */
    public static <T extends EditorTool> T getTool(Class<T> toolClass, TileTypes[][] grid, Canvas canvas) {
        return toolClass.cast(Pools.obtain(toolClass).getInstance(canvas));
    }

    /**
     * Frees a tool by returning it to the object pool.
     *
     * @param toolObject the tool object to free
     * @param <T>        the type of the tool object
     */
    public static <T extends Tool> void freeTool(T toolObject) {
        Gdx.app.debug("Free", toolObject.toString() + " ; " + Pools.get(toolObject.getClass()).getFree());
        Pools.free(toolObject);
        Gdx.app.debug("Free2", toolObject + " ; " + Pools.get(toolObject.getClass()).getFree());

    }
}
