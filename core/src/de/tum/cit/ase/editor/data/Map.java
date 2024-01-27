package de.tum.cit.ase.editor.data;

import de.tum.cit.ase.editor.utlis.TileTypes;

/**
 * Represents a map with a name and a grid of tile types.
 */
public record Map(String name, TileTypes[][] map) {
}
