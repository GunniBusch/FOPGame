package de.tum.cit.ase.maze.utils.exceptions;

/**
 * An exception that is thrown when there is an error loading a map.
 */
public class MapLoadingException extends MazeGameException {
    public MapLoadingException() {
        super();
    }

    public MapLoadingException(String message) {
        super(message);
    }

    public MapLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapLoadingException(Throwable cause) {
        super(cause);
    }
}
