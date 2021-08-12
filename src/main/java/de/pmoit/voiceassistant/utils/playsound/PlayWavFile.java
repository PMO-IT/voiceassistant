package de.pmoit.voiceassistant.utils.playsound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class PlayWavFile {
    private Logger logger = LoggerFactory.getLogger(getClass());

    void playWavFile(String file) {
        File audioFile = new File(file);
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine audioLine = ( SourceDataLine ) AudioSystem.getLine(info);
            audioLine.open(format);
            audioLine.start();
            byte[] bytesBuffer = new byte[BUFFER_SIZE];
            int bytesRead = - 1;
            while ( ( bytesRead = audioStream.read(bytesBuffer) ) != - 1) {
                audioLine.write(bytesBuffer, 0, bytesRead);
            }
            audioLine.drain();
            audioLine.close();
            audioStream.close();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private static final int BUFFER_SIZE = 4096;
}
