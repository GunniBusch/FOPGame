package de.tum.cit.ase.editor.tools;

import com.badlogic.gdx.math.GridPoint2;
import de.tum.cit.ase.editor.data.EditorConfig;
import de.tum.cit.ase.editor.utlis.TileTypes;
import de.tum.cit.ase.editor.utlis.exceptions.InvalidGridCellException;

import java.util.Stack;

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

    @Override
    protected void markTile(GridPoint2 gridPoint) throws InvalidGridCellException {
        // Fill the area (all the cells) that are in an area witch encloses gridPoint
        canvas.startNewGridEpoch();
        fillArea(canvas.virtualGrid, gridPoint);


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
