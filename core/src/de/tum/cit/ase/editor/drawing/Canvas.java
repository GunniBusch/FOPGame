package de.tum.cit.ase.editor.drawing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import de.tum.cit.ase.editor.data.EditorConfig;
import de.tum.cit.ase.editor.screens.EditorCanvas;
import de.tum.cit.ase.editor.tools.Tool;
import de.tum.cit.ase.editor.tools.ToolManager;
import de.tum.cit.ase.editor.utlis.TileTypes;
import de.tum.cit.ase.editor.utlis.exceptions.MazeGameEditorException;

import java.lang.ref.SoftReference;
import java.util.Objects;

public class Canvas {
    private final EditorCanvas editorCanvas;
    public TileTypes[][] virtualGrid;

    public Canvas(EditorCanvas editorCanvas) {
        this.editorCanvas = editorCanvas;
    }

    public void update(float dt) {

    }

    public void createNewGrid(int width, int height) {
        this.virtualGrid = new TileTypes[height][width];
    }

    public boolean makeInput(int x, int y) {
        manipulateGrid(new GridPoint2(x, y), ToolManager.getTool(EditorConfig.selectedTool));
        return true;
    }

    public void manipulateGrid(GridPoint2 position, SoftReference<? extends Tool> toolReference) {
        try {
            System.out.println(position);
            Objects.requireNonNull(toolReference.get()).applyTool(this.virtualGrid, position.x, position.y);
        } catch (NullPointerException | MazeGameEditorException e) {
            Gdx.app.error("Drawing", "Could not draw", e);
        } finally {
            toolReference.enqueue();
        }


    }

    public void setGridTile(int x, int y, TileTypes tileType) {
        virtualGrid[x][y] = tileType;
    }
}