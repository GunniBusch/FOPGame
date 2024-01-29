package de.tum.cit.ase.maze.objects.dynamic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.ase.maze.Input.DeathListener;
import de.tum.cit.ase.maze.map.AStar;
import de.tum.cit.ase.maze.map.path.Grid;
import de.tum.cit.ase.maze.map.path.Node;
import de.tum.cit.ase.maze.utils.MapLoader;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import static de.tum.cit.ase.maze.utils.CONSTANTS.*;

/**
 * An enemy is a {@link Character} that can't be controlled by a GOD e.g. a person.
 * Instead, it will act as a dynamic "obstacle", that, if in contact, will hurt a {@link Player}.
 * The damage is handled by a {@link com.badlogic.gdx.physics.box2d.ContactListener}, more specific by the {@link de.tum.cit.ase.maze.Input.ListenerClass}.
 */
public class Enemy extends Character {
    private final float FRAME_DURATION = 0.1f;
    private final float nodeProximityThreshold;
    private final float nodeDistanceThreshold = 15f * PPM;
    private Player player = null;
    public boolean isFollowing;
    private final float DETECTION_RADIUS = 5;
    private final Grid grid;
    private List<Vector2> path;
    private int currentPathIndex;
    private int health = 1;


    public Enemy(World world, DeathListener deathListener) {
        this(world, deathListener, 0f, 0f);
    }

    public Enemy(World world, DeathListener deathListener, Player player, Vector2 position) {
        this(world, deathListener, player, position.x, position.y);
    }


    public Enemy(World world, DeathListener deathListener, Player player, float x, float y) {
        this(world, deathListener, x, y);
        this.player = player;
    }

