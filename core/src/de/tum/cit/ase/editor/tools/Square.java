package de.tum.cit.ase.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import de.tum.cit.ase.editor.data.EditorConfig;
import de.tum.cit.ase.editor.utlis.exceptions.InvalidGridCellException;

// ToDo decide if hollow square.

/**
 * Represents a square tool for drawing squares on a canvas.
 */
public final class Square extends EditorTool {

    private GridPoint2 corner1, corner2;
    private boolean pressed = false;

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        if (pressed && corner1 != null && corner2 != null) {
            shapeRenderer.setColor(0f, 0f, 0f, .5f);
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            var c1 = this.projectGridPointToWorld(corner1, true);
            var c2 = this.projectGridPointToWorld(corner2, true);
            var width = Math.abs(c2.x - c1.x);
            var height = Math.abs(c2.y - c1.y);
            shapeRenderer.rect(Math.min(c1.x, c2.x), Math.min(c1.y, c2.y), width + canvas.getTileSize(), height + canvas.getTileSize());
        }

    }

    @Override
    public void validate() {

        super.validate();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        lastPosition = canvas.calculateGridPoint(screenX, screenY, true);
        var current = canvas.calculateGridPoint(screenX, screenY, false);
        if (current != null) {
            lastGridPosition = current;
            corner1 = current;
            corner2 = current;
            pressed = true;

            return true;
        }
        return false;
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
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        pressed = false;
        if (corner1 != null) {


            Gdx.app.debug("Square", "Corner 1: " + corner1);
            Gdx.app.debug("Square", "Corner 2: " + corner2);


            markSquare(corner1, corner2);
        }
        this.lastGridPosition = null;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        lastPosition = canvas.calculateGridPoint(screenX, screenY, true);
        if (corner1 != null) {

            corner2 = lastPosition;
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        super.reset();
    }

    /**
     * Marks a square on the canvas starting from the given start point and ending at the given end point.
     *
     * @param start the start point of the square
     * @param end   the end point of the square
     */
    private void markSquare(GridPoint2 start, GridPoint2 end) {

        var width = Math.abs(end.x - start.x);
        var height = Math.abs(end.y - start.y);
        var x = Math.min(start.x, end.x);
        var y = Math.min(start.y, end.y);
        canvas.startNewGridEpoch();
        for (int i = y; i <= height + y; i++) {
            for (int j = x; j <= width + x; j++) {

                markTile(new GridPoint2(j, i));
            }
        }
        canvas.endNewGridEpoch();


    }
}
