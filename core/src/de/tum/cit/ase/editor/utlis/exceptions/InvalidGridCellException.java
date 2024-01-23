package de.tum.cit.ase.editor.utlis.exceptions;

public class InvalidGridCellException extends RuntimeException {
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