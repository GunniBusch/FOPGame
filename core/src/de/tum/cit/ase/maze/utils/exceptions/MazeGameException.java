package de.tum.cit.ase.maze.utils.exceptions;

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