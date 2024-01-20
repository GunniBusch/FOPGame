package de.tum.cit.ase.maze.objects.still;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.objects.dynamic.Player;
import de.tum.cit.ase.maze.objects.still.collectable.Collectable;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

public class Traps extends Collectable {
    private final TextureRegion textureRegion;

    public Traps(Vector2 position, World world, RayHandler rayHandler) {
        super(position, world, rayHandler);
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

    @Override
    public void collect(Player player) {
        player.makeDamage(1);
    }

}