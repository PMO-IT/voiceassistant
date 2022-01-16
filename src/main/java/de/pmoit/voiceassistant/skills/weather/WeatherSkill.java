package de.pmoit.voiceassistant.skills.weather;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import de.pmoit.voiceassistant.skills.Skill;
import de.pmoit.voiceassistant.utils.Normaliser;
import de.pmoit.voiceassistant.utils.filereader.FileContentReader;
import de.pmoit.voiceassistant.utils.readproperties.Propertyloader;
import de.pmoit.voiceassistant.utils.rest.JSONService;


public class WeatherSkill implements Skill {
    private Set<String> actionKeywords = Sets.newHashSet("wetter", "sonne", "sonnenaufgang", "sonnenuntergang", "regnet",
        "regen", "sonnenschein", "gewitter", "temperatur", "warm", "heiß", "kalt", "schnee", "schneit", "grad");
    private final Properties loadWetherProperty = Propertyloader.loadProperties(
        "resources/skills/weather.config.properties");
    private final String API = loadWetherProperty.getProperty("api");
    private final String defaultCity = loadWetherProperty.getProperty("defaultcity");
    private JSONService jsonService = new JSONService();
    private JSONParserWeatherApp jsonParser;

    private String replacePhrase = "[replaceA]";
    private String replacePhraseB = "[replaceB]";
    private String replacePhraseC = "[replaceC]";
    private String replacePhraseOrt = "[replaceOrt]";

    private String baseUrl;
    private Calendar calendar;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public WeatherSkill() {
        this.calendar = Calendar.getInstance(TimeZone.getDefault());
        this.baseUrl = "http://api.openweathermap.org";
    }

    public WeatherSkill(final String url) {
        this.calendar = Calendar.getInstance(TimeZone.getDefault());
        this.baseUrl = url;
    }

    public WeatherSkill(final String url, final Calendar calendar) {
        this.calendar = calendar;
        this.baseUrl = url;
    }

    private String callWeatherApi(String city) {
        city = encodeValue(city);
        String apicall = baseUrl + "/data/2.5/weather?q=" + ( city ).trim() + "&appid=" + API + "&lang=de&units=Metric";
        String weatherResult = jsonService.getJSONRequestResult(apicall);
        if (weatherResult.equals("Fehler : HTTP error code : 404")) {
            return "404";
        } else if (weatherResult.equals("Fehler : HTTP error code : 400")) {
            return "400";
        } else if (weatherResult.equals("Fehler : HTTP error code : 401")) {
            return "401";
        }
        jsonParser = new JSONParserWeatherApp(weatherResult);
        return weatherResult;
    }

    private String callWeatherApiForecast(double lat, double lon) {
        String apicall = baseUrl + "/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&appid=" + API
            + "&lang=de&units=Metric";
        return jsonService.getJSONRequestResult(apicall);
    }

    private String callWeatherApiForecastForCity(String city) {
        String result = callWeatherApi(city);
        if (result.equals("404") || result.equals("400") || result.equals("401")) {
            return result;
        }
        String foreCastResult = callWeatherApiForecast(getLatitude(), getLontitude());
        jsonParser = new JSONParserWeatherApp(foreCastResult);
        return foreCastResult;
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
        spokenPhrase = Normaliser.normaliseNumbers(spokenPhrase);

        String wetterResult = handleWeather(spokenPhrase);
        if (wetterResult != null) {
            return wetterResult;
        }

        String wetterInResult = handleWeatherIn(spokenPhrase);
        if (wetterInResult != null) {
            return wetterInResult;
        }

        String wetterSunRiseResult = handleSunrise(spokenPhrase);
        if (wetterSunRiseResult != null) {
            return wetterSunRiseResult;
        }
        String wetterSunRiseInResult = handleSunriseIn(spokenPhrase);
        if (wetterSunRiseInResult != null) {
            return wetterSunRiseInResult;
        }
        String wetterSunSetResult = handleSunset(spokenPhrase);
        if (wetterSunSetResult != null) {
            return wetterSunSetResult;
        }
        String wetterSunSetInResult = handleSunsetIn(spokenPhrase);
        if (wetterSunSetInResult != null) {
            return wetterSunSetInResult;
        }

        String result = specialWeather(spokenPhrase);
        if (result != null) {
            return result;
        }
        String tempResult = handleTemp(spokenPhrase);
        if (tempResult != null) {
            return tempResult;
        }
        tempResult = handleTempIn(spokenPhrase);
        if (tempResult != null) {
            return tempResult;
        }
        result = handleWeatherUmIn(spokenPhrase);
        if (result != null) {
            return result;
        }
        result = handleWeatherUm(spokenPhrase);
        if (result != null) {
            return result;
        }

        return null;
    }

