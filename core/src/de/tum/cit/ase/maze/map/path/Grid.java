package de.tum.cit.ase.maze.map.path;

import com.badlogic.gdx.math.Vector2;
import de.tum.cit.ase.maze.map.path.Node;

import java.util.ArrayList;
import java.util.List;

public class Grid {
    private Node[][] grid;
    private int width, height;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Node[width][height];

        // Initialize nodes
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Node(new Vector2(x, y), false);
            }
        }

        // Initialize neighbors
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y].setNeighbors(calculateNeighbors(x, y));
            }
        }
    }

    private List<Node> calculateNeighbors(int x, int y) {
        List<Node> neighbors = new ArrayList<>();
        int[] dx = {0, 0, 1, -1}; // Horizontal displacements
        int[] dy = {1, -1, 0, 0}; // Vertical displacements

        for (int i = 0; i < 4; i++) {
            int checkX = x + dx[i];
            int checkY = y + dy[i];

            if (checkX >= 0 && checkX < width && checkY >= 0 && checkY < height) {
                neighbors.add(grid[checkX][checkY]);
            }
        }

        return neighbors;
    }

    // Method to set obstacles
    public void setObstacle(int x, int y, boolean isObstacle) {
        grid[x][y].setObstacle(isObstacle);
    }

    // Get the node from the grid
    public Node getNode(int x, int y) {
        return grid[x][y];
    }

    public Node[][] getGrid() {
        return grid;
    }

    public void setGrid(Node[][] grid) {
        this.grid = grid;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}

