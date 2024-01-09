package de.tum.cit.ase.maze.objects;

import de.tum.cit.ase.maze.utils.exceptions.ObjectTypeException;

public enum ObjectType {
    Wall(0), EntryPoint(1), Exit(2), Trap(3), Enemy(4), Key(5), Player(6);
    public final int number;

    ObjectType(int number) {
        this.number = number;
    }

    public static ObjectType valueOfLabel(int number) throws ObjectTypeException {
        for (ObjectType e : values()) {
            if (e.number == number) {
                return e;
            }
        }
        throw new ObjectTypeException();
    }

}
