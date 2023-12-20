package de.tum.cit.ase.maze.objects.dynamic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.ase.maze.map.path.Grid;
import de.tum.cit.ase.maze.map.AStar;
import de.tum.cit.ase.maze.map.path.Node;

import java.util.*;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;

public class Enemy extends Character {
    int count;
    private final float nodeProximityThreshold = 0.15f;
    private final float nodeDistanceThreshold = 15f * PPM;
    public List<Node> al;
    private Player player = null;
    public boolean isFollowing;
    private final float DETECTION_RADIUS = 10;

    Grid mp;
    private List<Node> path;
    private int currentPathIndex;


    public Enemy(World world) {
        this(world, new ArrayList<>(), 0f, 0f);
    }

    // TODO: Other designs
    public Enemy(World world, List<Vector2> wallList, float x, float y) {
        super(world);
        this.speed = 150f;
        frameWidth = 16;
        frameHeight = 16;
        count = 0;
        this.currentPathIndex = 0;
        this.path = new ArrayList<>();


        int animationFrames = 3;

        this.texture = new Texture("mobs.png");
        this.createBody(x, y);

        FixtureDef fd = new FixtureDef();
        fd.isSensor = true;

        CircleShape shape = new CircleShape();
        shape.setRadius(DETECTION_RADIUS);
        fd.shape = shape;

        this.body.createFixture(fd).setUserData(this);

        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);
        // Add all frames to the animation

        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(this.texture, col * frameWidth, 0 * frameHeight, frameWidth, frameHeight));
        }
        this.walkTypesAnimationMap.put(WalkDirection.DOWN, new Animation<>(0.1f, walkFrames));
        walkFrames.clear();
        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(this.texture, col * frameWidth, 1 * frameHeight, frameWidth, frameHeight));
        }
        this.walkTypesAnimationMap.put(WalkDirection.LEFT, new Animation<>(0.1f, walkFrames));
        walkFrames.clear();
        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(this.texture, col * frameWidth, 2 * frameHeight, frameWidth, frameHeight));
        }
        this.walkTypesAnimationMap.put(WalkDirection.RIGHT, new Animation<>(0.1f, walkFrames));
        walkFrames.clear();
        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(this.texture, col * frameWidth, 3 * frameHeight, frameWidth, frameHeight));
        }
        this.walkTypesAnimationMap.put(WalkDirection.UP, new Animation<>(0.1f, walkFrames));
        walkFrames.clear();

        int width = (int) wallList.stream().filter(vector2 -> vector2.y == 0f).max(Comparator.comparing(vector2 -> vector2.x)).orElseThrow().x;
        int height = (int) wallList.stream().filter(vector2 -> vector2.x == 0f).max(Comparator.comparing(vector2 -> vector2.y)).orElseThrow().y;


        mp = new Grid(width + 1, height + 1);

        for (Vector2 vector2 : wallList) {
            mp.setObstacle((int) vector2.x, (int) vector2.y, true);


        }


        al = new ArrayList<>();


    }


    /**
     * Starts moving in defined direction.
     * Should NOT move in two directions. e.g. left and up.
     *
     * @param direction Direction to move.
     */
    @Override
    public void startMoving(WalkDirection direction) {
        this.updateStateAndDirection(State.WALKING, direction);
    }

    /**
     * Stops moving for one direction.
     * For example if Object moves left, but was overridden, it does not stop if it should stop moving right.
     *
     * @param direction Direction to stop moving.
     */
    @Override
    public void stopMoving(WalkDirection direction) {
        this.updateStateAndDirection(State.STILL, direction);
        this.body.setLinearVelocity(0f, 0f);
    }

    /**
     * Renders the appearence of the game object
     *
     * @param spriteBatch
     */
    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin(); // Important to call this before drawing anything
        spriteBatch.draw(
                this.getTexture(),
                this.getPosition().x * PPM - (this.frameWidth / 2f),
                this.getPosition().y * PPM - (this.frameHeight / 2f)
        );
        spriteBatch.end();
    }


    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        this.texture.dispose();
        this.world.destroyBody(body);
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
        }

        if (isFollowing) {
            al = AStar.findPath(mp, player.getPosition().scl(0.5f), this.getPosition().scl(0.5f));
            setPath(al);

            if (path.size() >= 2 && !path.isEmpty()) {
                Node nextNode = path.get(currentPathIndex);
                Vector2 targetPosition = nextNode.getPosition().cpy();
                moveTowards(targetPosition);
            } else if (isFollowing) {
                this.moveTowards(player.getPosition().cpy().scl(0.5f));
            } else {
                stopMoving(this.walkDirectionList.get(0));
            }
        } else {
            this.isFollowing = false;
            this.path.clear();
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
        if (path.size() < 2 || path.isEmpty()) {

//            this.body.setLinearVelocity(0f, 0f); // Stop the enemy
//            updateStateAndDirection(State.STILL, WalkDirection.DOWN); // Update state and walk direction accordingly

            if (distanceToTarget <= nodeProximityThreshold || distanceToTarget >= nodeDistanceThreshold) {
                this.isFollowing = false;
            } else {
                this.isFollowing = true;

            }
        } else {
//            this.isFollowing = false;
        }


        //Gdx.app.log("Targ Pos:", "" + targetPosition);


        // Check if the enemy is close to the next node


        // Normalize and scale the direction vector by the speed
        //Gdx.app.log("Targ Pos 2:", "" + targetPosition);

        Vector2 velocity = directionToTarget.nor().scl(this.speed / PPM);
        //Gdx.app.log("Targ Vel:", "" + velocity);


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
    private void updateStateAndDirection(State state, WalkDirection direction) {
        this.state = state;
        this.walkDirectionList = new ArrayList<>();
        this.walkDirectionList.add(direction);
    }

    /**
     * Sets the path it should follow the start node
     *
     * @param path {@link List<Node>} it should follow.
     */

    public void setPath(List<Node> path) {
        this.path = path;
        this.currentPathIndex = path.size() - 2;
    }
}
