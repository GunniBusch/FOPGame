package de.tum.cit.ase.maze.objects.still.collectable;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.ase.maze.objects.dynamic.Player;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

public class HealthCollectable extends Collectable {
    private final Animation<TextureRegion> animation;
    private float stateTime = 0f;
    private final int HEALTH_TO_RESTORE = 2;


    public HealthCollectable(Vector2 position, World world, RayHandler rayHandler) {
        super(position, world, rayHandler);



        texture = new Texture("objects.png");


        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);
        int animationFrames = 4;
        // Add all frames to the animation
        int row = 3;
        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(this.texture, col * 16, row * 16, 16, 16));
        }
        this.animation = new Animation<>(0.1f, walkFrames);
        walkFrames.clear();


    }

    /**
     * @param player
     */
    @Override
    public void collect(Player player) {
        if (player.heal(HEALTH_TO_RESTORE)) removable = true;
    }

    /**
     * @param spriteBatch
     */
    @Override
    public void render(SpriteBatch spriteBatch) {

        spriteBatch.draw(
                this.animation.getKeyFrame(stateTime, true),
                this.body.getPosition().x * PPM - (16 / 2f),
                body.getPosition().y * PPM - (16 / 2f),
                this.animation.getKeyFrame(stateTime, true).getRegionWidth() * ZOOM,
                this.animation.getKeyFrame(stateTime, true).getRegionHeight() * ZOOM

        );


    }

    /**
     * @param deltaTime Time since last frame.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        stateTime += deltaTime;

    }
}
