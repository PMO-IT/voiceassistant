package de.pmoit.voiceassistant.utils.reminder;

import java.util.Timer;
import java.util.TimerTask;

import de.pmoit.voiceassistant.utils.playsound.PlaySound;


public class Intervall {
    private Timer timer;
    private IntervallTask intervallTask;

    public Intervall(int seconds, String speakResult) {
        timer = new Timer();
        int milliseconds, period;
        if (seconds > 3) {
            milliseconds = ( seconds - 3 ) * 1000;
            period = seconds * 1000;
        } else {
            milliseconds = seconds * 1000;
            period = milliseconds;
        }
        timer.scheduleAtFixedRate(intervallTask, milliseconds, period);
    }

    public void stopTimer() {
        intervallTask.stopTimer();
    }

    class IntervallTask extends TimerTask {
        @Override
        public void run() {
            PlaySound.playIntervallSound();
        }

        public void stopTimer() {
            timer.cancel();
        }
    }
}
