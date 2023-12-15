package de.tum.cit.ase.maze.objects.dynamic;

import org.checkerframework.checker.nullness.qual.*;

/**
 * States whether an object walks or stands Still.
 */
public enum State{
    STILL, WALKING;

    private WalkDirection direction = null;

    public static @NonNull State WALKING(@NonNull WalkDirection direction) {
        var state = State.WALKING;
        state.setDirection(direction);
        return state;
    }


    public WalkDirection getDirection() {
        return direction;
    }

    public void setDirection(WalkDirection direction) {
        this.direction = direction;
    }
}
