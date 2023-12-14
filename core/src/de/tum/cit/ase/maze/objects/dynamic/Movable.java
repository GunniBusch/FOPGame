package de.tum.cit.ase.maze.objects.dynamic;


/**
 * Defines if an Object is Dynamic e.g. can move.
 */
public interface Movable {
    /**
     * Moves the Object
     */
    void updateMotion();

    void setLeftMove(boolean move);

    void setRightMove(boolean move);

    void setDownMove(boolean move);

    void setUpMove(boolean move);


}
