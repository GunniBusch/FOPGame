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

public class Trap2 extends Collectable {
    private final TextureRegion textureRegion;
    Music soundEffects;

    public Trap2(Vector2 position, World world, RayHandler rayHandler, TextureAtlas textureAtlas) {
        super(position, world, rayHandler, textureAtlas);
        texture = new Texture("trap2.png");
        textureRegion  = new TextureRegion(texture);
    }

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
