package de.tum.cit.ase.maze.objects.dynamic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Defines an abstract class, that describes a movable Character.
 * Can be a Player or a NPC.
 */
public abstract class Character implements Movable {


    /**
     * Vector for the position.
     */
    protected Vector2 position;
    protected State state = State.STILL;
    /**
     * Texture that holds animations etc.
     */
    protected Texture texture;

    public Character(float x, float y) {
        this.position = new Vector2(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public abstract TextureRegion getTexture();
}
