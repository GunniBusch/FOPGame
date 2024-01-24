package de.tum.cit.ase.editor.drawing;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.ase.editor.data.EditorConfig;
import de.tum.cit.ase.editor.screens.EditorCanvas;
import de.tum.cit.ase.editor.utlis.TileTypes;

public class Canvas {
    private final EditorCanvas editorCanvas;
    public TileTypes[][] virtualGrid;

    public Canvas(EditorCanvas editorCanvas) {
        this.editorCanvas = editorCanvas;
    }

    public void update(float dt) {

    }

    public void draw(ShapeRenderer shapeRenderer) {

        renderGrid(shapeRenderer);
        EditorConfig.selectedTool.draw(shapeRenderer);
    }

    public GridPoint2 calculateGridPoint(float x, float y, boolean clampToGrid) {
        return editorCanvas.getMouseGridPosition(editorCanvas.getViewport().unproject(new Vector2(x, y)), clampToGrid);
    }

    private void renderGrid(ShapeRenderer shapeRenderer) {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {

                if (virtualGrid[y][x] != null) {
                    shapeRenderer.setColor(virtualGrid[y][x].canvasColor);
                    shapeRenderer.rect(x * getTileSize() + 1 + editorCanvas.getGrid().getX(), y * getTileSize() + 1 + editorCanvas.getGrid().getY(), getTileSize() - 2, getTileSize() - 2);
                }

            }
        }

    }

    public void createNewGrid(int width, int height) {
        this.virtualGrid = new TileTypes[height][width];
        EditorConfig.selectedTool.validate();
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