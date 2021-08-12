package de.pmoit.voiceassistant;

import de.pmoit.voiceassistant.client.DeepSpeechClient;
import de.pmoit.voiceassistant.recognition.SpeechRecognizer;
import de.pmoit.voiceassistant.tts.TextToSpeech;
import de.pmoit.voiceassistant.utils.readproperties.GlobalConfiguration;
import de.pmoit.voiceassistant.utils.reflection.SkillSet;


public class Main {
    public static void main(String[] args) throws Exception {
        TextToSpeech tts = new TextToSpeech(GlobalConfiguration.getVoice());

        DeepSpeechClient client = new DeepSpeechClient();

        SkillSet skillSet = SkillSet.fromPackage("de.pmoit.voiceassistant.skills", tts);
        SpeechRecognizer recognizer = new SpeechRecognizer(tts, client, skillSet);
        recognizer.start();
    }
}
