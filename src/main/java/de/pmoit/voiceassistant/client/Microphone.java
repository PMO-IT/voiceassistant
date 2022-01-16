package de.pmoit.voiceassistant.client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tarsos.dsp.io.jvm.JVMAudioInputStream;


/**
 * Class to handle microphone. Checks if line is supported and opens it for use.
 */
class Microphone {
    private static final int SAMPLE_RATE_IN_HZ = 16000;
    private static final int SAMPLE_SIZE_IN_BITS = 16;
    private static final int NO_OF_CHANNELS = 1;
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIAN = false;
    private TargetDataLine line;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public JVMAudioInputStream start() throws Exception {
        AudioFormat format = new AudioFormat(SAMPLE_RATE_IN_HZ, SAMPLE_SIZE_IN_BITS, NO_OF_CHANNELS, SIGNED, BIG_ENDIAN);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if ( ! AudioSystem.isLineSupported(info)) {
            throw new Exception("Mikrofon ist nicht verfügbar!");
        }

        getAllAvailableMics(info);
        selectAvailableMic();
        if (line == null)
            throw new UnsupportedOperationException("No recording device found");

        synchronized ( line ) {
            line.open(format, line.getBufferSize());
            logger.debug("Programm start");
            line.start();
        }

        AudioInputStream stream = new AudioInputStream(line);
        return new JVMAudioInputStream(stream);
    }

    /**
     * Get all available microphones.
     */
    private void getAllAvailableMics(DataLine.Info info) {
        Mixer.Info[] aInfos = AudioSystem.getMixerInfo();

        for (int i = 0; i < aInfos.length; i ++ ) {
            Mixer mixer = AudioSystem.getMixer(aInfos[i]);
            if (mixer.isLineSupported(info)) {
                logger.debug(aInfos[i].getName());
            }
        }
    }

    /**
     * Select the next available microphone.
     * 
     * @throws LineUnavailableException
     */
    private void selectAvailableMic() throws LineUnavailableException {
        Info[] info = AudioSystem.getMixerInfo();
        for (int i = 0; i < info.length; i ++ ) {
            Mixer mixer = AudioSystem.getMixer(info[i]);
            Line.Info[] targetLineInfo = mixer.getTargetLineInfo();
            if (targetLineInfo.length > 0) {
                line = ( TargetDataLine ) mixer.getLine(targetLineInfo[0]);
                return;
            }
        }
    }

    public int getFrameSize() {
        return line.getFormat().getFrameSize();
    }

    public AudioFormat getAudioFormat() {
        return line.getFormat();
    }

}
