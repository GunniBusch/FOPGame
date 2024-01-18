package de.tum.cit.ase.maze.objects.still;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.CircleShape;
import de.tum.cit.ase.maze.objects.CollectableManager;
import de.tum.cit.ase.maze.objects.GameElement;
import de.tum.cit.ase.maze.objects.ObjectType;
import de.tum.cit.ase.maze.objects.dynamic.Player;
import de.tum.cit.ase.maze.objects.still.collectable.Collectable;
import de.tum.cit.ase.maze.utils.MapLoader;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;


public class Key extends Collectable {
    private boolean isCollected;
    private TextureRegion textureRegion;

    public Key(Vector2 position, World world, RayHandler rayHandler) {
        super(position, world, rayHandler);
        texture = new Texture("gameKey.png");
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
        player.collectKey(this);
        removable = true;
        active = true;
    }
}
