package de.tum.cit.ase.editor.tools;

import com.badlogic.gdx.math.GridPoint2;
import de.tum.cit.ase.editor.data.EditorConfig;
import de.tum.cit.ase.editor.utlis.TileTypes;
import de.tum.cit.ase.editor.utlis.exceptions.InvalidGridCellException;

import java.util.Stack;

/**
 * The Bucket class represents a tool for filling areas with a selected tile in an editor application.
 * It extends the EditorTool class and provides methods for validating, handling touch events, marking tiles, and resetting the tool.
 */
public final class Bucket extends EditorTool {

    @Override
    public void validate() {
        super.validate();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        var s = canvas.calculateGridPoint(screenX, screenY, false);
        if (s != null) {
            markTile(s);
        }
        return true;
    }

    /**
     * Fills the area with the selected tile starting from the given point.
     *
     * @param area        the 2D array representing the area
     * @param pointInArea the starting point in the area
     */
    private void fillArea(TileTypes[][] area, GridPoint2 pointInArea) {


        Stack<GridPoint2> stack = new Stack<>();

// Start with the initial point
        stack.push(pointInArea);

        while (!stack.isEmpty()) {
            GridPoint2 point = stack.pop();

            if (!isPointOutOfArea(area, point) && point.x >= 0 && point.y >= 0) {
                area[point.y][point.x] = EditorConfig.selectedTile;

                // Push neighboring points to stack
                stack.push(new GridPoint2(point.x + 1, point.y));
                stack.push(new GridPoint2(point.x - 1, point.y));
                stack.push(new GridPoint2(point.x, point.y + 1));
                stack.push(new GridPoint2(point.x, point.y - 1));
            }
        }


    }

    /**
     * Checks if a given point is outside the area.
     *
     * @param area        the 2D array representing the area
     * @param pointInArea the point to check
     * @return true if the point is outside the area, false otherwise
     */
    private boolean isPointOutOfArea(TileTypes[][] area, GridPoint2 pointInArea) {

//        if (pointInArea.y >= area.length || pointInArea.y < 0 || pointInArea.x >= area[0].length || pointInArea.x < 0)
//            return true;
//        else
//            return area[pointInArea.y][pointInArea.x] == EditorConfig.selectedTile && area[pointInArea.y][pointInArea.x] != null;

        int x = pointInArea.x;
        int y = pointInArea.y;

        // Check if the point lies outside the bounds of the area
        if (x < 0 || y < 0 || y >= area.length || x >= area[0].length) {
            return true;
        }

        TileTypes tileAtPoint = area[y][x];

        // Check if the tile at the point is the same as the selected tile or if the thread should be reset
        return tileAtPoint != null;
    }

    /**
     * Marks the tile located at the given grid point.
     *
     * @param gridPoint the grid point representing the tile to be marked
     * @throws InvalidGridCellException if an invalid grid cell is accessed
     */
    @Override
    protected synchronized void markTile(GridPoint2 gridPoint) throws InvalidGridCellException {
        // Fill the area (all the cells) that are in an area witch encloses gridPoint
        canvas.startNewGridEpoch();
        fillArea(canvas.virtualGrid, gridPoint);
        canvas.endNewGridEpoch();


    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public void reset() {
        super.reset();
    }
}
