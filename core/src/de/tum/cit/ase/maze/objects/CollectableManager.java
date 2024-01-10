package de.tum.cit.ase.maze.objects;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.tum.cit.ase.maze.utils.MapLoader;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CollectableManager implements Disposable {
    private final World world;
    private final RayHandler rayHandler;
    private final List<Collectable> collectableList;
    private final Array<Vector2> spawnablePoints;

    public CollectableManager(World world, RayHandler rayHandler) {
        this.world = world;
        this.rayHandler = rayHandler;
        this.collectableList = new ArrayList<>();
        this.spawnablePoints = new Array<>(false, 16);
        for (int i = 0; i < MapLoader.height + 1; i++) {
            for (int j = 0; j < MapLoader.width + 1; j++) {
                this.spawnablePoints.add(new Vector2(j, i));
            }
        }
        this.spawnablePoints.removeAll(new Array<>(MapLoader.getMapCoordinates(ObjectType.Wall).toArray(new Vector2[0])), false);
    }

    public void update(float dt) {
        collectableList.forEach(collectable -> collectable.update(dt));


    }

    public void render(SpriteBatch spriteBatch) {
        collectableList.forEach(collectable -> collectable.render(spriteBatch));

    }

    public final <T extends Collectable> void spawn(@NonNull Class<T> collectableClass, int ammount) {
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

    /**
     *
     */
    @Override
    public void dispose() {
        this.collectableList.forEach(Collectable::dispose);
    }
}
