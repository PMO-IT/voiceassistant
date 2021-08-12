package de.pmoit.skills.datetime;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

import de.pmoit.voiceassistant.skills.datetime.DateTimeSkill;


public class DateTimeSkillTest {

    @Test
    public void whatWeekdayIsToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.JANUARY, 10);
        DateTimeSkill dateTimeSkill = new DateTimeSkill(calendar);

        String response = dateTimeSkill.handle("Welcher tag ist heute");

        assertThat(response, anyOf(is("Heute ist Sonntag"), is("Sonntag")));
    }

    @Test
    public void whatWeekdayIsTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.JANUARY, 10);
        DateTimeSkill dateTimeSkill = new DateTimeSkill(calendar);

        String response = dateTimeSkill.handle("Welcher tag ist morgen");

        assertThat(response, anyOf(is("Morgen ist Montag"), is("Montag")));
    }

    @Test
    public void whatWeekdayIsTodayAfterAskingForTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.JANUARY, 10);
        DateTimeSkill dateTimeSkill = new DateTimeSkill(calendar);

        String responseTomorrow = dateTimeSkill.handle("Welcher tag ist morgen");
        String response = dateTimeSkill.handle("Welcher tag ist heute");

        assertThat(responseTomorrow, anyOf(is("Morgen ist Montag"), is("Montag")));
        assertThat(response, anyOf(is("Heute ist Sonntag"), is("Sonntag")));
    }

    @Test
    public void whatDateIsToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.JANUARY, 10);
        DateTimeSkill dateTimeSkill = new DateTimeSkill(calendar);

        String response = dateTimeSkill.handle("Welches datum ist heute");

        assertThat(response, anyOf(is("Heute ist der 10 01 21"), is("10 01 21")));
    }

    @Test
    public void whatDateIsTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.JANUARY, 10);
        DateTimeSkill dateTimeSkill = new DateTimeSkill(calendar);

        String response = dateTimeSkill.handle("Der wievielte ist morgen");

        assertThat(response, anyOf(is("Morgen ist der 11 01 21"), is("11 01 21")));
    }

    @Test
    public void whatDateIsTodayAfterAskingForTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.JANUARY, 10);
        DateTimeSkill dateTimeSkill = new DateTimeSkill(calendar);

        String response = dateTimeSkill.handle("Der wievielte ist morgen");
        String responsetoday = dateTimeSkill.handle("Welches datum ist heute");

        assertThat(response, anyOf(is("Morgen ist der 11 01 21"), is("11 01 21")));
        assertThat(responsetoday, anyOf(is("Heute ist der 10 01 21"), is("10 01 21")));
    }

    @Test
    public void whatTimeIsIt() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.set(Calendar.HOUR, 10);
        calendar.set(Calendar.MINUTE, 40);
        DateTimeSkill dateTimeSkill = new DateTimeSkill(calendar);

        String response = dateTimeSkill.handle("Wie spaet ist es");

        assertThat(response, anyOf(is("Aktuell ist es 10 Uhr 40"), is("Es ist 10 Uhr 40"), is("Wir haben es 10 Uhr 40")));
    }

    @Test
    public void whatDayWasYesterday_expectError() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.set(Calendar.HOUR, 10);
        calendar.set(Calendar.MINUTE, 40);
        DateTimeSkill dateTimeSkill = new DateTimeSkill(calendar);

        String response = dateTimeSkill.handle("Welcher Tag war gestern");

        assertEquals("Ich habe leider keine Antwort auf diese Frage.", response);
    }

}
