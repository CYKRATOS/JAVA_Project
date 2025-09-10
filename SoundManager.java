import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class SoundManager {

    private Clip clip;          // For sound effects
    private Clip musicClip;     // For background music

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Play WAV background music (looped) ---
    public void playMusic(String filePath) {
        stopMusic(); // stop previous music

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
            e.printStackTrace();
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
