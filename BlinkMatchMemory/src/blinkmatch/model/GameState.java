package blinkmatch.model;

// Tracks the current state of a game session.
// ENCAPSULATION: internal state is hidden; consumers call high-level methods.

public class GameState {

    private int     timeRemaining;
    private final int totalPairs;
    private int     matchedPairs;
    private boolean running;


    public GameState(int startTime, int totalPairs) {
        this.timeRemaining = startTime;
        this.totalPairs    = totalPairs;
        this.matchedPairs  = 0;
        this.running       = false; 
    }

    // Getters
    public int     getTimeRemaining()  { 
        return timeRemaining; 
    }
    public int     getTotalPairs()     { 
        return totalPairs; 
    }
    public int     getMatchedPairs()   { 
        return matchedPairs; 
    }
    public boolean isRunning()         { 
        return running; 
    }

    // Setters and Mutators
    public void setTimeRemaining(int t)  { 
        this.timeRemaining = t; 
    }
    public void setRunning(boolean r)    { 
        this.running = r; 
    }
    public void decrementTime()          { 
        this.timeRemaining--; 
    }
    public void incrementMatchedPairs()  { 
        this.matchedPairs++; 
    }

    // State Checks
    public boolean isComplete() { 
        return matchedPairs >= totalPairs; 
    }
    public boolean isTimeUp()   { 
        return timeRemaining <= 0; 
    }

    public void reset(int startTime) {
        this.timeRemaining = startTime;
        this.matchedPairs  = 0;
        this.running       = false;
    }
}
