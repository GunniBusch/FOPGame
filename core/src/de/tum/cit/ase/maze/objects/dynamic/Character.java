package de.tum.cit.ase.maze.objects.dynamic;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.maze.Input.DeathListener;
import de.tum.cit.ase.maze.objects.GameElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PLAYER_MAX_HEALTH;
import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

/**
 * Defines an abstract class, that describes a movable Character.
 * Can be a Player or a NPC.
 */
public abstract class Character extends GameElement {

    protected int frameWidth;
    protected int frameHeight;
    /**
     * Health of a Character
     */
    protected int health = 4;
    /**
     * State of the object.
     */
    protected State state = State.STILL;
    /**
     * List of requested directions
     */
    protected List<WalkDirection> walkDirectionList = List.of(WalkDirection.DOWN);

    /**
     * Defines the speed the Character is moving
     */
    protected float speed;

    /**
     * List of moving animations
     */
    protected final Map<WalkDirection, Animation<TextureRegion>> walkTypesAnimationMap;

    /**
     * Time for a state
     */
    protected float stateTime = 0f;
    /**
     * Handles death of a Charter
     */
    protected final DeathListener deathListener;

    protected final float ZOOM = 1.254f;


    public Character(World world, DeathListener deathListener) {
        this.world = world;
        this.walkTypesAnimationMap = new HashMap<>();
        this.deathListener = deathListener;
    }

    /**
     * Gets position
     *
     * @return {@link Vector2} of the position in Box2D scale. (REQUIRED to covert into pixel)
     */
    public Vector2 getPosition() {
        return this.body.getPosition();
    }


    /**
     * Gets Texture.
     *
     * @return TextureRegion at the moment
     */
    public TextureRegion getTexture() {
        return this.walkTypesAnimationMap.get(this.walkDirectionList.get(this.walkDirectionList.size() - 1)).getKeyFrame(this.state == State.WALKING ? this.stateTime : 0f, true);
    }

    /**
     * Creates the {@link Body}
     *
     * @param x Position x
     * @param y Position y
     */
    void createBody(float x, float y) {

        BodyDef def = new BodyDef();

        def.type = BodyDef.BodyType.DynamicBody;

        def.position.set(x / PPM, y / PPM);
        def.fixedRotation = true;
        def.linearDamping = 9f;

        Body pBody = this.world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(this.frameWidth * ZOOM / 2f / PPM, this.frameHeight * ZOOM / 2f / PPM);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 2.0f;
        pBody.createFixture(fd).setUserData(this);
        shape.dispose();
        this.body = pBody;
    }

    /**
     * Moves the Object
     *
     * @param deltaTime Time since last frame.
     */
    @Override
    public void update(float deltaTime) {
        if (this.state == State.WALKING) {
            //Gdx.app.log("Pos Ply", this.state.getDirection().toString());
            this.stateTime += deltaTime;

            switch (this.walkDirectionList.get(this.walkDirectionList.size() - 1)) {
                case UP -> this.body.setLinearVelocity(0f, speed / PPM);
                case DOWN -> this.body.setLinearVelocity(0f, -speed / PPM);
                case LEFT -> this.body.setLinearVelocity(-speed / PPM, 0f);
                case RIGHT -> this.body.setLinearVelocity(speed / PPM, 0f);
            }
        }
        if (isDead()) {
            deathListener.onDeath(this);
        }
    }

    /**
     * Tells if {@link Character} has 0 lives;
     */
    public boolean isDead() {
        return health <= 0;
    }

    public Body getBody() {
        return body;
    }

    public int getHealth() {
        return health;
    }


    /**
     * Applies damage.
     *
     * @param damage damage to apply
     */
    public void makeDamage(int damage) {
        this.health -= damage;
    }


    /**
     * Heals the Character
     *
     * @param amountToHeal Health amount to restore
     * @return if health was full
     */
    public boolean heal(int amountToHeal) {
        var health_restorable = MathUtils.clamp(amountToHeal, 0, PLAYER_MAX_HEALTH - health);
        this.health += health_restorable;
        return health_restorable > 0;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public Vector2 getDimensions(){
        return new Vector2(frameWidth * ZOOM, frameHeight * ZOOM);
    }
}
