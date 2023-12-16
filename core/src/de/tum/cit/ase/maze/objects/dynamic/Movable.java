package de.tum.cit.ase.maze.objects.dynamic;

/**
 * Defines if an Object is Dynamic e.g. can move.
 */
public interface Movable {
    /**
     * Moves the Object
     *
     * @param deltaTime Time since last frame.
     */
    void update(float deltaTime);

    /**
     * Starts moving in defined direction.
     * Should NOT move in two directions. e.g. left and up.
     *
     * @param direction Direction to move.
     */
    void startMoving(WalkDirection direction);

    /**
     * Stops moving for one direction.
     * For example if Object moves left, but was overridden, it does not stop if it should stop moving right.
     *
     * @param direction Direction to stop moving.
     */
    void stopMoving(WalkDirection direction);

}
