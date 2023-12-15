package de.tum.cit.ase.maze.objects.dynamic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

public class Player extends Character {
    private Map<WalkDirection, Animation<TextureRegion>> walkTypesAnimationMap;

    public Player(float x, float y) {
        super(x, y);
        this.texture = new Texture("character.png");
        walkTypesAnimationMap = new HashMap<>();
        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4;

        // libGDX internal Array instead of ArrayList because of performance
        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);

        // Add all frames to the animation
        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(this.texture, col * frameWidth, 0, frameWidth, frameHeight));
        }

        this.walkTypesAnimationMap.put(WalkDirection.DOWN, new Animation<>(0.1f, walkFrames));


    }

    /**
     * @return TextureRegion at the moment
     */
    @Override
    public TextureRegion getTexture() {
        return null;
    }

    /**
     * Moves the Object
     */
    @Override
    public void updateMotion() {

        switch (this.state.getDirection()) {
            case UP:
                position.add(0,5*5 * Gdx.graphics.getDeltaTime());
                break;
            case DOWN:
                break;
            case Left:
                break;
            case Right:
                break;
            default:
                break;
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
}