package de.tum.cit.ase.maze.objects.dynamic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import javax.print.attribute.standard.PagesPerMinute;
import java.util.HashMap;
import java.util.Map;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

/**
 * Class for the player.
 */
public class Player extends Character {

    /**
     * List of Moving animations
     */
    private Map<WalkDirection, Animation<TextureRegion>> walkTypesAnimationMap;
    /**
     * Time for a state
     */
    private float stateTime = 0f;

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
        this.walkTypesAnimationMap = new HashMap<>();
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
            Gdx.app.log("Pos Ply", this.body.getPosition().toString());
            this.stateTime += deltaTime;

            switch (this.state.getDirection()) {
                case UP -> this.body.setLinearVelocity(0f, speed / PPM);
                case DOWN -> this.body.setLinearVelocity(0f, -speed / PPM);
                case LEFT -> this.body.setLinearVelocity(-speed / PPM, 0f);
                case RIGHT -> this.body.setLinearVelocity(speed / PPM, 0f);
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
            this.state = State.STILL(direction);
            this.body.setLinearVelocity(0, 0);
        }
    }

    private void createBody(float x, float y) {
        Body pBody;
        BodyDef def = new BodyDef();

        def.type = BodyDef.BodyType.DynamicBody;

        def.position.set(x / PPM, y / PPM);
        def.fixedRotation = true;
        pBody = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(this.frameWidth / 2f / PPM, this.frameHeight / 2f / PPM);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 1.0f;
        pBody.createFixture(fd).setUserData(this);
        shape.dispose();
        this.body = pBody;
        /*
        Body pBody;
        BodyDef pDef = new BodyDef();
        pDef.type = BodyDef.BodyType.DynamicBody;
        pDef.position.set(x / PPM, y / PPM);
        pDef.fixedRotation = true;
        pBody = world.createBody(pDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(this.frameWidth / 2f / PPM, this.frameHeight / 2f / PPM);
        pBody.createFixture(shape, 1.0f);
        shape.dispose();
        this.body = pBody;
        this.body.setUserData(this);

         */

    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        this.texture.dispose();
    }
}