package de.tum.cit.ase.maze.objects.dynamic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

public class Enemy extends Character {
    private Player player = null;
    public boolean isFollowing;

    public Enemy(World world) {
        this(world, 0f, 0f);
    }

    public Enemy(World world, float x, float y) {
        super(world);
        this.speed = 150f;
        frameWidth = 16;
        frameHeight = 16;

        int animationFrames = 3;

        this.texture = new Texture("mobs.png");
        this.createBody(x, y);

        FixtureDef fd = new FixtureDef();
        fd.isSensor = true;

        CircleShape shape = new CircleShape();
        shape.setRadius(frameHeight * 5 / PPM);
        fd.shape = shape;

        this.body.createFixture(fd).setUserData(this);

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
     * Should NOT move in two directions. e.g. left and up.
     *
     * @param direction Direction to move.
     */
    @Override
    public void startMoving(WalkDirection direction) {
        this.state = State.WALKING;
        this.walkDirectionList = new ArrayList<>();
        this.walkDirectionList.add(direction);

    }

    /**
     * Stops moving for one direction.
     * For example if Object moves left, but was overridden, it does not stop if it should stop moving right.
     *
     * @param direction Direction to stop moving.
     */
    @Override
    public void stopMoving(WalkDirection direction) {
        this.state = State.STILL;
        this.walkDirectionList = new ArrayList<>();
        this.walkDirectionList.add(direction);
        this.body.setLinearVelocity(0f, 0f);

    }

    /**
     * Renders the appearence of the game object
     *
     * @param spriteBatch
     */
    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin(); // Important to call this before drawing anything

        spriteBatch.draw(
                this.getTexture(),
                this.getPosition().x * PPM - (this.frameWidth / 2f),
                this.getPosition().y * PPM - (this.frameHeight / 2f)

        );


        spriteBatch.end();

    }


    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        this.texture.dispose();
        this.world.destroyBody(body);

    }

    /**
     * Moves the Object
     *
     * @param deltaTime Time since last frame.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (isFollowing) {
            Vector2 direction = new Vector2(
                    player.getPosition().x - this.getPosition().x ,
                    player.getPosition().y - this.getPosition().y
            );
            direction.nor(); // Normalize to get direction
            this.body.setLinearVelocity(direction.scl(player.speed * 0.8f / PPM)); // npcSpeed is the speed of the NPC
            if(this.getPosition().dst2(player.getPosition())>50) isFollowing = false;
            if (this.getPosition().dst2(player.getPosition())<20f/PPM) this.body.setLinearVelocity(new Vector2(0,0));
        }
    }


    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
