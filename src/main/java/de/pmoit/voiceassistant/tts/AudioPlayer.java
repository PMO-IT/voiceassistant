package de.pmoit.voiceassistant.tts;

import static javax.sound.sampled.FloatControl.Type.MASTER_GAIN;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import marytts.util.data.audio.StereoAudioInputStream;


/**
 * Class for handling and playing audio. Enables the voice to be louder and
 * manages the audiothread.
 *
 */
class AudioPlayer {

    private AudioPlayerThread audioPlayerThread;
    private AudioInputStream ais;
    private boolean stopThread;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Cancel the AudioPlayer which will cause the Thread to exit.
     */
    public void interrupt() {
        if (audioPlayerThread != null)
            audioPlayerThread.interrupt();
    }

    public void play(AudioInputStream audio) {
        play(audio, 1F);
    }

    /**
     * Start the audioplayer thread.
     * 
     * @param audio
     * @param gain
     */
    public void play(AudioInputStream audio, float gain) {
        if (audioPlayerThread != null) {
            try {
                audioPlayerThread.join();
            } catch (InterruptedException e) {
                logger.warn(e.getMessage(), e);
            }
        }
        audioPlayerThread = new AudioPlayerThread(gain);
        this.ais = audio;
        audioPlayerThread.start();
    }

    private class AudioPlayerThread extends Thread {
        private final int STEREO = 3;

        private SourceDataLine sdLine;
        private final float gain;

        private AudioPlayerThread(float gain) {
            this.setDaemon(false);
            this.gain = gain;
        }

        private void setMasterGain() {
            if (sdLine != null && sdLine.isControlSupported(MASTER_GAIN)) {
                float value = ( float ) ( 20 * Math.log10(gain <= 0.0 ? 0.0000 : gain) );
                FloatControl control = ( FloatControl ) sdLine.getControl(MASTER_GAIN);
                control.setValue(value);
            }
        }

        @Override
        public void run() {
            ais = new StereoAudioInputStream(ais, STEREO);
            AudioFormat audioFormat = ais.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

            try {
                if (sdLine == null) {
                    sdLine = ( SourceDataLine ) AudioSystem.getLine(info);
                    setMasterGain();
                }
                sdLine.open(audioFormat);
                sdLine.start();

                writeDataToLine();
                if ( ! stopThread) {
                    sdLine.drain();
                }
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
                return;
            } finally {
                sdLine.close();
            }
        }

        private void writeDataToLine() throws IOException {
            int readLine = 0;
            byte[] data = new byte[65532];
            while ( ( readLine != - 1 ) && ( ! stopThread )) {
                readLine = ais.read(data, 0, data.length);
                if (readLine >= 0) {
                    sdLine.write(data, 0, readLine);
                }
            }
        }

        /**
         * Stops AudioPlayer and thread.
         */
        @Override
        public void interrupt() {
            if (sdLine != null) {
                sdLine.stop();
            }
            stopThread = true;
            super.interrupt();
        }
    }
}
