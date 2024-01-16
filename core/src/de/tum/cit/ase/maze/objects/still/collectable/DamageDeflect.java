package de.tum.cit.ase.maze.objects.still.collectable;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.objects.dynamic.Player;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

public class DamageDeflect extends TimedCollectable {
    private final TextureRegion textureRegion;

    public DamageDeflect(Vector2 position, World world, RayHandler rayHandler) {
        super(position, world, rayHandler);
        this.duration = 15;
        texture = new Texture("objects.png");
        textureRegion = new TextureRegion(this.texture, 0, 4 * 16, 16, 16);

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

    /**
     * @param player
     */
    @Override
    protected void apply(Player player) {
        player.setVulnerable(false);

    }

    /**
     * @param player
     */
    @Override
    public void restore(Player player) {
        player.setVulnerable(true);
        this.active = true;

    }
}
