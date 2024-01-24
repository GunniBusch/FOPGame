package de.tum.cit.ase.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import de.tum.cit.ase.editor.data.EditorConfig;
import de.tum.cit.ase.editor.utlis.exceptions.InvalidGridCellException;

public final class Pen extends EditorTool {
    private GridPoint2 lastPosition;
    private GridPoint2 lastGridPosition;

    @Override
    public void draw(ShapeRenderer shapeRenderer) {


    }

    @Override
    public void validate() {
        this.lastGridPosition = null;
        this.lastPosition = null;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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

    private void markTile(GridPoint2 gridPoint) throws InvalidGridCellException {
        try {
            canvas.virtualGrid[gridPoint.y][gridPoint.x] = EditorConfig.selectedTile;
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidGridCellException(e.getCause());
        }
    }

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
        super.reset();
        this.lastPosition = null;
        this.lastGridPosition = null;
    }
}
