package blinkmatch.weather;

/**
 * Abstract base for all weather types.
 * ABSTRACTION: defines the contract every weather must fulfil without
 *              exposing how each type implements the behaviour.
 */
public abstract class Weather {

    public abstract String  getName();
    public abstract String  getIcon();
    public abstract String  getEffect();
    public abstract boolean causesShuffle();

    /** Convenience: returns "Icon Name" for HUD display. */
    public String getDisplayText() {
        return getIcon() + " " + getName();
    }

    @Override
    public String toString() { return getName(); }
}
