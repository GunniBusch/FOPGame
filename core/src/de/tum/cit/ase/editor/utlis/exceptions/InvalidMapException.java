package de.tum.cit.ase.editor.utlis.exceptions;

/**
 * Exception thrown when an invalid map is encountered in the Maze Game Editor.
 */
public class InvalidMapException extends MazeGameEditorException {
    public InvalidMapException() {
    }

    public InvalidMapException(String message) {
        super(message);
    }

    public InvalidMapException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMapException(Throwable cause) {
        super(cause);
    }
}
