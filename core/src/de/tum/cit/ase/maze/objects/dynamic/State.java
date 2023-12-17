package de.tum.cit.ase.maze.objects.dynamic;

import org.checkerframework.checker.nullness.qual.*;

import java.util.ArrayList;

/**
 * States whether an object walks or stands Still.
 */
public enum State {
    STILL, WALKING;

    /**
     * List of Directions the player is and wants to be. Last Entry actual state, and others are overridden states.
     */
    private @NonNull ArrayList<WalkDirection> direction;

    State() {
        direction = new ArrayList<>();
        direction.add(WalkDirection.DOWN);
    }

    public static @NonNull State WALKING(@NonNull WalkDirection direction) {
        var state = State.WALKING;
        state.setDirection(direction);
        return state;
    }

    public static @NonNull State STILL(@NonNull WalkDirection direction) {
        var state = State.STILL;
        state.setDirection(direction);
        return state;
    }


    public ArrayList<WalkDirection> getDirection() {
        return this.direction;
    }

    public void setDirection(WalkDirection direction) {
        this.direction = new ArrayList<>();
        this.direction.add(direction);
    }

    public void addDirection(WalkDirection direction) {
        this.direction.add(direction);
    }

    public void removeDirection(@NonNull WalkDirection direction) {
        this.direction.removeIf(direction1 -> direction1.equals(direction));
    }
}
