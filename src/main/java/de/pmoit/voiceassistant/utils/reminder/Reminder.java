package de.pmoit.voiceassistant.utils.reminder;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.pmoit.voiceassistant.tts.TextToSpeech;
import de.pmoit.voiceassistant.utils.readproperties.GlobalConfiguration;


public class Reminder {
    private Timer timer;

    public Reminder(int seconds, String speakResult) {
        TextToSpeech tts = new TextToSpeech(GlobalConfiguration.getVoice());
        timer = new Timer();
        timer.schedule(new RemindTask(tts, speakResult), seconds * 1000);
    }

    public Reminder(int hour, int minute, int second, String speakResult) {
        TextToSpeech tts = new TextToSpeech(GlobalConfiguration.getVoice());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        Date time = calendar.getTime();

        timer = new Timer();
        timer.schedule(new RemindTask(tts, speakResult), time);
    }

    class RemindTask extends TimerTask {
        private TextToSpeech tts;
        private String speakResult;

        public RemindTask(TextToSpeech tts, String speakResult) {
            this.tts = tts;
            this.speakResult = speakResult;
        }

        @Override
        public void run() {
            tts.speak(speakResult);
            timer.cancel();
        }

        public void stopTimer() {
            System.exit(0);
        }
    }
}
