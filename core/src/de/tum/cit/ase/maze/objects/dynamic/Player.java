package de.tum.cit.ase.maze.objects.dynamic;

import box2dLight.PointLight;
import box2dLight.PositionalLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.ase.maze.Input.DeathListener;
import de.tum.cit.ase.maze.objects.ObjectType;
import de.tum.cit.ase.maze.objects.still.Key;
import de.tum.cit.ase.maze.objects.still.collectable.TimedCollectable;
import de.tum.cit.ase.maze.utils.MapLoader;

import java.util.*;

import static de.tum.cit.ase.maze.utils.CONSTANTS.*;

/**
 * Class represents the Player. The player is the main character that can be controlled by a person.
 */
public class Player extends Character implements Movable {
    public final float SPRINT_BOOST = 1.8f;
    public final int numberOfKeys;
    private final int RAYS_NUM = 200;
    private final Set<TimedCollectable> timedCollectables;
    private final float lightDistance = 15f;
    private final RayHandler rayHandler;
    private final PositionalLight light;
    private final List<Key> keyList;
    Music soundEffects;
    /**
     * Marks if game is finished
     */
    private boolean isFinished = false;
    private boolean isSprint = false;
    private boolean isVulnerable = true;
    private boolean isCooldown = true;
    private float timeCount;


    public Player(World world, DeathListener deathListener, RayHandler rayHandler) {
        this(world, deathListener, rayHandler, 0, 0);
    }

    /**
     * Creates a Player with respective coordinates.
     *
     * @param x X-Coordinate
     * @param y Y-Coordinate
     */
    public Player(World world, DeathListener deathListener, RayHandler rayHandler, float x, float y) {
        super(world, deathListener);
        keyList = new ArrayList<>();
        numberOfKeys = MapLoader.getMapCoordinates(ObjectType.Key).size();
        this.timedCollectables = new HashSet<>();
        this.rayHandler = rayHandler;
        this.light = new PointLight(rayHandler, RAYS_NUM, new Color(1, 1, 1, 0.89f), lightDistance * 2, x, y);
        light.setSoftnessLength(1.5f);
        var filter = new Filter();
        light.setSoft(true);
        filter.groupIndex = IGNORE_GROUP_BIT;
        filter.categoryBits = LIGHT_BIT;
        this.light.setContactFilter(filter);
        this.health = PLAYER_MAX_HEALTH;
        this.speed = 150f;
        frameWidth = 16;
        frameHeight = 32;

        int animationFrames = 4;

        this.texture = new Texture("character.png");
        this.createBody(x, y);
        var playerFilter = new Filter();
        playerFilter.categoryBits = PLAYER_BIT;
        this.body.getFixtureList().get(0).setFilterData(playerFilter);
        this.light.attachToBody(body);
        this.light.setActive(true);

        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);
        // Add all frames to the animation
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < animationFrames; col++) {
                walkFrames.add(new TextureRegion(this.texture, col * frameWidth, row * frameHeight, frameWidth, frameHeight));
            }
            this.walkTypesAnimationMap.put(WalkDirection.values()[row], new Animation<>(0.1f, walkFrames));
            walkFrames.clear();
        }

    }

    public Player(World world, DeathListener deathListener, RayHandler rayHandler, Vector2 position) {
        this(world, deathListener, rayHandler, position.x, position.y);
    }


    public boolean addCollectable(TimedCollectable collectable) {
        soundEffects = Gdx.audio.newMusic(Gdx.files.internal("coin-upaif-14631.mp3"));
        soundEffects.play();
        return timedCollectables.add(collectable);
    }


    /**
     * @param deltaTime Time since last frame.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.timedCollectables.stream().filter(TimedCollectable::isRemovable).forEach(collectable -> collectable.restore(this));
        this.timedCollectables.removeIf(TimedCollectable::isRemovable);
        timeCount += deltaTime;
        // Create a Random object
        Random random = new Random();
        // Generate a random integer between 1 (included) and 2 (included)
        int randomInt = random.nextInt(2) + 1;

        if (timeCount >= 3) {
            //randomly if coolDown needed or not -> luck = player's swordSkills
            if (randomInt == 1) {
                isCooldown = false;
            } else {
                isCooldown = true;
            }
        }

    }

    /**
     * @param damage damage to apply
     */
    @Override
    public void makeDamage(int damage) {
        if (!isFinished && isVulnerable) {
            super.makeDamage(damage);
            soundEffects = Gdx.audio.newMusic(Gdx.files.internal("ough-47202.mp3"));
            soundEffects.play();
        }
    }

    /**
     * Method to attack enemies
     */
    public void attack(int damage) {
        if (!isCooldown) {
            soundEffects = Gdx.audio.newMusic(Gdx.files.internal("sword-slash-and-swing-185432.mp3"));
            soundEffects.play();
        }

    }

    /**
     * Starts moving in defined direction.
     * Should NOT move in two directions. e.g. left and up. But after release other pressed buttons take over.
     *
     * @param direction Direction to move.
     */
    @Override
    public synchronized void startMoving(WalkDirection direction) {
        switch (this.state) {
            case STILL -> {
                this.state = State.WALKING;
                this.walkDirectionList = new ArrayList<>();
                this.walkDirectionList.add(direction);
            }
            case WALKING -> this.walkDirectionList.add(direction);
        }

    }

    /**
     * Stops moving for one direction.
     * For example if Object moves left, but was overridden, it does not stop if it should stop moving right.
     * And also allows after stopping to move into other requested directions.
     *
     * @param direction Direction to stop moving.
     */
    @Override
    public synchronized void stopMoving(WalkDirection direction) {
        this.walkDirectionList.remove(direction);
        if (this.walkDirectionList.isEmpty()) {
            this.state = State.STILL;
            this.walkDirectionList = new ArrayList<>();
            this.walkDirectionList.add(direction);

            this.body.setLinearVelocity(0, 0);
        }
    }

    public void collectKey(Key key) {
        keyList.add(key);
        soundEffects = Gdx.audio.newMusic(Gdx.files.internal("fantasy_ui_button_6-102219.mp3"));
        soundEffects.play();
    }

    public Set<TimedCollectable> getTimedCollectables() {
        return timedCollectables;
    }

    public void markAsFinished() {
        this.isFinished = true;
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        this.texture.dispose();
    }

    /**
     * Renders the appearance of the Player
     *
     * @param spriteBatch
     */
    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(
                this.getTexture(),
                this.getPosition().x * PPM - (this.getTexture().getRegionWidth() * ZOOM / 2f),
                this.getPosition().y * PPM - (this.getTexture().getRegionHeight() * ZOOM / 2f),
                this.getTexture().getRegionWidth() * ZOOM, this.getTexture().getRegionHeight() * ZOOM
        );

    }

    public boolean isVulnerable() {
        return isVulnerable;
    }

    public void setVulnerable(boolean vulnerable) {
        isVulnerable = vulnerable;
    }

    public List<Key> getKeyList() {
        return keyList;
    }

    public synchronized boolean isSprint() {
        return isSprint;
    }


    /**
     * Requests elevated Speed
     *
     * @param sprint
     */
    public synchronized void setSprint(boolean sprint) {
        if (sprint == this.isSprint()) return;
        else isSprint = sprint;
        if (sprint) {
            speed *= SPRINT_BOOST;
        } else {
            speed /= SPRINT_BOOST;
        }
    }


}