package de.tum.cit.ase.maze.objects.still;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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
 * This class represents a specific type of collectable called "Trap2". It extends the Collectable class and inherits its properties and behaviors.
 */
public class Trap2 extends Collectable {
    private final TextureRegion textureRegion;
    Music soundEffects;

    /**
     * Constructs a Trap2 object.
     *
     * @param position      the position of the Trap2 object
     * @param world         the world the Trap2 object belongs to
     * @param rayHandler    the rayHandler used for lighting
     * @param textureAtlas  the textureAtlas used to retrieve the sprite
     */
    public Trap2(Vector2 position, World world, RayHandler rayHandler, TextureAtlas textureAtlas) {
        super(position, world, rayHandler, textureAtlas);
        texture = new Texture("trap2.png");
        textureRegion  = new TextureRegion(texture);
    }

    /**
     * Decreases the player's speed by 25%, and plays a sound effect.
     *
     * @param player the player object to collect the trap2
     */
    @Override
    public void collect(Player player) {
        player.setSpeed(player.getSpeed() / 1.25f);
        soundEffects = Gdx.audio.newMusic(Gdx.files.internal("fireball-whoosh-1-179125.mp3"));
        soundEffects.play();


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


}

