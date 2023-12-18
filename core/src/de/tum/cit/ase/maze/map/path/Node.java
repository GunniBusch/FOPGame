package de.tum.cit.ase.maze.map.path;

import com.badlogic.gdx.math.Vector2;

import java.util.List;

/**
 * Node representing a "point" in a {@link Grid}.
 */
public class Node {
    /**
     * Position of the node in the World
     */
    private Vector2 position;
    /**
     * Defines if this Node can be traversed or not
     */
    private boolean isObstacle;
    private float gCost, hCost, fCost;
    /**
     * The parent Node
     */
    private Node parent;
    /**
     * {@link List} of neighboring nodes.
     */
    private List<Node> neighbors;

    public Node(Vector2 position, boolean isObstacle) {
        this.position = position;
        this.isObstacle = isObstacle;
        this.neighbors = null; // Initially, neighbors are not known
    }

    public void calculateFCost() {
        this.fCost = this.gCost + this.hCost;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public boolean isObstacle() {
        return isObstacle;
    }

    public void setObstacle(boolean obstacle) {
        isObstacle = obstacle;
    }

    public float getgCost() {
        return gCost;
    }

    public void setgCost(float gCost) {
        this.gCost = gCost;
    }

    public float gethCost() {
        return hCost;
    }

    public void sethCost(float hCost) {
        this.hCost = hCost;
    }

    public float getfCost() {
        return fCost;
    }

    public void setfCost(float fCost) {
        this.fCost = fCost;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<Node> neighbors) {
        this.neighbors = neighbors;
    }
}