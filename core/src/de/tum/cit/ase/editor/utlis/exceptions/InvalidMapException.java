package de.tum.cit.ase.editor.utlis.exceptions;

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
