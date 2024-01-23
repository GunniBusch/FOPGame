package de.tum.cit.ase.editor.utlis;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

public class Helper {
    public static GridPoint2 convertVector2ToGridPoint(Vector2 vector2) {
        return new GridPoint2((int) vector2.x, (int) vector2.y);
    }

    public static Vector2 convertGridPointToVector2(GridPoint2 gridPoint2) {
        return new Vector2(gridPoint2.x, gridPoint2.y);
    }
}
