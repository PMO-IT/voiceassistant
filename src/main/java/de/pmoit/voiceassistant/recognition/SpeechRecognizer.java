package de.pmoit.voiceassistant.recognition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pmoit.voiceassistant.client.DeepSpeechClient;
import de.pmoit.voiceassistant.tts.TextToSpeech;
import de.pmoit.voiceassistant.utils.playsound.PlaySound;
import de.pmoit.voiceassistant.utils.readproperties.GlobalConfiguration;
import de.pmoit.voiceassistant.utils.reflection.SkillSet;


/**
 * Speech recognizer class, handles the text to speach, the speech to text and
 * the skillsets.
 */
public class SpeechRecognizer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SkillSet skillSet;
    private TextToSpeech tts;
    private DeepSpeechClient client;

    public SpeechRecognizer(TextToSpeech tts, DeepSpeechClient client, SkillSet skillSet) {
        this.tts = tts;
        this.client = client;
        this.skillSet = skillSet;
    }

    /**
     * Starts the speech recognizer and prepares the transcription.
     */
    public void start() {
        logger.info("Starting Speech Recognizer...\n");
        try {
            String speechResult;
            client.prepareTranscription();
            while (true) {
                speechResult = client.getResult();
                if (speechResult != null) {
                    logger.info("Result: " + speechResult + " \n");
                    makeDecision(speechResult);
                    client.resetResult();
                }
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }

    /**
     * Decides based on call-to-action phrase if the skillset.runAll should be
     * triggered.
     */
    private void makeDecision(String speechRecognitionResult) {
        String normalisedResult = normaliseResultByCallToAction(speechRecognitionResult);
        if ( ! normalisedResult.equals("")) {
            PlaySound.playAcknowledgeSound();
            try {
                skillSet.runAll(normalisedResult);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                tts.speak("Es ist ein Fehler aufgetreten!");
            }
        }
    }

    private String normaliseResultByCallToAction(String speechRecognitionResult) {
        String callToAction1 = GlobalConfiguration.getCallToAction1();
        String callToAction2 = GlobalConfiguration.getCallToAction2();

        String recognitionResultToLower = speechRecognitionResult.toLowerCase();

        int posCallToAction = recognitionResultToLower.indexOf(callToAction1);
        if (posCallToAction == - 1) {
            posCallToAction = recognitionResultToLower.indexOf(callToAction2);
        }

        if (posCallToAction == - 1) {
            return "";
        }
        return speechRecognitionResult.substring(posCallToAction);
    }
}
