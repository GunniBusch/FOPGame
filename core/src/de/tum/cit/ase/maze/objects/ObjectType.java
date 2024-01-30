package de.tum.cit.ase.maze.objects;

import de.tum.cit.ase.maze.utils.exceptions.ObjectTypeException;

/**
 * Represents different types of objects in a game.
 */
public enum ObjectType {
    Wall(0), EntryPoint(1), Exit(2), Trap(3), Enemy(4), Key(5), Player(6);
    public final int number;

    /**
     * Initializes a new instance of the {@code ObjectType} class.
     *
     * @param number the number associated with the object type
     */
    ObjectType(int number) {
        this.number = number;
    }

    /**
     * Returns the ObjectType corresponding to the given label number.
     *
     * @param number The label number of the ObjectType.
     * @return The corresponding ObjectType.
     * @throws ObjectTypeException if no ObjectType with the given label number is found.
     */
    public static ObjectType valueOfLabel(int number) throws ObjectTypeException {
        for (ObjectType e : values()) {
            if (e.number == number) {
                return e;
            }
        }
        throw new ObjectTypeException();
    }

}