    private String specialWeather(String spokenPhrase) {
        List<String> wetherTypeRain = Arrays.asList(" regen ", "regnet");
        String result = handleSpecialWeatherIn(spokenPhrase, wetherTypeRain);
        if (result != null) {
            return result;
        }
        result = handleSpecialWeather(spokenPhrase, wetherTypeRain);
        if (result != null) {
            return result;
        }
        List<String> wetherTypeSun = Arrays.asList("sonne", "scheint die Sonne", "sonnenschein");
        result = handleSpecialWeatherIn(spokenPhrase, wetherTypeSun);
        if (result != null) {
            return result;
        }
        result = handleSpecialWeather(spokenPhrase, wetherTypeSun);
        if (result != null) {
            return result;
        }
        List<String> wetherTypeClouds = Arrays.asList("wolkig", "ist bewölkt", "ist bewoelkt", "bewoelkt", "bewölkt",
            "wolken");
        result = handleSpecialWeatherIn(spokenPhrase, wetherTypeClouds);
        if (result != null) {
            return result;
        }
        result = handleSpecialWeather(spokenPhrase, wetherTypeClouds);
        if (result != null) {
            return result;
        }
        wetherTypeRain = Arrays.asList("schnee", "schneit");
        result = handleSpecialWeatherIn(spokenPhrase, wetherTypeRain);
        if (result != null) {
            return result;
        }
        result = handleSpecialWeather(spokenPhrase, wetherTypeRain);
        if (result != null) {
            return result;
        }

        return null;
    }

    private String handleWeatherUmIn(String spokenPhrase) {
        String userDir = System.getProperty("user.dir");
        String outputPath = "/src/main/java/de/pmoit/voiceassistant/skills/weather/output/";

        if (spokenPhrase.matches(".*in [0-9]+.*") && spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*")) {
            return weatherInXHours(spokenPhrase, userDir, outputPath);
        } else if (spokenPhrase.matches(".*um [0-9]+.*") && spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*") && ! spokenPhrase
            .contains(" morgen ")) {
            return weatherAtXOClock(spokenPhrase, userDir, outputPath);
        } else if (spokenPhrase.matches(".*um [0-9]+.*") && spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*") && spokenPhrase
            .contains(" morgen ")) {
            return weatherAtXOClockTomorrow(spokenPhrase, userDir, outputPath);
        }
        return null;

    }

    private String weatherAtXOClockTomorrow(String spokenPhrase, String userDir, String outputPath) {
        String newOrt = getOrtFromPhrase(spokenPhrase);
        int currentHour = getCurrentHour();
        int hour = getHourFromPhraseUm(spokenPhrase);
        if (hour == - 1) {
            return "Es ist nur eine Prognose von 48 Stunden möglich.";
        }

        int calcHour = hour + 24 - currentHour;

        String result = callWeatherApiForecastForCity(newOrt);
        if (result.equals("404")) {
            return newOrt + " kenne ich leider nicht.";
        }
        FileContentReader fcr = new FileContentReader(userDir + outputPath + "wetterMorgenUmUhr.output.txt");

        return fcr.getRandomResponseFromFile(fcr).replace(replacePhrase, Integer.toString(hour) + " Uhr ").replace(
            replacePhraseB, getHourlyWetherResult(calcHour)).replace(replacePhraseOrt, newOrt);
    }

