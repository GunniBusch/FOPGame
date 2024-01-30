package de.tum.cit.ase.maze.utils.exceptions;

/**
 * The ObjectTypeException class represents an exception that occurs when there is an error with object types in the Maze Game.
 * It is a subclass of the MazeGameException class.
 */
public class ObjectTypeException extends MazeGameException {
    public ObjectTypeException() {
    }

    public ObjectTypeException(String message) {
        super(message);
    }
}
