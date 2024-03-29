package de.tum.cit.ase.editor.tools;

import com.badlogic.gdx.math.GridPoint2;
import de.tum.cit.ase.editor.data.EditorConfig;
import de.tum.cit.ase.editor.utlis.exceptions.InvalidGridCellException;

/**
 * The Pen class represents a pen tool used for drawing on a canvas. It extends the EditorTool class.
 */
public final class Pen extends EditorTool {

    @Override
    public void validate() {
        super.validate();
    }

    @Override
    protected synchronized void markTile(GridPoint2 gridPoint) throws InvalidGridCellException {
        canvas.startNewGridEpoch();
        try {
            canvas.virtualGrid[gridPoint.y][gridPoint.x] = EditorConfig.selectedTile;
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidGridCellException(e.getCause());
        } finally {
            canvas.endNewGridEpoch();

        }
    }

    @Override
    public void reset() {
        super.reset();

    }
}
