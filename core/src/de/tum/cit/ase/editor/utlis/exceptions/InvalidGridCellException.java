package de.tum.cit.ase.editor.utlis.exceptions;

/**
 * The InvalidGridCellException class represents an exception that is thrown when an invalid grid cell is accessed.
 * It is a subclass of MazeGameEditorException.
 */
public class InvalidGridCellException extends MazeGameEditorException {
    public InvalidGridCellException() {
    }

    public InvalidGridCellException(String message) {
        super(message);
    }

    public InvalidGridCellException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidGridCellException(Throwable cause) {
        super(cause);
    }
}