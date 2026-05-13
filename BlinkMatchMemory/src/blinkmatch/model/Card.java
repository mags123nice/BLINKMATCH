package blinkmatch.model;

/**
 * Represents a single card on the game board.
 * ENCAPSULATION: all fields are private, accessed only via getters/setters.
 */
public class Card {

    private final int id;
    private final String symbol;
    private boolean faceUp;
    private boolean matched;

    public Card(int id, String symbol) {
        this.id     = id;
        this.symbol = symbol;
        this.faceUp = false;
        this.matched = false;
    }

    // ---------- getters ----------
    public int     getId()       { return id; }
    public String  getSymbol()   { return symbol; }
    public boolean isFaceUp()    { return faceUp; }
    public boolean isMatched()   { return matched; }

    // ---------- setters ----------
    public void setFaceUp(boolean faceUp)   { this.faceUp  = faceUp; }
    public void setMatched(boolean matched) { this.matched = matched; }

    // ---------- helpers ----------
    public void flip()  { this.faceUp = !this.faceUp; }
    public void reset() { this.faceUp = false; this.matched = false; }
}