    private String weatherAtXOClock(String spokenPhrase, String userDir, String outputPath) {
        String newOrt = getOrtFromPhrase(spokenPhrase);
        int currentHour = getCurrentHour();
        int hour = getHourFromPhraseUm(spokenPhrase);
        if (hour == - 1) {
            return "Es ist nur eine Prognose von 48 Stunden möglich.";
        }
        if (hour < currentHour) {
            return hour + " Uhr ist schon vorbei.";
        }

        int calcHour = hour - currentHour;

        String result = callWeatherApiForecastForCity(newOrt);
        if (result.equals("404")) {
            return newOrt + " kenne ich leider nicht.";
        }

        FileContentReader fcr = new FileContentReader(userDir + outputPath + "wetterUmUhr.output.txt");
        return fcr.getRandomResponseFromFile(fcr).replace(replacePhrase, Integer.toString(hour) + " Uhr ").replace(
            replacePhraseB, getHourlyWetherResult(calcHour)).replace(replacePhraseOrt, newOrt);
    }

    private String weatherInXHours(String spokenPhrase, String userDir, String outputPath) {
        String newOrt = getOrtFromPhrase(spokenPhrase);
        int hour = getHourFromPhraseIn(spokenPhrase);
        if (hour == - 1) {
            return "Es ist nur eine Prognose von 48 Stunden möglich.";
        }
        String result = callWeatherApiForecastForCity(newOrt);
        if (result.equals("404")) {
            return newOrt + " kenne ich leider nicht.";
        }

        FileContentReader fcr = new FileContentReader(userDir + outputPath + "wetterInStunden.output.txt");
        return fcr.getRandomResponseFromFile(fcr).replace(replacePhrase, Integer.toString(hour) + " Stunden").replace(
            replacePhraseB, getHourlyWetherResult(hour)).replace(replacePhraseOrt, newOrt);
    }

    private int getHourFromPhraseIn(String spokenPhrase) {
        Pattern inHourPattern = Pattern.compile(".* in ([0-9]+) .*");
        return getHourFromPhrase(spokenPhrase, inHourPattern);
    }

    private int getHourFromPhraseUm(String spokenPhrase) {
        Pattern inHourPattern = Pattern.compile(".* um ([0-9]+) .*");
        return getHourFromPhrase(spokenPhrase, inHourPattern);
    }

    private int getHourFromPhrase(String spokenPhrase, Pattern inHourPattern) {
        Matcher m = inHourPattern.matcher(spokenPhrase);
        if (m.find()) {
            String hour = m.group(1);
            int iHour = Integer.parseInt(hour);
            if (iHour > 48) {
                return - 1;
            }
            return iHour;
        }
        return - 1;
    }

    private String handleWeatherUm(String spokenPhrase) {
        if ( ! spokenPhrase.contains(".*in [A-Za-zÄÜÖäüö]+.*")) {
            return handleWeatherUmIn(spokenPhrase + " in " + defaultCity);
        }
        return null;
    }

    private String handleTemp(String spokenPhrase) {
        if ( ! spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*")) {
            return handleTempIn(spokenPhrase + " in " + defaultCity);
        }
        return null;
    }

