package de.tum.cit.ase.maze.objects;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import de.tum.cit.ase.maze.objects.still.collectable.Collectable;
import de.tum.cit.ase.maze.objects.still.collectable.TimedCollectable;
import de.tum.cit.ase.maze.utils.MapLoader;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages collectables like {@link Collectable} or {@link TimedCollectable}
 */
public class CollectableManager implements Disposable {
    public final float RESPAWN_TIME = .1f * 60;
    private final World world;
    private final RayHandler rayHandler;
    private final Array<Vector2> spawnablePoints;
    private final Map<Class<? extends Collectable>, Integer> spawnMap;
    private final List<Collectable> collectableList;
    public boolean canRespawn = false;
    private float time;
    private boolean scheduledRespawn = false;
    private Timer timer;
    private RespawnTask respawnTask;

    public CollectableManager(World world, RayHandler rayHandler, boolean canRespawn) {
        this.world = world;
        this.canRespawn = true;
        this.rayHandler = rayHandler;
        this.collectableList = new ArrayList<>();
        this.spawnablePoints = new Array<>(false, 16);
        this.spawnMap = new HashMap<>();
        for (int i = 0; i < MapLoader.height + 1; i++) {
            for (int j = 0; j < MapLoader.width + 1; j++) {
                this.spawnablePoints.add(new Vector2(j, i));
            }
        }
        for (ObjectType objectType : ObjectType.values()) {
            this.spawnablePoints.removeAll(new Array<>(MapLoader.getMapCoordinates(objectType).toArray(new Vector2[0])), false);
        }


        if (canRespawn) {
            timer = new Timer();
            respawnTask = new RespawnTask();
            timer.scheduleTask(respawnTask, RESPAWN_TIME, RESPAWN_TIME);

        }
    }

    /**
     * Updates the collectables
     *
     * @param dt delta time. See {@link Graphics#getDeltaTime()}
     */

    public void update(float dt) {
        if (scheduledRespawn) respawn();

        collectableList.forEach(collectable -> collectable.update(dt));
        collectableList.removeIf(Collectable::isRemovable);


    }

    /**
     * Renders all {@link Collectable Collectables}.
     *
     * @param spriteBatch the {@link SpriteBatch} to render
     */
    public void render(SpriteBatch spriteBatch) {
        collectableList.forEach(collectable -> collectable.render(spriteBatch));

    }

    /**
     * Spawns {@link Collectable Collectables}
     *
     * @param collectableClass the class of the {@link Collectable} to spawn
     * @param amount           amount to spawn
     */
    public final void spawn(@NonNull Class<? extends Collectable> collectableClass, int amount) {
        this.spawnMap.put(collectableClass, amount);
        try {

            if (spawnablePoints.size >= amount) {
                for (int i = 0; i < amount; i++) {
                    var spawnPoint = spawnablePoints.random();
                    collectableList.add(collectableClass.getConstructor(Vector2.class, World.class, RayHandler.class).newInstance(spawnPoint, world, rayHandler));
                    spawnablePoints.removeValue(spawnPoint, false);

                }
            }
        } catch (ReflectiveOperationException e) {
            Gdx.app.error("Collectable Manager", "Could not load collectable " + collectableClass.getTypeName(), e);
        }

    }

    /**
     * Spawns {@link Collectable Collectables}
     *
     * @param collectableClass the class of the {@link Collectable} to spawn
     * @param areaToCover      percentage of the area the will be covered
     */
    public final void spawn(@NonNull Class<? extends Collectable> collectableClass, float areaToCover) {
        this.spawn(collectableClass, MathUtils.round(spawnablePoints.size * MathUtils.clamp(areaToCover, MathUtils.FLOAT_ROUNDING_ERROR, 1)));
    }

    /**
     * Schedules a respawn. Called by the {@link RespawnTask respawn task}.
     */
    protected synchronized final void scheduleRespawn() {
        this.scheduledRespawn = true;

    }

    /**
     * Respawns the {@link Collectable collectables}.
     */
    private void respawn() {
        this.collectableList.forEach(Collectable::remove);
        this.collectableList.removeIf(Collectable::isActive);
        this.spawnMap.forEach(this::spawn);
        this.scheduledRespawn = false;
    }

    public Timer getTimer() {
        return timer;
    }

    public RespawnTask getRespawnTask() {
        return respawnTask;
    }

    @Override
    public void dispose() {
        this.collectableList.forEach(Collectable::dispose);
        this.timer.stop();
    }

    /**
     * Task that schedules a respawn by a {@link Timer}
     */
    public class RespawnTask extends Timer.Task {
        /**
         * Runs the commands to initiate a respawn
         */
        @Override
        public void run() {
            scheduleRespawn();
            Gdx.app.debug("Collectable manager", "Respawned collectables");
        }

        /**
         * Calculates when the next respawn is executed.
         *
         * @return time in seconds
         */
        public float getTimeToExecutionInSeconds() {
            return RESPAWN_TIME - (this.getExecuteTimeMillis() - (System.nanoTime() / 1000000f)) / 1000f;
        }
    }
}
