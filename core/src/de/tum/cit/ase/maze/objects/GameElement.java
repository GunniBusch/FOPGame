package de.tum.cit.ase.maze.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class GameElement {
    protected World world;
    protected Body body;
    protected Texture texture;

    public abstract void render(SpriteBatch spriteBatch);
}
