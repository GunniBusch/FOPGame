package de.tum.cit.ase.maze.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;


/**
 * This class is the common superclass for all game objects
 */
public abstract class GameElement {

    /**
     * Saves the box2d world
     */
    protected World world;
    /**
     * Represents the physics body
     */
    protected Body body;
    /**
     * Defines the texture of the object
     */
    protected Texture texture;

    /**
     * Renders the appearence of the game object
     * @param spriteBatch
     */
    public abstract void render(SpriteBatch spriteBatch);
}