    private String handleTempIn(String spokenPhrase) {
        String userDir = System.getProperty("user.dir");
        String outputPath = "/src/main/java/de/pmoit/voiceassistant/skills/weather/output/";

        List<String> wethertypes = Arrays.asList("temperatur", "grad", "heiß", "warm", "kalt", "mindestwert", "maximalwert");
        boolean wetterAndIn = doesSpokenPhraseContainWethertype(spokenPhrase, wethertypes);

        if (wetterAndIn && spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*") && ! spokenPhrase.contains("morgen")) {
            String newOrt = getOrtFromPhrase(spokenPhrase);
            String result = callWeatherApiForecastForCity(newOrt);
            if (result.equals("404")) {
                return newOrt + " kenne ich leider nicht.";
            }

            FileContentReader fcr = new FileContentReader(userDir + outputPath + "temperaturHeute.output.txt");
            return fcr.getRandomResponseFromFile(fcr).replace(replacePhrase, Double.toString(getCurrentTemperature()))
                .replace(replacePhraseB, Double.toString(getDailyTemperatureMax(1))).replace(replacePhraseC, Double.toString(
                    getDailyTemperatureMin(1))).replace(replacePhraseOrt, newOrt);
        }
        if (wetterAndIn && spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*") && spokenPhrase.contains("morgen")) {
            String newOrt = getOrtFromPhrase(spokenPhrase);
            String result = callWeatherApiForecastForCity(newOrt);
            if (result.equals("404")) {
                return newOrt + " kenne ich leider nicht.";
            }

            FileContentReader fcr = new FileContentReader(userDir + outputPath + "temperaturMorgen.output.txt");
            return fcr.getRandomResponseFromFile(fcr).replace(replacePhrase, Double.toString(getDailyTemperatureMax(1)))
                .replace(replacePhraseB, Double.toString(getDailyTemperatureMin(1))).replace(replacePhraseC, Double.toString(
                    getDailyTemperatureMin(1))).replace(replacePhraseOrt, newOrt);
        }
        return null;
    }

    private String handleSpecialWeather(String spokenPhrase, List<String> wethertypes) {
        if ( ! spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*")) {
            return handleSpecialWeatherIn(spokenPhrase + " in " + defaultCity, wethertypes);
        }
        return null;
    }

    private String handleSpecialWeatherIn(String spokenPhrase, List<String> wethertypes) {
        String userDir = System.getProperty("user.dir");
        String outputPath = "/src/main/java/de/pmoit/voiceassistant/skills/weather/output/";

        boolean wetterAndIn = doesSpokenPhraseContainWethertype(spokenPhrase, wethertypes);

        if (wetterAndIn && spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*") && ! spokenPhrase.contains("morgen")) {
            String newOrt = getOrtFromPhrase(spokenPhrase);
            String result = callWeatherApiForecastForCity(newOrt);
            if (result.equals("404") || result.equals("400")) {
                return newOrt + " kenne ich leider nicht.";
            } else if (result.equals("401")) {
                return "Nicht authorisierter Zugriff!";
            }

            int isRain = doesItWetherToday(wethertypes);
            String replaceWithPhrase = wethertypes.get(1);
            if (isRain == 0) {
                return itRainsNow(userDir, outputPath, newOrt, replaceWithPhrase);
            } else if (isRain > 0) {
                return itRainsInTheNextHours(userDir, outputPath, newOrt, isRain, replaceWithPhrase);
            }
            return itDoesntRain(userDir, outputPath, newOrt, replaceWithPhrase);
        }
        if (wetterAndIn && spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*") && spokenPhrase.contains("morgen")) {
            String newOrt = getOrtFromPhrase(spokenPhrase);
            String result = callWeatherApiForecastForCity(newOrt);
            if (result.equals("404") || result.equals("400")) {
                return newOrt + " kenne ich leider nicht.";
            } else if (result.equals("401")) {
                return "Nicht authorisierter Zugriff!";
            }

            FileContentReader fcrMorgenIst = new FileContentReader(userDir + outputPath + "wetterTypMorgenIn.output.txt");
            return fcrMorgenIst.getRandomResponseFromFile(fcrMorgenIst).replace(replacePhrase, getDailyWeather(1)).replace(
                replacePhraseOrt, newOrt);
        }
        return null;
    }

    private boolean doesSpokenPhraseContainWethertype(String spokenPhrase, List<String> wethertypes) {
        for (String wetherType : wethertypes) {
            boolean phraseResult = spokenPhrase.contains(wetherType);
            if (phraseResult) {
                return true;
            }
        }
        return false;
    }

    private String itDoesntRain(String userDir, String outputPath, String newOrt, String wethertype) {
        FileContentReader fcr = new FileContentReader(userDir + outputPath + "wetterTypHeuteIn.nein.output.txt");
        return fcr.getRandomResponseFromFile(fcr).replace(replacePhrase, wethertype).replace(replacePhraseOrt, newOrt);
    }

