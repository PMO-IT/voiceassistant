package de.pmoit.voiceassistant.tts;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;


/**
 * Handels the text to speech functionality.
 *
 */
public class TextToSpeech {

    private AudioPlayer audioPlayer = new AudioPlayer();
    private MaryInterface marytts;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public TextToSpeech(String voice) {
        try {
            marytts = new LocalMaryInterface();
            marytts.setVoice(voice);
        } catch (MaryConfigurationException ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * Audiooutputs the passed string.
     */
    public void speak(String text) {
        speak(text, 2.0f);
    }

    /**
     * If you want to speak immediatly and cancel every other ongoing
     * speakingprocess.
     */
    public void speakImmediately(String text) {
        audioPlayer.interrupt();
        speak(text, 2.0f);
    }

    /**
     * Audiooutput with increased audio
     * 
     * @param text
     * @param gainValue in percent
     */
    private void speak(String text, float gainValue) {
        try (AudioInputStream audio = marytts.generateAudio(text)) {
            audioPlayer.play(audio);
        } catch (SynthesisException | IOException ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }
}
