package de.tum.cit.ase.maze.objects.dynamic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import javax.print.attribute.standard.PagesPerMinute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

/**
 * Class for the player.
 */
public class Player extends Character {

    public Player(World world) {
        this(world, 0, 0);
    }

    /**
     * Creates a Player with respective coordinates.
     *
     * @param x X-Coordinate
     * @param y Y-Coordinate
     */
    public Player(World world, float x, float y) {
        super(world);
        this.speed = 150f;
        frameWidth = 16;
        frameHeight = 32;

        int animationFrames = 4;

        this.texture = new Texture("character.png");
        this.createBody(x, y);
        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);
        // Add all frames to the animation
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < animationFrames; col++) {
                walkFrames.add(new TextureRegion(this.texture, col * frameWidth, row * frameHeight, frameWidth, frameHeight));
            }
            this.walkTypesAnimationMap.put(WalkDirection.values()[row], new Animation<>(0.1f, walkFrames));
            walkFrames.clear();
        }

    }


    /**
     * Starts moving in defined direction.
     * Should NOT move in two directions. e.g. left and up. But after release other pressed buttons take over.
     *
     * @param direction Direction to move.
     */
    @Override
    public void startMoving(WalkDirection direction) {
        switch (this.state) {
            case STILL -> {
                this.state = State.WALKING;
                this.walkDirectionList = new ArrayList<>();
                this.walkDirectionList.add(direction);
            }
            case WALKING -> this.walkDirectionList.add(direction);
        }

    }

    /**
     * Stops moving for one direction.
     * For example if Object moves left, but was overridden, it does not stop if it should stop moving right.
     * And also allows after stopping to move into other requested directions.
     *
     * @param direction Direction to stop moving.
     */
    @Override
    public void stopMoving(WalkDirection direction) {
        this.walkDirectionList.remove(direction);
        if (this.walkDirectionList.isEmpty()) {
            this.state = State.STILL;
            this.walkDirectionList = new ArrayList<>();
            this.walkDirectionList.add(direction);

            this.body.setLinearVelocity(0, 0);
        }
    }

    /**
     * Requests elevated Speed
     *
     * @param sprint
     */
    public void setSprint(boolean sprint) {
        if (sprint) {
            speed *= 1.5f;
        } else {
            speed /= 1.5f;
        }
    }





    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        this.texture.dispose();
    }

    /**
     * Renders the appearence of the game object
     *
     * @param spriteBatch
     */
    @Override
    public void render(SpriteBatch spriteBatch) {

    }
}