    // TODO: Other designs
    public Enemy(World world, DeathListener deathListener, float x, float y) {
        super(world, deathListener);
        this.speed = 150f;
        frameWidth = 16;
        frameHeight = 16;
        this.currentPathIndex = 0;
        this.path = new ArrayList<>();


        int animationFrames = 3;

        this.texture = new Texture("mobs.png");
        this.createBody(x, y);
        nodeProximityThreshold = body.getFixtureList().get(0).getShape().getRadius() * PPM;

        var enemyFilter = new Filter();
        enemyFilter.categoryBits = ENEMY_BIT;
        this.body.getFixtureList().get(0).setFilterData(enemyFilter);
        FixtureDef fd = new FixtureDef();
        fd.isSensor = true;
        fd.filter.groupIndex = IGNORE_GROUP_BIT;
        CircleShape shape = new CircleShape();
        shape.setRadius(DETECTION_RADIUS);
        fd.shape = shape;

        this.body.createFixture(fd).setUserData(this);

        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);
        // Add all frames to the animation


        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(this.texture, col * frameWidth, 0, frameWidth, frameHeight));
        }
        this.walkTypesAnimationMap.put(WalkDirection.DOWN, new Animation<>(FRAME_DURATION, walkFrames));
        walkFrames.clear();
        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(this.texture, col * frameWidth, frameHeight, frameWidth, frameHeight));
        }
        this.walkTypesAnimationMap.put(WalkDirection.LEFT, new Animation<>(FRAME_DURATION, walkFrames));
        walkFrames.clear();
        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(this.texture, col * frameWidth, 2 * frameHeight, frameWidth, frameHeight));
        }
        this.walkTypesAnimationMap.put(WalkDirection.RIGHT, new Animation<>(FRAME_DURATION, walkFrames));
        walkFrames.clear();
        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(this.texture, col * frameWidth, 3 * frameHeight, frameWidth, frameHeight));
        }
        this.walkTypesAnimationMap.put(WalkDirection.UP, new Animation<>(FRAME_DURATION, walkFrames));
        walkFrames.clear();

        grid = MapLoader.getGameGrid();

        path = new ArrayList<>();


    }

    public void damage(int damage) {
        health = health - damage;
        if(health == 0) {
            soundEffects = Gdx.audio.newMusic(Gdx.files.internal("slime-squish-14539.mp3"));
            soundEffects.play();
            world.destroyBody(body);
        }
    }

    /**
     * Renders the appearance of the game object
     *
     * @param spriteBatch
     */
    @Override
    public void render(SpriteBatch spriteBatch) {

        spriteBatch.draw(
                this.getTexture(),
                this.getPosition().x * PPM - (this.frameWidth * ZOOM / 2f),
                this.getPosition().y * PPM - (this.frameHeight * ZOOM / 2f),
                this.frameWidth * ZOOM, this.frameWidth * ZOOM
        );

    }


    /**
     * Moves the Object
     *
     * @param deltaTime Time since last frame.
     */
    @Override
    public void update(float deltaTime) {
        if (this.state == State.WALKING) {
            this.stateTime += deltaTime;
            if (!isFollowing) this.state = State.STILL;
        }

        if (isFollowing) {
            setPath(AStar.findPath(grid, player.getPosition().scl(0.5f), this.getPosition().scl(0.5f)));

            if (path.size() >= 2) {
                Vector2 nextNode = path.get(currentPathIndex);
                Vector2 targetPosition = nextNode.cpy();
                moveTowards(targetPosition);
            } else if (player.getPosition().cpy().scl(0.5f).dst(this.getPosition().cpy().scl(0.5f)) <= nodeProximityThreshold) {
                this.updateStateAndDirection(State.STILL, this.walkDirectionList.get(0));
                this.body.setLinearVelocity(0f, 0f);

            } else {
                this.moveTowards(player.getPosition().cpy().scl(0.5f));
            }
        }
    }


    public Player getPlayer() {
        return this.player;
    }


    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Moves the enemy towards a Point
     *
     * @param targetPosition Vector that represents a point e.g. Vector points from (0,0)
     */
    private void moveTowards(Vector2 targetPosition) {
        Vector2 currentPosition = this.body.getPosition().cpy().scl(0.5f);
        Vector2 directionToTarget = new Vector2(targetPosition.x - currentPosition.x, targetPosition.y - currentPosition.y);
        float distanceToTarget = directionToTarget.len();
        if (path.size() < 2) {


            if (distanceToTarget <= nodeProximityThreshold) {
                this.body.setLinearVelocity(0f, 0f); // Stop the enemy
                updateStateAndDirection(State.STILL, this.walkDirectionList.get(0)); // Update state and walk direction accordingly
            }

            this.isFollowing = !(distanceToTarget >= nodeDistanceThreshold);
        }

        // Normalize and scale the direction vector by the speed
        Vector2 velocity = directionToTarget.nor().scl(this.speed / PPM);

        // Set the linear velocity of the body
        this.body.setLinearVelocity(velocity);

        // Update state and walk direction for animation
        updateStateAndDirection(State.WALKING, determineWalkDirection(velocity));

    }

    /**
     * Determines the Direction the Enemy walks.
     *
     * @param velocity {@link Vector2} that represents the direction of the movement.
     * @return WalkDirection
     */
    private WalkDirection determineWalkDirection(Vector2 velocity) {
        // Determine the primary direction based on the velocity vector
        if (MathUtils.round(Math.abs(velocity.x)) > MathUtils.round(Math.abs(velocity.y))) {
            return velocity.x > 0 ? WalkDirection.RIGHT : WalkDirection.LEFT;
        } else {
            return velocity.y > 0 ? WalkDirection.UP : WalkDirection.DOWN;
        }

    }

    /**
     * Updates the state and direction. Convenience wrapper for start Moving and Stop moving.
     *
     * @param state     {@link State} to which it should be changed.
     * @param direction {@link WalkDirection} it should change to.
     */
    public synchronized void updateStateAndDirection(State state, WalkDirection direction) {
        this.state = state;
        this.walkDirectionList = new ArrayList<>();
        this.walkDirectionList.add(direction);
    }

    /**
     * Sets the path it should follow the start node
     *
     * @param path {@link List<Node>} it should follow.
     */

    public void setPath(@NonNull List<Node> path) {
        this.path = path.parallelStream().map(Node::getPosition).toList();
        this.currentPathIndex = path.size() - 2;
    }

    public List<Vector2> getPath() {
        return path;
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        this.texture.dispose();
        this.world.destroyBody(body);
    }
}
