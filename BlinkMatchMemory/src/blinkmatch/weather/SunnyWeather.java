package blinkmatch.weather;

public class SunnyWeather extends Weather {

    @Override public String  getName()         { return "Sunny"; }
    @Override public String  getIcon()         { return "\u2600"; }   
    @Override public String  getEffect()       { return "Normal gameplay"; }
    @Override public boolean causesShuffle()   { return false; }
}
