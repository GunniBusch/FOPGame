package de.tum.cit.ase.maze.objects.dynamic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;

public class Enemy extends Character {

    public Enemy(World world) {
        this(world, 0f, 0f);
    }

    public Enemy(World world, float x, float y) {
        super(world);

    }

    /**
     * @return
     */
    @Override
    public TextureRegion getTexture() {
        return null;
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {

    }

    /**
     * Moves the Object
     *
     * @param deltaTime Time since last frame.
     */
    @Override
    public void update(float deltaTime) {

    }

    /**
     * Starts moving in defined direction.
     * Should NOT move in two directions. e.g. left and up.
     *
     * @param direction Direction to move.
     */
    @Override
    public void startMoving(WalkDirection direction) {

    }

    /**
     * Stops moving for one direction.
     * For example if Object moves left, but was overridden, it does not stop if it should stop moving right.
     *
     * @param direction Direction to stop moving.
     */
    @Override
    public void stopMoving(WalkDirection direction) {

    }
}
