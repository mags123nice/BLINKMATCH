package blinkmatch;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundPlayer {
    
    public static void playClickEffect()
    {
        try {         
            Clip sound;
            AudioInputStream audio = AudioSystem.getAudioInputStream(
                    new File("resources/music/clickEffect.wav"));
            sound = AudioSystem.getClip();
            
            sound.open(audio);
            sound.loop(0);
            sound.start();

        } catch (Exception e) {
        }


    }
}
