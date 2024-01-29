package de.tum.cit.ase.editor.utlis.exceptions;

/**
 * Represents an exception that occurs when reading or validating a map file.
 */
public class InvalidMapFile extends MazeGameEditorException {
    public InvalidMapFile() {
    }

    public InvalidMapFile(String message) {
        super(message);
    }

    public InvalidMapFile(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMapFile(Throwable cause) {
        super(cause);
    }
}
