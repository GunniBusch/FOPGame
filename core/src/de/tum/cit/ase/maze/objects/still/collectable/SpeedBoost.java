package de.tum.cit.ase.maze.objects.still.collectable;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.objects.dynamic.Player;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

public class SpeedBoost extends TimedCollectable {
    private final float speedBoost = 1.4f;
    private float originalSpeed;
    private TextureRegion textureRegion;

    public SpeedBoost(Vector2 position, World world, RayHandler rayHandler) {
        super(position, world, rayHandler);
        this.duration = 30;
        texture = new Texture("objects.png");
        textureRegion = new TextureRegion(this.texture, 0, 4 * 16, 16, 16);

    }

    /**
     * @param player
     */
    @Override
    protected void apply(Player player) {
        if (player.isSprint()) {
            this.originalSpeed = player.getSpeed() / player.SPEED_BOOST;
        } else {
            this.originalSpeed = player.getSpeed();
        }
        player.setSpeed(player.getSpeed() * speedBoost);

    }

    /**
     * @param player
     */
    @Override
    public void restore(Player player) {
        if (player.isSprint()) {
            player.setSpeed(this.originalSpeed * player.SPEED_BOOST);
        } else {
            player.setSpeed(this.originalSpeed);
        }
        this.active = true;

    }

    /**
     * @param spriteBatch
     */
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
