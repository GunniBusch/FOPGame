package de.tum.cit.ase.editor.tools;

import de.tum.cit.ase.editor.utlis.TileTypes;
import de.tum.cit.ase.editor.utlis.exceptions.InvalidGridCellException;

public final class Eraser extends EditorTool {

    @Override
    public void applyTool(TileTypes[][] grid, int x, int y) {
        try {
            grid[y][x] = null;
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidGridCellException(e.getCause());
        } finally {
            ToolManager.freeTool(this);
        }
    }
}
