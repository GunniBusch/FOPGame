package de.tum.cit.ase.editor.utlis;

import com.badlogic.gdx.graphics.Color;

public enum TileTypes {
    Wall(new Color(0.1f, 0.1f, .1f, 1), "Wall"),    // Gray
    Player(new Color(0f, 0f, 1f, 1), "Player"),        // Blue
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

    public String getDisplayName() {
        return this.displayName;
    }
}
