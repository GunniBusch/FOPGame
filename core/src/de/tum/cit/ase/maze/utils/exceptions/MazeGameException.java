package de.tum.cit.ase.maze.utils.exceptions;

/**
 * The MazeGameException class represents an exception that occurs in the Maze Game.
 * It is an abstract class that extends RuntimeException.
 */
public abstract class MazeGameException extends RuntimeException {
    public MazeGameException() {
    }

    public MazeGameException(String message) {
        super(message);
    }

    public MazeGameException(String message, Throwable cause) {
        super(message, cause);
    }

    public MazeGameException(Throwable cause) {
        super(cause);
    }

    protected MazeGameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}