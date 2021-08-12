package de.pmoit.voiceassistant.client;

import be.tarsos.dsp.SilenceDetector;


/**
 * A custom silencedetector. Detects if someone is speaking in a specific range.
 */
class CustomSilenceDetector extends SilenceDetector {
    private static final boolean BREAK_PROCESSING_QUEUE_ON_SILENCE = false;
    private static final double MINTHRESHOLD = - 30;
    private static final double MAXTHRESHOLD = - 80;

    public CustomSilenceDetector() {
        super(MAXTHRESHOLD, BREAK_PROCESSING_QUEUE_ON_SILENCE);
    }

    public boolean isLoudEnough() {
        return currentSPL() > MAXTHRESHOLD && currentSPL() < MINTHRESHOLD;
    }
}
