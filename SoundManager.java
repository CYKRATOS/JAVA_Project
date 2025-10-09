
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundManager {

    private Clip musicClip;
    private static SoundManager instance; // ✅ shared instance

    private SoundManager() {
    } // private constructor

    // ✅ Access point for global instance
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    // --- Play WAV effect (jump, death, door) ---
    public void playSound(String filePath) {
        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) {
                System.out.println("WAV file not found: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip tempClip = AudioSystem.getClip();
            tempClip.open(audioStream);
            tempClip.start();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
        }
    }

    // --- Play WAV background music (looped) ---
    public void playMusic(String filePath) {
        if (musicClip != null && musicClip.isRunning()) {
            return;
        }

        try {
            File musicFile = new File(filePath);
            if (!musicFile.exists()) {
                System.out.println("Background music not found: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioStream);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY); // loop music
            musicClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
        }
    }

    // --- Stop background music ---
    public void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            musicClip.close();
        }
    }
}
