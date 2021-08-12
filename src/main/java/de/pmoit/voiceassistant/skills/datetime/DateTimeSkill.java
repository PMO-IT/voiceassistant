package de.pmoit.voiceassistant.skills.datetime;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import com.google.common.collect.Sets;

import de.pmoit.voiceassistant.skills.Skill;
import de.pmoit.voiceassistant.utils.filereader.FileContentReader;


public class DateTimeSkill implements Skill {
    private Set<String> actionKeywords = Sets.newHashSet("wie spät", "wie spaet", "wie viel uhr", "welches datum",
        "der wie vielte", "welcher tag");

    private final Calendar calendar;
    private FileContentReader fcrHeuteIst;
    private FileContentReader fcrMorgenIst;
    private FileContentReader fcrZeit;
    private FileContentReader fcrTagHeute;
    private FileContentReader fcrTagMorgen;

    private String replacePhrase = "[replaceA]";

    public DateTimeSkill() {
        calendar = Calendar.getInstance(TimeZone.getDefault());
    }

    public DateTimeSkill(final Calendar calendar) {
        this.calendar = calendar;
    }

    @Override
    public String handle(String spokenPhrase) {
        spokenPhrase = spokenPhrase.toLowerCase();
        String response = handleResults(spokenPhrase);
        if (response == null) {
            response = "Ich habe leider keine Antwort auf diese Frage.";
        }
        return response;
    }

    private String handleResults(String spokenPhrase) {
        String dateResult = handleDate(spokenPhrase);
        if (dateResult != null) {
            return dateResult;
        }
        String timeResult = handleTime(spokenPhrase);
        if (timeResult != null) {
            return timeResult;
        }
        String dayResult = handleDay(spokenPhrase);
        if (dayResult != null) {
            return dayResult;
        }
        return null;
    }

    private String handleDate(String spokenPhrase) {
        String userDir = System.getProperty("user.dir");
        String outputPath = "/src/main/java/de/pmoit/voiceassistant/skills/datetime/output/";
        if (spokenPhrase.contains("heute") && ( spokenPhrase.contains("datum") || spokenPhrase.contains("wievielte") )) {
            fcrHeuteIst = new FileContentReader(userDir + outputPath + "heuteIst.output.txt");
            return fcrHeuteIst.getRandomResponseFromFile(fcrHeuteIst).replace(replacePhrase, getTodayDate());
        }
        if (spokenPhrase.contains("morgen") && ( spokenPhrase.contains("datum") || spokenPhrase.contains("wievielte") )) {
            fcrMorgenIst = new FileContentReader(userDir + outputPath + "morgenIst.output.txt");
            return fcrMorgenIst.getRandomResponseFromFile(fcrMorgenIst).replace(replacePhrase, getTomorrowDate());
        }
        return null;
    }

    private String handleDay(String spokenPhrase) {
        String userDir = System.getProperty("user.dir");
        String outputPath = "/src/main/java/de/pmoit/voiceassistant/skills/datetime/output/";
        if (spokenPhrase.contains("heute") && ( spokenPhrase.contains("tag") )) {
            fcrTagHeute = new FileContentReader(userDir + outputPath + "tagHeute.output.txt");
            return fcrTagHeute.getRandomResponseFromFile(fcrTagHeute).replace(replacePhrase, getTodayDay());
        }
        if (spokenPhrase.contains("morgen") && ( spokenPhrase.contains("tag") )) {
            fcrTagMorgen = new FileContentReader(userDir + outputPath + "tagMorgen.output.txt");
            return fcrTagMorgen.getRandomResponseFromFile(fcrTagMorgen).replace(replacePhrase, getTomorrowDay());
        }
        return null;
    }

    private String handleTime(String spokenPhrase) {
        String userDir = System.getProperty("user.dir");
        String outputPath = "/src/main/java/de/pmoit/voiceassistant/skills/datetime/output/";

        List<String> keywords = Arrays.asList("wie spät", "wie spaet", "wie viel uhr", "zeit");

        for (String keyword : keywords) {
            if (spokenPhrase.contains(keyword)) {
                fcrZeit = new FileContentReader(userDir + outputPath + "wieSpaet.output.txt");
                return fcrZeit.getRandomResponseFromFile(fcrZeit).replace(replacePhrase, getCurrentTime());
            }
        }
        return null;
    }

    private String getTodayDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yy");
        return formatter.format(calendar.getTime());
    }

    private String getTomorrowDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yy");
        Calendar tomorrowCalendar = ( Calendar ) calendar.clone();
        tomorrowCalendar.add(Calendar.DATE, 1);
        return formatter.format(tomorrowCalendar.getTime());
    }

    private String getTodayDay() {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 1:
                return "Sonntag";
            case 2:
                return "Montag";
            case 3:
                return "Dienstag";
            case 4:
                return "Mittwoch";
            case 5:
                return "Donnerstag";
            case 6:
                return "Freitag";
            case 7:
                return "Samstag";
        }
        return "Error";
    }

    private String getTomorrowDay() {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) + 1;
        switch (dayOfWeek) {
            case 2:
                return "Montag";
            case 3:
                return "Dienstag";
            case 4:
                return "Mittwoch";
            case 5:
                return "Donnerstag";
            case 6:
                return "Freitag";
            case 7:
                return "Samstag";
            case 8:
                return "Sonntag";
        }
        return "Error";
    }

    private String getCurrentTime() {
        SimpleDateFormat formatterHH = new SimpleDateFormat("HH");
        SimpleDateFormat formattermm = new SimpleDateFormat("mm");
        return formatterHH.format(calendar.getTime()) + " Uhr " + formattermm.format(calendar.getTime());
    }

    @Override
    public Set<String> getActionWords() {
        return actionKeywords;
    }
}
