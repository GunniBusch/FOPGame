package de.tum.cit.ase.maze.objects.still;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.objects.dynamic.Player;
import de.tum.cit.ase.maze.objects.still.collectable.Collectable;
import de.tum.cit.ase.maze.utils.Score;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;


/**
 * Represents a Key collectable object in the game.
 * This class extends the Collectable class.
 */
public class Key extends Collectable {



    /**
     * Represents a Key collectable object in the game.
     * This class extends the Collectable class.
     */
    public Key(Vector2 position, World world, RayHandler rayHandler, TextureAtlas textureAtlas) {
        super(position, world, rayHandler, textureAtlas, 16, 29);
        //texture = new Texture("gameKey.png");
        textureRegion = textureAtlas.findRegion("key");
        active = false;
        ZOOM = 0.75f;

    }


    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(
                this.textureRegion,
                this.body.getPosition().x * PPM - (frameWidth / 2f),
                body.getPosition().y * PPM - (frameHeight / 2f),
                frameWidth * ZOOM,
                frameHeight * ZOOM
        );
    }

    /**
     * Collects the key from the player and updates the state of the key.
     *
     * @param player the player object that collects the key
     */
    @Override
    public void collect(Player player) {
        player.collectKey(this);
        removable = true;
        active = true;
    }
}
