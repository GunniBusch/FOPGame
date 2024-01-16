package de.tum.cit.ase.maze.objects;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import de.tum.cit.ase.maze.objects.still.collectable.Collectable;
import de.tum.cit.ase.maze.utils.MapLoader;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectableManager implements Disposable {
    private final World world;
    private final RayHandler rayHandler;
    public final float RESPAWN_TIME = 5f * 60;
    private final Array<Vector2> spawnablePoints;
    private final Map<Class<? extends Collectable>, Integer> spawnMap;
    public boolean canRespawn = false;
    private final List<Collectable> collectableList;
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
        this.spawnablePoints.removeAll(new Array<>(MapLoader.getMapCoordinates(ObjectType.Wall).toArray(new Vector2[0])), false);

        if (canRespawn) {
            timer = new Timer();
            respawnTask = new RespawnTask();
            timer.scheduleTask(respawnTask, RESPAWN_TIME, RESPAWN_TIME);

        }
    }

    public void update(float dt) {
        if (scheduledRespawn) respawn();

        collectableList.forEach(collectable -> collectable.update(dt));
        collectableList.removeIf(Collectable::isRemovable);


    }

    public void render(SpriteBatch spriteBatch) {
        collectableList.forEach(collectable -> collectable.render(spriteBatch));

    }

    public final <T extends Collectable> void spawn(@NonNull Class<T> collectableClass, int ammount) {
        this.spawnMap.put(collectableClass, ammount);
        try {
            Vector2 spawnPoint;
            if (spawnablePoints.size >= ammount) {
                for (int i = 0; i < ammount; i++) {
                    spawnPoint = spawnablePoints.random();
                    spawnablePoints.removeValue(spawnPoint, false);

                    collectableList.add(collectableClass.getConstructor(Vector2.class, World.class, RayHandler.class).newInstance(spawnPoint, world, rayHandler));
                }
            }
        } catch (ReflectiveOperationException e) {
            Gdx.app.error("Collectables", e.getMessage() + " : " + "Could not load collectable " + collectableClass.getTypeName());
            e.printStackTrace();
        }

    }

    public final <T extends Collectable> void spawn(@NonNull Class<T> collectableClass, float areaToCover) {
        this.spawn(collectableClass, MathUtils.round(spawnablePoints.size * MathUtils.clamp(areaToCover, MathUtils.FLOAT_ROUNDING_ERROR, 1)));
    }

    protected synchronized final void setRespawn() {
        this.scheduledRespawn = true;

    }

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

    /**
     *
     */
    @Override
    public void dispose() {
        this.collectableList.forEach(Collectable::dispose);
        this.timer.stop();
    }

    public class RespawnTask extends Timer.Task {
        /**
         * Runs the commands to initiate a respawn
         */
        @Override
        public void run() {
            setRespawn();
            Gdx.app.debug("Collectable manager", "Respawned collectables");
        }

        public float getTimeToExecutionInSeconds() {
            return RESPAWN_TIME - (this.getExecuteTimeMillis() - (System.nanoTime() / 1000000f)) / 1000f;
        }
    }
}
