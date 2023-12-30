package de.tum.cit.ase.maze.map;

import com.badlogic.gdx.math.Vector2;
import de.tum.cit.ase.maze.map.path.Grid;
import de.tum.cit.ase.maze.map.path.Node;

import java.util.*;

/**
 * Class, that finds a path in a {@link Grid} using the <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">A* Algorithm</a>
 */
public class AStar {
    /**
     * Finds the path between nodes.
     *
     * @param grid  {@link Grid} that defines all nodes e.g. if there is a wall or not
     * @param start {@link Vector2} that defines the start position in the grid. Gets converted to a node
     * @param end   {@link Vector2} that defines the end position in the grid. Gets converted to a node
     * @return List of nodes that represent the found path between start and end in the grid
     */
    public static List<Node> findPath(Grid grid, Vector2 start, Vector2 end) {
        Node startNode = getNodeFromVector(grid, start);
        Node endNode = getNodeFromVector(grid, end);

        PriorityQueue<Node> openList = new PriorityQueue<>();
        Set<Node> closedList = new HashSet<>();

        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();

            closedList.add(currentNode);

            if (currentNode.equals(endNode)) {
                return retracePath(startNode, endNode);
            }

            for (Node neighbor : getNeighbors(currentNode)) {
                if (neighbor.isObstacle() || closedList.contains(neighbor)) continue;

                float newCostToNeighbor = currentNode.getgCost() + getDistance(currentNode, neighbor);
                if (newCostToNeighbor < neighbor.getgCost() || !openList.contains(neighbor)) {
                    neighbor.setgCost(newCostToNeighbor);
                    neighbor.sethCost(getDistance(neighbor, endNode));
                    neighbor.calculateFCost();
                    neighbor.setParent(currentNode);

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>(); // No path found
    }

    /**
     * Retraces the path from the start node to the end node.
     * <p>
     * This method backtracks from the end node to the start node using the parent references
     * in each node. It constructs the path in reverse, from end to start, and then reverses
     * it to get the correct order from start to end. This method is typically called after
     * a pathfinding algorithm like A* has been executed and the end node has been reached.
     *
     * @param startNode The starting {@link Node} of the path.
     * @param endNode   The end {@link Node} of the path.
     * @return A list of nodes representing the path from the start node to the end node.
     * The path is in the correct order, starting with the start node and ending
     * with the end node. If no path is found, an empty list is returned.
     */
    private static List<Node> retracePath(Node startNode, Node endNode) {
        List<Node> path = new ArrayList<>();
        Node currentNode = endNode;

        while (!currentNode.equals(startNode)) {
            path.add(currentNode);
            currentNode = currentNode.getParent();
        }

        Collections.reverse(path);
        return path;
    }

    private static float getDistance(Node nodeA, Node nodeB) {
        return nodeA.getPosition().dst(nodeB.getPosition());
    }


    /**
     * Gets the neighbours of a {@link Node}
     * @param node {@link Node} to get the neighbours from
     * @return Returns a list of all neighbours
     */
    private static List<Node> getNeighbors(Node node) {
        return node.getNeighbors();
    }

    /**
     * Converts a Vector to a node.
     * A vector cant be used for the algorithm. Only a {@link Node} can be.
     * @param grid {@link Grid} the {@link Node} should belong to
     * @param vector {@link Vector2} to be converted
     * @return Node from a vector
     */
    private static Node getNodeFromVector(Grid grid, Vector2 vector) {
        // Rounding the vector to the nearest whole number to find the closest node
        int x = Math.round(vector.x);
        int y = Math.round(vector.y);
        x = Math.max(0, Math.min(x, grid.getWidth() - 1)); // Ensure x is within grid bounds
        y = Math.max(0, Math.min(y, grid.getHeight() - 1)); // Ensure y is within grid bounds
        return grid.getNode(x, y);
    }
}
