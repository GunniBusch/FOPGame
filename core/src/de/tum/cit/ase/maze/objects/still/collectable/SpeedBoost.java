package de.tum.cit.ase.maze.objects.still.collectable;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.objects.dynamic.Player;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

/**
 * Boosts the speed of the player by {@link #speedBoost}
 */
public class SpeedBoost extends TimedCollectable {
    private final float speedBoost = 1.4f;
    private float originalSpeed;

    public SpeedBoost(Vector2 position, World world, RayHandler rayHandler, TextureAtlas textureAtlas) {
        super(position, world, rayHandler, textureAtlas);
        this.duration = 30;
        textureRegion = textureAtlas.findRegion("powerup-speed");
    }


    @Override
    protected void apply(Player player) {
        if (player.isSprint()) {
            this.originalSpeed = player.getSpeed() / player.SPRINT_BOOST;
        } else {
            this.originalSpeed = player.getSpeed();
        }
        player.setSpeed(player.getSpeed() * speedBoost);
    }


    @Override
    public void restore(Player player) {
        if (player.isSprint()) {
            player.setSpeed(this.originalSpeed * player.SPRINT_BOOST);
        } else {
            player.setSpeed(this.originalSpeed);
        }
        this.active = true;

    }


    @Override
    public void render(SpriteBatch spriteBatch) {
        if (active) {
            spriteBatch.draw(
                    this.textureRegion,
                    this.body.getPosition().x * PPM - (16 / 2f),
                    body.getPosition().y * PPM - (16 / 2f),
                    frameWidth * ZOOM,
                    frameHeight * ZOOM
            );
        }

    }


}
