package de.tum.cit.ase.editor.tools;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.tum.cit.ase.editor.drawing.Canvas;

import javax.annotation.WillNotClose;

public sealed interface Tool permits EditorTool {


    Tool getInstance(Canvas canvas);


    void draw(@WillNotClose ShapeRenderer shapeRenderer);

    /**
     * Validate after big changes
     */
    void validate();

}