    private String itRainsInTheNextHours(String userDir, String outputPath, String newOrt, int isRain, String wethertype) {
        FileContentReader fcr = new FileContentReader(userDir + outputPath + "wetterTypHeuteIn.ja.output.txt");
        String stundenPhrase = "";
        stundenPhrase = handleRainPhrase(isRain);
        return fcr.getRandomResponseFromFile(fcr).replace(replacePhrase, wethertype).replace(replacePhraseOrt, newOrt)
            .replace(replacePhraseB, stundenPhrase);
    }

    private String itRainsNow(String userDir, String outputPath, String newOrt, String wethertype) {
        FileContentReader fcr = new FileContentReader(userDir + outputPath + "wetterTypHeuteInJetzt.ja.output.txt");
        return fcr.getRandomResponseFromFile(fcr).replace(replacePhrase, wethertype).replace(replacePhraseOrt, newOrt);
    }

    private String handleRainPhrase(int isRain) {
        String stundenPhrase;
        if (isRain == 1) {
            stundenPhrase = "in der nächsten Stunde";
        } else {
            stundenPhrase = "in etwa " + isRain + " Stunden";
        }
        return stundenPhrase;
    }

    private int doesItWetherToday(List<String> wethertypes) {
        int currentHour = getCurrentHour();
        for (int hourCounter = 0; hourCounter < ( 24 - currentHour ); hourCounter ++ ) {
            String hourlyWether = getHourlyWetherResult(hourCounter).toLowerCase();
            if (doesSpokenPhraseContainWethertype(hourlyWether, wethertypes)) {
                return hourCounter;
            }
        }
        return - 1;
    }

    private int getCurrentHour() {
        SimpleDateFormat formatterHH = new SimpleDateFormat("HH");
        return Integer.parseInt(formatterHH.format(calendar.getTime()));
    }

    private String handleSunriseIn(String spokenPhrase) {
        if (spokenPhrase.matches(".*geht die sonne auf [A-Za-zÄÜÖäüö]+.*") || spokenPhrase.matches(
            ".*sonnenaufgang [A-Za-zÄÜÖäüö]+.*")) {

            String newOrt = getOrtFromPhrase(spokenPhrase);
            String wetherApiResult = callWeatherApiForecastForCity(newOrt);

            if (wetherApiResult.equals("404") || wetherApiResult.equals("400")) {
                return "Diese Anfrage hat keine Daten ergeben, bitte versuch es erneut.";
            } else if (wetherApiResult.equals("401")) {
                return "Nicht authorisierter Zugriff!";
            }
            return "Die Sonne geht um " + getSunrise() + " auf in " + newOrt;
        }
        return null;
    }

    private String handleSunsetIn(String spokenPhrase) {
        if (spokenPhrase.matches(".*geht die sonne unter [A-Za-zÄÜÖäüö]+.*") || spokenPhrase.matches(
            ".*sonnenuntergang [A-Za-zÄÜÖäüö]+.*")) {

            String newOrt = getOrtFromPhrase(spokenPhrase);
            String wetherApiResult = callWeatherApiForecastForCity(newOrt);

            if (wetherApiResult.equals("404") || wetherApiResult.equals("400")) {
                return "Diese Anfrage hat keine Daten ergeben, bitte versuch es erneut.";
            } else if (wetherApiResult.equals("401")) {
                return "Nicht authorisierter Zugriff!";
            }
            return "Die Sonne geht um " + getSunset() + " unter in " + newOrt;
        }
        return null;
    }

    private String handleWeather(String spokenPhrase) {
        if ( ! spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*")) {
            return handleWeatherIn(spokenPhrase + " in " + defaultCity);
        }
        return null;
    }

    private String handleSunrise(String spokenPhrase) {
        if ( ! spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*")) {
            return handleSunriseIn(spokenPhrase + " in " + defaultCity);
        }
        return null;
    }

