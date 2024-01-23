package de.tum.cit.ase.editor.utlis.exceptions;

import de.tum.cit.ase.maze.utils.exceptions.MazeGameException;

public abstract class MazeGameEditorException extends MazeGameException {
    public MazeGameEditorException() {
    }

    public MazeGameEditorException(String message) {
        super(message);
    }

    public MazeGameEditorException(String message, Throwable cause) {
        super(message, cause);
    }

    public MazeGameEditorException(Throwable cause) {
        super(cause);
    }

    protected MazeGameEditorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}