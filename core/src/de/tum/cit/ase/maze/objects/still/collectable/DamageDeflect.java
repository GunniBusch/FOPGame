package de.tum.cit.ase.maze.objects.still.collectable;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.objects.dynamic.Player;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

/**
 * Makes the {@link Player} invulnerable for the {@link #duration} this has an effect
 */
public class DamageDeflect extends TimedCollectable {

    public DamageDeflect(Vector2 position, World world, RayHandler rayHandler, TextureAtlas textureAtlas) {
        super(position, world, rayHandler, textureAtlas);
        this.duration = 15;
        textureRegion = textureAtlas.findRegion("powerup-shield");
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

    @Override
    protected void apply(Player player) {
        player.setVulnerable(false);

    }

    @Override
    public void restore(Player player) {
        player.setVulnerable(true);
        this.active = true;

    }
}
