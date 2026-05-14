package blinkmatch.weather;

public abstract class Weather {

    public abstract String  getName();
    public abstract String  getIcon();
    public abstract String  getEffect();
    public abstract boolean causesShuffle();

    public String getDisplayText() {
        return getIcon() + " " + getName();
    }

    @Override
    public String toString() { return getName(); }
}
