package de.tum.cit.ase.editor.tools;

import de.tum.cit.ase.editor.utlis.TileTypes;
import de.tum.cit.ase.editor.utlis.exceptions.InvalidGridCellException;

public sealed interface Tool permits EditorTool {

    void applyTool(TileTypes[][] grid, int x, int y) throws InvalidGridCellException;

}
