package de.tum.cit.ase.maze.objects.dynamic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

/**
 * Defines an abstract class, that describes a movable Character.
 * Can be a Player or a NPC.
 */
public abstract class Character implements Movable, Disposable {

    protected int frameWidth;
    protected int frameHeight;
    /**
     * Vector for the position.
     */
    //protected Vector2 position;
    /**
     * State of the object and its direction.
     */
    protected State state = State.STILL(WalkDirection.DOWN);

    protected float speed;
    /**
     * Texture that holds animations etc.
     */
    protected Texture texture;
    /**
     * Body that defines the Physical body. E.g. for collision etc.
     */
    protected Body body;
    /**
     * Box2d world.
     */
    protected World world;

    public Character(World world) {
        this.world = world;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public abstract TextureRegion getTexture();

    public Body getBody() {
        return body;
    }
}
