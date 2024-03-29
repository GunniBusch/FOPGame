package de.tum.cit.ase.maze.objects.still.collectable;

import box2dLight.PointLight;
import box2dLight.PositionalLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.objects.GameElement;
import de.tum.cit.ase.maze.objects.dynamic.Player;

import static de.tum.cit.ase.maze.utils.CONSTANTS.*;

/**
 * A collectable is a {@link GameElement} that can be collected by a player and then applies an effect to the {@link Player}.
 */
public abstract class Collectable extends GameElement {
    protected final PositionalLight light;
    protected float ZOOM = 1.15f;
    protected RayHandler rayHandler;
    protected float frameWidth, frameHeight;
    protected boolean active = true;
    protected boolean removable = false;
    protected TextureAtlas textureAtlas;
    protected TextureRegion textureRegion;

    public Collectable(Vector2 position, World world, RayHandler rayHandler, TextureAtlas textureAtlas) {
        this(position, world, rayHandler, textureAtlas, 16, 16);

    }

    protected Collectable(Vector2 position, World world, RayHandler rayHandler, TextureAtlas textureAtlas, float frameWidth, float frameHeight) {
        this.world = world;
        this.rayHandler = rayHandler;
        this.frameHeight = frameHeight;
        this.frameWidth = frameWidth;
        this.textureAtlas = textureAtlas;
        this.createBoy(position);
        this.light = new PointLight(rayHandler, 5);
        this.light.setColor(new Color(.8f, .8f, .8f, 1f));
        this.light.setPosition(position);
        this.light.setDistance(2);
        this.light.setSoft(false);
        this.light.setActive(true);
        this.light.attachToBody(body);
        this.light.setXray(true);
    }

    /**
     * Creates the body
     *
     * @param position
     */
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
     * Called when a {@link Player} is collects it.
     *
     * @param player the {@link Player} that collected it
     */
    public abstract void collect(Player player);

    /**
     * @param deltaTime Time since last frame.
     */
    @Override
    public void update(float deltaTime) {
        if (this.removable) remove();
    }

    /**
     * Removes the collectable
     */
    public void remove() {
        world.destroyBody(body);
        light.setActive(false);
        light.remove();
        this.dispose();
    }

    /**
     * Tells if it is removable
     */
    public final boolean isRemovable() {
        return removable;
    }

    /**
     * Tells if the collectable is active. A collectable should be active if it is NOT in use by a player.
     * If it is active, it can safely not being updated and removed.
     *
     * @return if it is active
     */
    public boolean isActive() {
        return active;
    }

    @Override
    public void dispose() {
        //texture.dispose();

    }
}
