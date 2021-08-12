package de.pmoit.voiceassistant.utils.playsound;

public class PlaySound {
    public static void playSound(String file) {
        PlayWavFile playWavFile = new PlayWavFile();
        playWavFile.playWavFile(file);
    }

    public static void playAcknowledgeSound() {
        playSound("resources/sound/readyBeep.wav");
    }

    public static void playIntervallSound() {
        playSound("resources/sound/321beep.wav");
    }
}
