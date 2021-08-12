package de.pmoit.voiceassistant.client;

import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import de.pmoit.voiceassistant.utils.readproperties.GlobalConfiguration;


/**
 * Intern class to handle audioprocessing and manages speaking and timeouts
 * while speaking.
 */
class AudioProcessorImpl implements AudioProcessor {
    private boolean isSpeaking;
    private boolean speakingTimeoutReached;
    private long speakingStartTime;
    private final CustomSilenceDetector silenceDetector;

    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final Consumer<ByteArrayOutputStream> transcriptionCallback;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public AudioProcessorImpl(CustomSilenceDetector silenceDetector, Consumer<ByteArrayOutputStream> transcriptionCallback) {
        this.silenceDetector = silenceDetector;
        this.transcriptionCallback = transcriptionCallback;
    }

    /**
     * Processes audio and checks if speaker is still speaking. If the speaking
     * signal ends, the process stops.
     */
    @Override
    public boolean process(AudioEvent audioEvent) {
        try {
            if (isCurrentlySpeaking()) {
                calcAdditionalSpeakingTime();
                logger.debug("listening " + ( System.currentTimeMillis() - speakingStartTime ));
                isSpeaking = true;
                out.write(audioEvent.getByteBuffer(), 0, audioEvent.getBufferSize());
            }
            if (isNoLongerSpeaking()) {
                out.write(audioEvent.getByteBuffer(), 0, audioEvent.getBufferSize());
                isSpeaking = false;
                transcriptionCallback.accept(out);
                out = new ByteArrayOutputStream();
                speakingTimeoutReached = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn(e.getMessage(), e);
        }
        return true;
    }

    private boolean isNoLongerSpeaking() {
        return ! silenceDetector.isLoudEnough() && isSpeaking && speakingTimeoutReached;
    }

    private boolean isCurrentlySpeaking() {
        return ( silenceDetector.isLoudEnough() || isSpeaking ) && ! speakingTimeoutReached;
    }

    /**
     * Calculates if additional speaking time is needed and checks if the
     * speaking signal is still loud enough.
     */
    private void calcAdditionalSpeakingTime() {
        final int ADDITIONAL_LISTENING_TIME = GlobalConfiguration.getAdditionalListeningTime();

        if ( ! silenceDetector.isLoudEnough()) {
            if ( ( System.currentTimeMillis() - speakingStartTime ) > ADDITIONAL_LISTENING_TIME) {
                speakingTimeoutReached = true;
            }
        } else {
            speakingStartTime = System.currentTimeMillis();
        }
    }

    @Override
    public void processingFinished() {
        // Do nothing
    }
}
