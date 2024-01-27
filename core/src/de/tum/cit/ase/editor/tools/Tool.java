package de.tum.cit.ase.editor.tools;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.tum.cit.ase.editor.drawing.Canvas;

import javax.annotation.WillNotClose;

/**
 * An interface representing a tool in an editor.
 */
public sealed interface Tool permits EditorTool {


    /**
     * Retrieves an instance of the Tool class with the provided Canvas.
     *
     * @param canvas The Canvas object to associate with the Tool instance.
     * @return The Tool instance with the provided Canvas.
     */
    Tool getInstance(Canvas canvas);

    /**
     * Draws using the provided ShapeRenderer.
     *
     * @param shapeRenderer the ShapeRenderer object to use for drawing
     */
    void draw(@WillNotClose ShapeRenderer shapeRenderer);

    /**
     * Validates the current state of the tool.
     * This method is called internally to ensure the tool is in a valid state.
     * It performs necessary validations and updates to the tool's internal state.
     */
    void validate();

}
