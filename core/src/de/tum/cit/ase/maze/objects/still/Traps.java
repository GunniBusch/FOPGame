package de.tum.cit.ase.maze.objects.still;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.objects.dynamic.Player;
import de.tum.cit.ase.maze.objects.still.collectable.Collectable;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

/**
 * This class represents a trap, which is a type of collectable in the game. Traps can be collected by a player and
 * have an effect on the player when collected.
 *
 * Traps inherit from the Collectable class and have additional methods and behavior specific to traps.
 */
public class Traps extends Collectable {
    private final TextureRegion textureRegion;

    /**
     * Constructs a Trap object at the specified position using the given parameters.
     *
     * @param position     the position of the trap in the game world
     * @param world        the Box2D world in which the trap will be simulated
     * @param rayHandler   the RayHandler used for rendering lights and shadows
     * @param textureAtlas the texture atlas containing the trap's textures
     */
    public Traps(Vector2 position, World world, RayHandler rayHandler, TextureAtlas textureAtlas) {
        super(position, world, rayHandler, textureAtlas);
        texture = new Texture("traps.png");
        textureRegion = new TextureRegion(texture);
        active = false;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(
                this.textureRegion,
                this.body.getPosition().x * PPM - (16 / 2f),
                body.getPosition().y * PPM - (16 / 2f),
                frameWidth * ZOOM,
                frameHeight * ZOOM
        );
    }

    /**
     * Applies a damage effect to the given player when the trap is collected.
     *
     * @param player the player to whom the damage effect will be applied
     */
    @Override
    public void collect(Player player) {
        player.makeDamage(1);
    }

}