package de.pmoit.skills.weather;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Calendar;

import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import de.pmoit.voiceassistant.skills.weather.WeatherSkill;


public class WeatherSkillTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    private final String HOST = "http://localhost";

    @Test
    public void howIsTheWetherTodayDefaultCity() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_frankfurt.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "Frankfurt+am+Main")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        String response = weatherskill.handle("wie wird das wetter heute");
        assertEquals("Heute ist Mäßig bewölkt angesagt in Frankfurt am Main", response);
    }

    @Test
    public void howIsTheWetherTodayBerlin() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_berlin.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "berlin")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200).withHeader(
                "Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        String response = weatherskill.handle("wie wird das wetter heute in berlin");

        assertEquals("Heute ist Ein paar Wolken angesagt in berlin", response);
    }

    @Test
    public void howIsTheWetherTomorrowDefaultCity() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_frankfurt.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_frankfurt.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "Frankfurt+am+Main")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("wie wird das wetter morgen");
        assertEquals("Morgen ist Leichter Regen angesagt in Frankfurt am Main", response);
    }

    @Test
    public void howIsTheWetherTomorrowBerlin() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_berlin.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_berlin.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "berlin")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200).withHeader(
                "Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("wie wird das wetter morgen in berlin");

        assertEquals("Morgen ist Leichter Regen angesagt in berlin", response);
    }

    @Test
    public void howIsTheWetherTodayAt10DefaultCity() throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.set(Calendar.HOUR, 10);
        calendar.set(Calendar.MINUTE, 40);

        final String JSON_FILE_PATH = "weatherskill/cloudy_frankfurt.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_frankfurt.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT, calendar);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "Frankfurt+am+Main")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("wie wird das wetter um zehn");

        assertEquals("Um 10 Uhr  ist Ein paar Wolken angesagt in Frankfurt am Main", response);
    }

    @Test
    public void howIsTheWetherTodayAt11Berlin() throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 43);

        final String JSON_FILE_PATH = "weatherskill/cloudy_berlin.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_berlin.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT, calendar);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "berlin")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200).withHeader(
                "Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("wie wird das wetter um 12 in berlin");
        assertEquals("Um 12 Uhr  ist Mäßig bewölkt angesagt in berlin", response);
    }

    @Test
    public void isItSunnyTodayDefaultCity_resultNo() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_frankfurt.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_frankfurt.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "Frankfurt+am+Main")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("scheint heute die Sonne");
        assertThat(response, anyOf(is("Nein, es scheint die Sonne heute nicht in Frankfurt am Main."), is(
            "Heute scheint die Sonne es nicht in Frankfurt am Main.")));
    }

    @Test
    public void isItSunnyTodayBerlin_resultNo() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_berlin.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_berlin.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "berlin")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200).withHeader(
                "Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("scheint heute die Sonne in berlin");
        assertThat(response, anyOf(is("Nein, es scheint die Sonne heute nicht in berlin."), is(
            "Heute scheint die Sonne es nicht in berlin.")));
    }

    @Test
    public void doesItRainTodayDefaultCity_resultNo() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_frankfurt.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_frankfurt.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "Frankfurt+am+Main")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("regnet es heute");
        assertThat(response, anyOf(is("Nein, es regnet heute nicht in Frankfurt am Main."), is(
            "Heute regnet es nicht in Frankfurt am Main.")));
    }

    @Test
    public void doesItRainTodayBerlin_resultNo() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_berlin.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_berlin.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "berlin")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200).withHeader(
                "Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("regnet es heute in berlin");
        assertThat(response, anyOf(is("Nein, es regnet heute nicht in berlin."), is("Heute regnet es nicht in berlin.")));
    }

    @Test
    public void howWarmIsTodayItDefaultCity() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_frankfurt.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_frankfurt.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "Frankfurt+am+Main")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("wie warm wird es");
        assertEquals(
            "Aktuel hat es 21.88 Grad die Höchstemperatur liegt heute bei 24.3, der niedrigste Wert bei 14.79 Grad in Frankfurt am Main.",
            response);
    }

    @Test
    public void howWarmIsItTodayBerlin() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_berlin.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_berlin.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "berlin")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200).withHeader(
                "Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("wie warm wird es in berlin");
        assertEquals(
            "Aktuel hat es 22.77 Grad die Höchstemperatur liegt heute bei 22.28, der niedrigste Wert bei 15.41 Grad in berlin.",
            response);
    }

    @Test
    public void howColdIsTodayItDefaultCity() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_frankfurt.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_frankfurt.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "Frankfurt+am+Main")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("wie kalt wird es");
        assertEquals(
            "Aktuel hat es 21.88 Grad die Höchstemperatur liegt heute bei 24.3, der niedrigste Wert bei 14.79 Grad in Frankfurt am Main.",
            response);
    }

    @Test
    public void howColdIsItTodayBerlin() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_berlin.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_berlin.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "berlin")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200).withHeader(
                "Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("wie kalt wird es in berlin");
        assertEquals(
            "Aktuel hat es 22.77 Grad die Höchstemperatur liegt heute bei 22.28, der niedrigste Wert bei 15.41 Grad in berlin.",
            response);
    }

    @Test
    public void doesItRainTomorrowDefaultCity_resultYes() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_frankfurt.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_frankfurt.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "Frankfurt+am+Main")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("regnet es morgen");

        assertThat(response, anyOf(is("Morgen gibt es Leichter Regen  in Frankfurt am Main"), is(
            "Morgen soll Leichter Regen geben, in Frankfurt am Main")));
    }

    @Test
    public void doesItRainTomorrowBerlin_resultYes() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_berlin.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_berlin.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "berlin")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200).withHeader(
                "Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("regnet es morgen in berlin");

        assertThat(response, anyOf(is("Morgen gibt es Leichter Regen  in berlin"), is(
            "Morgen soll Leichter Regen geben, in berlin")));
    }

    @Test
    public void whenIsSunriseDefaultCity() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_frankfurt.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_frankfurt.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "Frankfurt+am+Main")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("wann geht die Sonne auf");
        assertEquals("Die Sonne geht um 06 uhr 05 auf in Frankfurt am Main", response);
    }

    @Test
    public void whenIsSunriseAlternativePhraseDefaultCity() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_frankfurt.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_frankfurt.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "Frankfurt+am+Main")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("wann ist Sonnenaufgang");
        assertEquals("Die Sonne geht um 06 uhr 05 auf in Frankfurt am Main", response);
    }

    @Test
    public void whenIsSunriseBerlin() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_berlin.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_berlin.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "berlin")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200).withHeader(
                "Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("wann geht die Sonne auf in berlin");
        assertEquals("Die Sonne geht um 05 uhr 39 auf in berlin", response);
    }

    @Test
    public void whenIsSunsetDefaultCity() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_frankfurt.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_frankfurt.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "Frankfurt+am+Main")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("wann geht die Sonne unter");
        assertEquals("Die Sonne geht um 20 uhr 55 unter in Frankfurt am Main", response);
    }

    @Test
    public void whenIsSunsetAlternativePhraseDefaultCity() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_frankfurt.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_frankfurt.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "Frankfurt+am+Main")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("wann ist Sonnenuntergang");
        assertEquals("Die Sonne geht um 20 uhr 55 unter in Frankfurt am Main", response);
    }

    @Test
    public void whenIsSunsetBerlin() throws IOException {
        final String JSON_FILE_PATH = "weatherskill/cloudy_berlin.json";
        final String JSON_FILE_PATH_FORECAST = "weatherskill/forecast_berlin.json";
        final int PORT = wireMockRule.port();
        final WeatherSkill weatherskill = new WeatherSkill(HOST + ":" + PORT);

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/weather")).withQueryParam("q", WireMock.equalTo(
            "berlin")).withQueryParam("appid", WireMock.notMatching("")).willReturn(aResponse().withStatus(200).withHeader(
                "Content-Type", "application/json; charset=utf-8").withBodyFile(JSON_FILE_PATH)));

        wireMockRule.stubFor(get(WireMock.urlPathEqualTo("/data/2.5/onecall")).withQueryParam("appid", WireMock.notMatching(
            "")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile(JSON_FILE_PATH_FORECAST)));

        String response = weatherskill.handle("wann geht die Sonne unter in berlin");
        assertEquals("Die Sonne geht um 20 uhr 44 unter in berlin", response);

    }
}
