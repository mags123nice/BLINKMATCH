package blinkmatch.weather;

public class StormyWeather extends Weather {

    @Override public String  getName()         { return "Stormy"; }
    @Override public String  getIcon()         { return "\u26c8"; }   
    @Override public String  getEffect()       { return "Cards will shuffle!"; }
    @Override public boolean causesShuffle()   { return true; }
}
