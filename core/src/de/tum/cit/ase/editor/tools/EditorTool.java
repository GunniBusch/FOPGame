package de.tum.cit.ase.editor.tools;

import com.badlogic.gdx.math.Bresenham2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import de.tum.cit.ase.editor.drawing.Canvas;
import de.tum.cit.ase.editor.input.ToolInputAdapter;
import de.tum.cit.ase.editor.utlis.TileTypes;

public abstract sealed class EditorTool extends ToolInputAdapter implements Tool, Pool.Poolable permits Eraser, Pen {
    protected Bresenham2 bresenham2 = new Bresenham2();
    protected Canvas canvas;

    public EditorTool() {
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

    protected Array<GridPoint2> getRectAngle(GridPoint2 start, GridPoint2 end) {
        return null;
    }

    @Override
    public void reset() {
        canvas = null;
    }
}
