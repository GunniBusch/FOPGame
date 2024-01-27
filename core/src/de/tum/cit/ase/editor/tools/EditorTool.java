package de.tum.cit.ase.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bresenham2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import de.tum.cit.ase.editor.drawing.Canvas;
import de.tum.cit.ase.editor.input.Shortcuts;
import de.tum.cit.ase.editor.input.ToolInputAdapter;
import de.tum.cit.ase.editor.utlis.TileTypes;
import de.tum.cit.ase.editor.utlis.exceptions.InvalidGridCellException;

public abstract sealed class EditorTool extends ToolInputAdapter implements Tool, Pool.Poolable permits Eraser, Pen, Square {
    protected Bresenham2 bresenham2 = new Bresenham2();
    protected Canvas canvas;
    protected GridPoint2 lastPosition;
    protected GridPoint2 lastGridPosition;

    public EditorTool() {
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {

        if (isStraightLine()) {
            var start = new Vector2();
            var end = new Vector2();
            shapeRenderer.setColor(0, 0, 0, 0.7f);
            getStraightLineCoordinates(lastPosition, start, end);
            shapeRenderer.rectLine(start, end, 10);

        }

    }

    protected boolean isStraightLine() {
        return this.isShortcut(Shortcuts.EDITOR.STRAIGHT_LINE.keys());
    }

    protected void getStraightLineCoordinates(GridPoint2 startGrid, Vector2 start, Vector2 end) {
        start.set(this.projectGridPointToWorld(startGrid));
        end.set(this.projectGridPointToWorld(this.canvas.getMouseGridPosition(true)));

    }

    public TileTypes[][] getGrid() {
        return canvas.virtualGrid;
    }

    @Override
    public Tool getInstance(Canvas canvas) {
        this.canvas = canvas;
        return this;
    }

    protected Array<GridPoint2> getLine(GridPoint2 start, GridPoint2 end) {
        return bresenham2.line(start, end);
    }

    public Vector2 projectGridPointToWorld(GridPoint2 gridPoint2) {

        return projectGridPointToWorld(gridPoint2, false);
    }

    protected Array<GridPoint2> getRectAngle(GridPoint2 start, GridPoint2 end) {
        return null;
    }

    public Vector2 projectGridPointToWorld(GridPoint2 gridPoint2, boolean edge) {
        var gridStart = canvas.getGridStartPoint();
        return new Vector2((gridPoint2.x * canvas.getTileSize()) + (edge ? 0 : (canvas.getTileSize() / 2)) + gridStart.x, (gridPoint2.y * canvas.getTileSize()) + (edge ? 0 : (canvas.getTileSize() / 2)) + gridStart.y);
    }

    @Override
    public void validate() {
        this.lastGridPosition = null;
        this.lastPosition = canvas.convertWorldPositionToGrid(new Vector2(), true);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (isStraightLine()) {
            var start = new Vector2();
            var end = new Vector2();
            this.getStraightLineCoordinates(lastPosition, start, end);
            var l = bresenham2.line(canvas.convertWorldPositionToGrid(start, true), canvas.convertWorldPositionToGrid(end, true));
            for (GridPoint2 gridPoint2 : l) {
                this.markTile(gridPoint2);
            }
        }
        lastPosition = canvas.calculateGridPoint(screenX, screenY, true);
        var current = canvas.calculateGridPoint(screenX, screenY, false);
        if (current != null) {
            lastGridPosition = current;
            try {
                this.markTile(current);
            } catch (InvalidGridCellException e) {
                Gdx.app.error("Pen", "Could not draw", e);
            }
            return true;
        }
        return false;

    }

    protected abstract void markTile(GridPoint2 gridPoint) throws InvalidGridCellException;

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        lastGridPosition = null;
        lastPosition = canvas.calculateGridPoint(screenX, screenY, true);


        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        var currPos = canvas.calculateGridPoint(screenX, screenY, false);
        var savePos = true;

        //Gdx.app.debug("Pos", currPos + " : " + lastGridPosition);
        if (currPos == null && lastGridPosition == null) {
            lastPosition = canvas.calculateGridPoint(screenX, screenY, true);
            return false;
        } else if (currPos == null) {
            currPos = canvas.calculateGridPoint(screenX, screenY, true);
            savePos = false;

        } else if (lastGridPosition == null) {
            lastGridPosition = lastPosition;
        }
        if (currPos != null && lastGridPosition != null) {
            var l = this.getLine(lastGridPosition, currPos);
            for (GridPoint2 gridPoint2 : l) {
                this.markTile(gridPoint2);
            }

            if (savePos) {
                lastGridPosition.set(currPos);
            } else {
                lastGridPosition = null;
            }
            lastPosition = canvas.calculateGridPoint(screenX, screenY, true);

            return true; //editorCanvas.processMouseInput(screenX, screenY, activeButton);

        } else {
            if (savePos) {
                lastGridPosition = currPos;
            } else {
                lastGridPosition = null;
            }
            lastPosition = canvas.calculateGridPoint(screenX, screenY, true);

            return false;
        }

    }

    @Override
    public void reset() {
        this.lastPosition = canvas.convertWorldPositionToGrid(new Vector2(), true);
        canvas = null;
        this.lastGridPosition = null;
    }
}
