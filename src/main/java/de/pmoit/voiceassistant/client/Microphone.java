package de.pmoit.voiceassistant.client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
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
        line = ( TargetDataLine ) AudioSystem.getLine(info);
        line.open(format, line.getBufferSize());

        logger.debug("Programm start");

        line.start();

        AudioInputStream stream = new AudioInputStream(line);
        return new JVMAudioInputStream(stream);
    }

    public int getFrameSize() {
        return line.getFormat().getFrameSize();
    }

    public AudioFormat getAudioFormat() {
        return line.getFormat();
    }

}
