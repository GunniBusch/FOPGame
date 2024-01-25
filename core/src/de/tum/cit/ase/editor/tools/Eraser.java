package de.tum.cit.ase.editor.tools;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import de.tum.cit.ase.editor.utlis.TileTypes;
import de.tum.cit.ase.editor.utlis.exceptions.InvalidGridCellException;

public final class Eraser extends EditorTool {

    public void applyTool(TileTypes[][] grid, int x, int y) {

    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        super.draw(shapeRenderer);
    }

    @Override
    public void validate() {
        super.validate();
    }

    @Override
    protected void markTile(GridPoint2 gridPoint) throws InvalidGridCellException {
        try {
            canvas.virtualGrid[gridPoint.y][gridPoint.x] = null;
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidGridCellException(e.getCause());
        }
    }
}
