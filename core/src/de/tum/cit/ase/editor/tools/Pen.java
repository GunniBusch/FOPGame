package de.tum.cit.ase.editor.tools;

import com.badlogic.gdx.math.GridPoint2;
import de.tum.cit.ase.editor.data.EditorConfig;
import de.tum.cit.ase.editor.utlis.exceptions.InvalidGridCellException;

public final class Pen extends EditorTool {

    @Override
    public void validate() {
        super.validate();
    }

    @Override
    protected void markTile(GridPoint2 gridPoint) throws InvalidGridCellException {
        try {
            canvas.virtualGrid[gridPoint.y][gridPoint.x] = EditorConfig.selectedTile;
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidGridCellException(e.getCause());
        }
    }

    @Override
    public void reset() {
        super.reset();

    }
}
