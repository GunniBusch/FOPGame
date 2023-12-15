package de.tum.cit.ase.maze.objects.dynamic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for the player.
 */
public class Player extends Character {
    private Map<WalkDirection, Animation<TextureRegion>> walkTypesAnimationMap;
    private float stateTime = 0f;

    /**
     * Creates a Player with respective coordinates.
     *
     * @param x X-Coordinate
     * @param y Y-Coordinate
     */
    public Player(float x, float y) {
        super(x, y);
        this.speed = 50f;
        this.texture = new Texture("character.png");
        walkTypesAnimationMap = new HashMap<>();
        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4;

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
     * Gets Texture.
     *
     * @return TextureRegion at the moment
     */
    @Override
    public TextureRegion getTexture() {

        return this.walkTypesAnimationMap.get(this.state.getDirection()).getKeyFrame(this.state == State.WALKING ? this.stateTime : 0f, true);
    }

    /**
     * Updates the Player.
     *
     * @param deltaTime Time since last frame.
     */
    @Override
    public void update(float deltaTime) {

        if (this.state == State.WALKING) {
            this.stateTime += deltaTime;
            switch (this.state.getDirection()) {
                case UP -> position.add(0, speed * deltaTime);
                case DOWN -> position.sub(0, speed * deltaTime);
                case LEFT -> position.sub(speed * deltaTime, 0);
                case RIGHT -> position.add(speed * deltaTime, 0);
                default -> {
                }
            }

        }
    }

    /**
     * Starts moving in defined direction.
     * Should NOT move in two directions. e.g. left and up.
     *
     * @param direction Direction to move.
     */
    @Override
    public void startMoving(WalkDirection direction) {
        this.state = State.WALKING(direction);
    }

    /**
     * Stops moving for one direction.
     * For example if Object moves left, but was overridden, it does not stop if it should stop moving right.
     *
     * @param direction Direction to stop moving.
     */
    @Override
    public void stopMoving(WalkDirection direction) {
        if (this.state.getDirection() == direction) {
            this.state = State.STILL;
        }
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        this.texture.dispose();
    }
}