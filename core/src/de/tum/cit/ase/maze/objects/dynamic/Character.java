package de.tum.cit.ase.maze.objects.dynamic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Defines an abstract class, that describes a movable Character.
 * Can be a Player or a NPC.
 */
public abstract class Character implements Movable {



    /**
     * Flag if object should move left.
     */
    protected boolean leftMove;
    /**
     * Flag if object should move right.
     */
    protected boolean rightMove;
    /**
     * Flag if object should move up.
     */
    protected boolean upMove;
    /**
     * Flag if object should move down.
     */
    protected boolean downMove;
    protected float x;
    protected float y;
    protected Texture texture;

    public Character(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Texture getTexture() {
        return texture;
    }
}
