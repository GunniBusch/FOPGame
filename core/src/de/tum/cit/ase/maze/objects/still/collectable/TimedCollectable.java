package de.tum.cit.ase.maze.objects.still.collectable;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.maze.objects.dynamic.Player;

import java.util.Objects;

/**
 * A {@link TimedCollectable} is a {@link Collectable}, that applies an effect to the {@link Player} for a specified time. After that the effect is removed.
 */
public abstract class TimedCollectable extends Collectable {
    /**
     * Duration of the effect in seconds
     */
    protected float duration;
    protected float elapsedTIme = 0f;
    private boolean isDisposed = false;

    public TimedCollectable(Vector2 position, World world, RayHandler rayHandler, TextureAtlas textureAtlas) {
        super(position, world, rayHandler, textureAtlas);

    }

    /**
     * Applies the effect to the player. The effect is restored by {@link TimedCollectable#restore(Player)}
     *
     * @param player the {@link Player} to apply the effect to.
     */
    protected abstract void apply(Player player);

    /**
     * Restores the changes made by {@link TimedCollectable#apply}
     */
    public abstract void restore(Player player);

    /**
     * Gets Duration and Elapsed time.
     *
     * @return Array of size 2 with first the elapsed time and the duration;
     */
    public Float[] getDurationAndElapsed() {
        return new Float[]{
                MathUtils.clamp(MathUtils.round(elapsedTIme), 0, duration),
                duration
        };
    }

    /**
     * @param deltaTime Time since last frame.
     */
    @Override
    public void update(float deltaTime) {
        if (!active) {
            this.elapsedTIme += deltaTime;
            if (elapsedTIme >= duration) {
                this.removable = true;
            }
            if (!this.isDisposed) {
                this.remove();
            }
        }

    }


    @Override
    public void collect(Player player) {
        if (player.addCollectable(this)) {
            active = false;
            apply(player);
        }
    }

    /**
     *
     */
    @Override
    public void remove() {
        if (!this.isDisposed) {

            if (body != null) {
                world.destroyBody(body);
                body = null;
            }
            light.remove();
            this.dispose();
            this.isDisposed = true;
        }
    }

    @Override
    public void dispose() {
        if (!isDisposed) {
            this.isDisposed = true;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimedCollectable)) return false;
        return this.getClass().equals(o.getClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + MathUtils.clamp(MathUtils.round(elapsedTIme), 0, duration) + " / " + MathUtils.round(duration);
    }
}