    private String handleSunset(String spokenPhrase) {
        if ( ! spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*")) {
            return handleSunsetIn(spokenPhrase + " in " + defaultCity);
        }
        return null;
    }

    private String handleWeatherIn(String spokenPhrase) {
        String userDir = System.getProperty("user.dir");
        String outputPath = "/src/main/java/de/pmoit/voiceassistant/skills/weather/output/";

        boolean WetterAndIn = spokenPhrase.contains("wetter") && spokenPhrase.matches(".*in [A-Za-zÄÜÖäüö]+.*")
            && ! spokenPhrase.matches(".*in [0-9]+.*") && ! spokenPhrase.matches(".*um [0-9]+.*");
        if (WetterAndIn && ! spokenPhrase.contains("morgen")) {
            String newOrt = getOrtFromPhrase(spokenPhrase);
            String wetherApiResult = callWeatherApi(newOrt);
            if (wetherApiResult.equals("404") || wetherApiResult.equals("400")) {
                return "Diese Anfrage hat keine Daten ergeben, bitte versuch es erneut.";
            } else if (wetherApiResult.equals("401")) {
                return "Nicht authorisierter Zugriff!";
            }
            FileContentReader fcrHeuteIst = new FileContentReader(userDir + outputPath + "wetterHeute.output.txt");
            return fcrHeuteIst.getRandomResponseFromFile(fcrHeuteIst).replace(replacePhrase, getCurrentWeather()).replace(
                replacePhraseOrt, newOrt);
        }
        if (WetterAndIn && spokenPhrase.contains("morgen")) {
            String newOrt = getOrtFromPhrase(spokenPhrase);
            String result = callWeatherApiForecastForCity(newOrt);
            if (result.equals("404") || result.equals("400")) {
                return newOrt + " kenne ich leider nicht.";
            } else if (result.equals("401")) {
                return "Nicht authorisierter Zugriff!";
            }

            FileContentReader fcrMorgenIst = new FileContentReader(userDir + outputPath + "wetterMorgen.output.txt");
            return fcrMorgenIst.getRandomResponseFromFile(fcrMorgenIst).replace(replacePhrase, getDailyWeather(1)).replace(
                replacePhraseOrt, newOrt);
        }
        return null;
    }

    private String getOrtFromPhrase(String spokenPhrase) {
        Pattern pattern = Pattern.compile("(in )([A-Za-züöäÜÖÄ ]+)");
        Matcher matcher = pattern.matcher(spokenPhrase);
        matcher.find();
        return matcher.group(2);
    }

    private double getLontitude() {
        return jsonParser.parseWetherResultsAsDouble("coord", "lon");
    }

    private double getLatitude() {
        return jsonParser.parseWetherResultsAsDouble("coord", "lat");
    }

    private String getDailyWeather(int day) {
        return jsonParser.getDailyWetherResult(day);
    }

    private String getHourlyWetherResult(int hour) {
        return jsonParser.getHourlyWetherResult(hour);
    }

    private double getDailyTemperatureMin(int day) {
        return jsonParser.getDailyDataDouble(day, "temp", "min");
    }

    private double getDailyTemperatureMax(int day) {
        return jsonParser.getDailyDataDouble(day, "temp", "max");
    }

    private double getCurrentTemperature() {
        return jsonParser.parseWetherResultsAsDouble("current", "temp");
    }

    private String getCurrentWeather() {
        return jsonParser.parseWetherResultsAsArray("weather", "description");
    }

    private String getSunrise() {
        return formatResultTimeStampToHourMinute(jsonParser.parseWetherResultsAsInt("current", "sunrise"));
    }

    private String getSunset() {
        return formatResultTimeStampToHourMinute(jsonParser.parseWetherResultsAsInt("current", "sunset"));
    }

    private String formatResultTimeStampToHourMinute(int unitTimeStamp) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        return df.format(( long ) unitTimeStamp * 1000).replace(":", " uhr ");
    }

    @Override
    public Set<String> getActionWords() {
        return actionKeywords;
    }

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage(), e);
        }
        return value;
    }
}
