package de.tum.cit.ase.maze.objects.still.collectable;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.objects.dynamic.Player;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

/**
 * {@link Collectable} that restores the health of the player by {@link HealthCollectable#HEALTH_TO_RESTORE}
 */
public class HealthCollectable extends Collectable {
    private final Animation<TextureRegion> animation;
    private final int HEALTH_TO_RESTORE = 2;
    private float stateTime = 0f;

    public HealthCollectable(Vector2 position, World world, RayHandler rayHandler, TextureAtlas textureAtlas) {
        super(position, world, rayHandler, textureAtlas);
        animation = new Animation<>(0.1f, textureAtlas.findRegions("heart"), Animation.PlayMode.LOOP);
    }

    @Override
    public void collect(Player player) {
        if (player.heal(HEALTH_TO_RESTORE)) removable = true;
    }


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

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        stateTime += deltaTime;
    }

    @Override
    public void dispose() {
    }
}
