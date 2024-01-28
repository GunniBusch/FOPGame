package de.tum.cit.ase.editor.drawing;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.ase.editor.data.EditorConfig;
import de.tum.cit.ase.editor.screens.EditorCanvas;
import de.tum.cit.ase.editor.utlis.TileTypes;

import java.util.Stack;

/**
 * Represents a canvas that allows drawing and manipulation of a virtual grid.
 */
public class Canvas {
    private final EditorCanvas editorCanvas;
    private final Stack<TileTypes[][]> pastVGrids;
    private final Stack<TileTypes[][]> futureVGrids;
    public TileTypes[][] virtualGrid;

    /**
     * Represents a canvas for an editor.
     */
    public Canvas(EditorCanvas editorCanvas) {
        this.editorCanvas = editorCanvas;
        this.pastVGrids = new Stack<>();
        this.futureVGrids = new Stack<>();
    }

    /**
     * Updates the canvas.
     *
     * @param dt the time difference between the current frame and the previous frame
     */
    public void update(float dt) {

    }

    /**
     * Changes the grid of the canvas to the given TileTypes array.
     * Manual version of {@link Canvas#startNewGridEpoch()}
     * The current virtual grid is pushed onto the pastVGrids stack, and the futureVGrids list is cleared.
     *
     * @param tileTypes the new TileTypes array representing the grid
     * @see Canvas#startNewGridEpoch()
     */
    protected void changeGrid(TileTypes[][] tileTypes) {
        pastVGrids.push(virtualGrid);
        virtualGrid = tileTypes;
        futureVGrids.clear();
    }

    public void undo() {
        if (!pastVGrids.isEmpty()) {
            futureVGrids.push(virtualGrid);
            virtualGrid = pastVGrids.pop();
        }
    }

    public void redo() {
        if (!futureVGrids.isEmpty()) {
            pastVGrids.push(virtualGrid);
            virtualGrid = futureVGrids.pop();
        }
    }

    /**
     * Creates a new grid with the specified width and height.
     *
     * @param width  the width of the grid
     * @param height the height of the grid
     */
    public void createNewGrid(int width, int height) {
        this.createNewGrid(new TileTypes[height][width]);

    }

    /**
     * Creates a new grid with the given TileTypes array.
     *
     * @param grid the TileTypes array representing the new grid
     */
    public void createNewGrid(TileTypes[][] grid) {
        this.virtualGrid = grid;
        if (EditorConfig.selectedTool != null) {
            EditorConfig.selectedTool.validate();
        }
    }

    /**
     * Draws the canvas using the specified ShapeRenderer.
     * This method renders the grid and the selected tool on the canvas.
     *
     * @param shapeRenderer the ShapeRenderer used to draw the canvas
     */
    public void draw(ShapeRenderer shapeRenderer) {

        renderGrid(shapeRenderer);
        EditorConfig.selectedTool.draw(shapeRenderer);
    }

    /**
     * Converts screen Coordinates to the Position relative to the grid
     *
     * @param clampToGrid if true value will be clamped to grid else value can be null
     * @return Grid Point
     */
    public GridPoint2 calculateGridPoint(float x, float y, boolean clampToGrid) {
        return editorCanvas.getMouseGridPosition(editorCanvas.getViewport().unproject(new Vector2(x, y)), clampToGrid);
    }

    /**
     * Converts screen Coordinates to the Position relative to the grid
     *
     * @param clampToGrid if true value will be clamped to grid else value can be null
     * @return Grid Point
     */
    public GridPoint2 calculateGridPoint(Vector2 position, boolean clampToGrid) {
        return editorCanvas.getMouseGridPosition(editorCanvas.getViewport().unproject(position), clampToGrid);
    }

    /**
     * Converts <b>WORLD</b> Coordinates to the Position relative to the grid
     *
     * @param clampToGrid if true value will be clamped to grid else value can be null
     * @return Grid Point
     */
    public GridPoint2 convertWorldPositionToGrid(Vector2 position, boolean clampToGrid) {
        return editorCanvas.getMouseGridPosition(position, clampToGrid);

    }

    /**
     * Retrieves the grid position of the mouse.
     *
     * @param alwaysGrid specifies whether the mouse position should always be restricted to the grid
     * @return the grid position of the mouse as a GridPoint2 object, or null if the mouse position is outside the grid
     */
    public GridPoint2 getMouseGridPosition(boolean alwaysGrid) {
        return this.editorCanvas.getMouseGridPosition(getUnprotectedMousePosition(), alwaysGrid);
    }

    /**
     * Retrieves the unprotected mouse position on the canvas.
     * This method returns the mouse position on the canvas without any restrictions or clamping to the grid.
     *
     * @return the mouse position as a {@link Vector2} object on the canvas
     */
    public Vector2 getUnprotectedMousePosition() {
        return this.editorCanvas.getViewport().unproject(editorCanvas.getMousePosition());
    }

    /**
     * Renders the grid on the canvas using the specified ShapeRenderer.
     *
     * @param shapeRenderer the ShapeRenderer used to draw the canvas
     */
    private void renderGrid(ShapeRenderer shapeRenderer) {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {

                if (virtualGrid[y][x] != null) {
                    shapeRenderer.setColor(virtualGrid[y][x].canvasColor);
                    if (!editorCanvas.isBigZoom) {
                        shapeRenderer.rect(x * getTileSize() + 1 + editorCanvas.getGrid().getX(), y * getTileSize() + 1 + editorCanvas.getGrid().getY(), getTileSize() - 2, getTileSize() - 2);
                    } else {
                        shapeRenderer.rect(x * getTileSize() + editorCanvas.getGrid().getX(), y * getTileSize() + editorCanvas.getGrid().getY(), getTileSize(), getTileSize());

                    }
                }

            }
        }

    }

    /**
     * Returns the starting point of the grid.
     *
     * @return the starting point of the grid as a Vector2 object
     */
    public Vector2 getGridStartPoint() {
        return new Vector2(editorCanvas.getGrid().getX(), editorCanvas.getGrid().getY());
    }

    /**
     * Starts a new grid epoch by pushing the current virtual grid onto the pastVGrids stack and clearing the futureVGrids list.
     * Automatic version
     * Alternative: {@link Canvas#changeGrid(TileTypes[][])}
     *
     * @see Canvas#changeGrid(TileTypes[][])
     */
    public void startNewGridEpoch() {
        changeGrid(this.virtualGrid);
    }

    public float getHeight() {
        return this.editorCanvas.getHeight();
    }

    public float getWidth() {
        return this.editorCanvas.getWidth();
    }

    public void setGridTile(int x, int y, TileTypes tileType) {
        virtualGrid[x][y] = tileType;
    }

    public float getTileSize() {
        return this.editorCanvas.getTileSize();
    }
}