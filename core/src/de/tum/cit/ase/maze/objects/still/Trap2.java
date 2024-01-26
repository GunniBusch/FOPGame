package de.tum.cit.ase.maze.objects.still;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.objects.dynamic.Player;
import de.tum.cit.ase.maze.objects.still.collectable.TimedCollectable;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

public class Trap2 extends TimedCollectable {
    private final TextureRegion textureRegion;

    public Trap2(Vector2 position, World world, RayHandler rayHandler, TextureAtlas textureAtlas) {
        super(position, world, rayHandler, textureAtlas);
        textureRegion = textureAtlas.findRegion("trap2.png");
        duration = 15;
    }

    @Override
    protected void apply(Player player) {
        player.setSpeed(player.getSpeed() / 2);
    }

    @Override
    public void restore(Player player) {

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