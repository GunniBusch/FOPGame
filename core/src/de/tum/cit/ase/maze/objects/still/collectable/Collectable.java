package de.tum.cit.ase.maze.objects.still.collectable;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.objects.GameElement;
import de.tum.cit.ase.maze.objects.dynamic.Player;

import static de.tum.cit.ase.maze.utils.CONSTANTS.*;

public abstract class Collectable extends GameElement {
    protected Texture texture;
    protected float ZOOM = 1.15f;
    protected RayHandler rayHandler;
    protected float frameWidth, frameHeight;
    protected boolean aktive = true;
    protected boolean removable = false;

    public Collectable(Vector2 position, World world, RayHandler rayHandler) {
        this(position,world,rayHandler,16,16);

    }
    protected Collectable(Vector2 position, World world, RayHandler rayHandler, float frameWidth, float frameHeight) {
        this.world = world;
        this.rayHandler = rayHandler;
        this.frameHeight = frameHeight;
        this.frameWidth = frameWidth;
        this.createBoy(position);

    }

    public abstract void collect(Player player);

    /**
     * @param deltaTime Time since last frame.
     */
    @Override
    public void update(float deltaTime) {
        if (this.removable) remove();
    }

    public void remove() {
        world.destroyBody(body);
        this.dispose();
    }

    protected void createBoy(Vector2 position) {

        BodyDef def = new BodyDef();

        def.type = BodyDef.BodyType.StaticBody;

        def.position.set(position.cpy().scl(SCALE));
        def.fixedRotation = true;

        this.body = this.world.createBody(def);

        CircleShape shape = new CircleShape();
        shape.setRadius(Math.max(this.frameWidth * ZOOM / 2f / PPM, this.frameHeight * ZOOM / 2f / PPM) / 2f);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.isSensor = true;
        fd.filter.categoryBits = COLLECTABLE_BIT;
        fd.filter.maskBits = PLAYER_BIT;
        fd.filter.groupIndex = -IGNORE_GROUP_BIT;
        this.body.createFixture(fd).setUserData(this);
        shape.dispose();
    }

    /**
     * Tells if it is removable
     *
     * @return removable
     */
    public final boolean isRemovable() {
        return removable;
    }


}
