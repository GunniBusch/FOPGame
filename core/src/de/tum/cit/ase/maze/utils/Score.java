package de.tum.cit.ase.maze.utils;

/**
 * The Score class represents a score in a game.
 */
public class Score {
    private int score;

    public Score() {
        score = 0;
    }

    public void increaseScore(int points) {
        score += points;
    }


    public int getCurrentScore() {
        return score;
    }

    public void resetScore() {
        score = 0;
    }
    public void setScore(int points) {
        score = score + points;
    }
}
