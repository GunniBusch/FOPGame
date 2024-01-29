package de.tum.cit.ase.editor.utlis;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;

/**
 * The Helper class provides utility methods for converting between Vector2 and GridPoint2 objects.
 */
public class Helper {
    /**
     * Converts a Vector2 object to a GridPoint2 object.
     *
     * @param vector2 the Vector2 object to convert
     * @return the converted GridPoint2 object
     */
    public static GridPoint2 convertVector2ToGridPoint(Vector2 vector2) {
        return new GridPoint2((int) vector2.x, (int) vector2.y);
    }

    /**
     * Converts a GridPoint2 object to a Vector2 object.
     *
     * @param gridPoint2 the GridPoint2 object to convert
     * @return the converted Vector2 object
     */
    public static Vector2 convertGridPointToVector2(GridPoint2 gridPoint2) {
        return new Vector2(gridPoint2.x, gridPoint2.y);
    }

    public static TileTypes[][] cloneGrid(TileTypes[][] gridToCLone) {
        return Arrays.stream(gridToCLone)
                .map(a -> Arrays.copyOf(a, a.length))
                .toArray(TileTypes[][]::new);
    }
}
