package de.tum.cit.ase.editor.utlis;

import com.badlogic.gdx.graphics.Color;
import de.tum.cit.ase.maze.objects.ObjectType;

/**
 * Represents the different types of tiles in a map.
 */
public enum TileTypes {
    Wall(new Color(0.1f, 0.1f, .1f, 1), "Wall"),    // Gray
    Key(new Color(1f, 1f, 0f, 1), "Key"),           // Yellow
    Entry(new Color(1f, 0f, 1f, 1), "Entry"),         // Magenta
    Exit(new Color(0f, 0.5f, 0f, 1), "Exit"),        // Dark green
    Enemy(new Color(1f, 0f, 0f, 1), "Enemy"),         // Red
    Obstacle(new Color(0f, 1f, 1f, 1), "Obstacle");      // Cyan
    public final Color canvasColor;
    private final String displayName;

    TileTypes(Color canvasColor, String displayName) {
        this.canvasColor = canvasColor;
        this.displayName = displayName;
    }

    /**
     * Returns the display name of the tile type.
     *
     * @return the display name of the tile type
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Converts a {@link de.tum.cit.ase.maze.screens.MiniMap.TileType} to an {@link ObjectType}.
     *
     * @param tileType the TileType to convert
     * @return the converted {@link ObjectType}
     */
    public static ObjectType convertToObjectType(TileTypes tileType) {
        switch (tileType) {
            case Wall -> {
                return ObjectType.Wall;
            }
            case Key -> {
                return ObjectType.Key;
            }
            case Entry -> {
                return ObjectType.EntryPoint;
            }
            case Exit -> {
                return ObjectType.Exit;
            }
            case Enemy -> {
                return ObjectType.Enemy;
            }
            case Obstacle -> {
                return ObjectType.Trap;
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Converts an {@link ObjectType} to a corresponding {@link TileTypes TileType}.
     *
     * @param objectType the {@link ObjectType} to convert
     * @return the corresponding {@link TileTypes}, or null if no corresponding value is found
     */
    public static TileTypes convertFromObjectType(ObjectType objectType) {
        switch (objectType) {
            case Wall -> {
                return Wall;
            }
            case EntryPoint -> {
                return Entry;
            }
            case Exit -> {
                return Exit;
            }
            case Trap -> {
                return Obstacle;
            }
            case Enemy -> {
                return Enemy;
            }
            case Key -> {
                return Key;
            }
            default -> {
                return null;
            }
        }
    }
}
