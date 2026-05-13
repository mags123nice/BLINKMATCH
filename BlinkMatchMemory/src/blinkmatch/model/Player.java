package blinkmatch.model;

/**
 * Holds all player-related data.
 * ENCAPSULATION: score and moves can only be changed through controlled methods.
 */
public class Player {

    private final String name;
    private int score;
    private int moves;

    public Player(String name) {
        this.name  = name;
        this.score = 0;
        this.moves = 0;
    }

    // ---------- getters ----------
    public String getName()  { return name; }
    public int    getScore() { return score; }
    public int    getMoves() { return moves; }

    // ---------- controlled mutators ----------
    public void addScore(int points)  { this.score += points; }
    public void incrementMoves()      { this.moves++; }

    public void reset() {
        this.score = 0;
        this.moves = 0;
    }
}
