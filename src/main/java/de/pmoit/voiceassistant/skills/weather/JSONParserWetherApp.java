package de.pmoit.voiceassistant.skills.weather;

import org.json.JSONArray;
import org.json.JSONObject;


public class JSONParserWetherApp {
    private JSONObject obj;

    public JSONParserWetherApp(String jsonString) {
        obj = new JSONObject(jsonString);
    }

    public double parseWetherResultsAsDouble(String topElement, String subElement) {
        JSONObject jsonObject = obj.getJSONObject(topElement);
        return jsonObject.getDouble(subElement);
    }

    public String parseWetherResultsAsString(String topElement, String subElement) {
        JSONObject jsonObject = obj.getJSONObject(topElement);
        return jsonObject.getString(subElement);
    }

    public String parseWetherResultsAsArray(String jsonString, String arrayTerm) {
        JSONArray arr = obj.getJSONArray(jsonString);
        return handleAllArrayResults(arr, arrayTerm);
    }

    private String handleAllArrayResults(JSONArray arr, String arrayTerm) {
        return arr.getJSONObject(0).getString(arrayTerm);
    }

    public Double getDailyDataDouble(int day, String topterm, String subterm) {
        JSONArray arr = obj.getJSONArray("daily");
        return arr.getJSONObject(day).getJSONObject(topterm).getDouble(subterm);
    }

    public String getDailyDataString(int day, String topterm, String subterm) {
        JSONArray arr = obj.getJSONArray("daily");
        return arr.getJSONObject(day).getJSONObject(topterm).getString(subterm);
    }

    public String getDailyWetherResult(int day) {
        JSONArray arr = obj.getJSONArray("daily");
        JSONArray weatherArray = arr.getJSONObject(day).getJSONArray("weather");
        return weatherArray.getJSONObject(0).getString("description");
    }

    public String getHourlyWetherResult(int day) {
        JSONArray arr = obj.getJSONArray("hourly");
        JSONArray weatherArray = arr.getJSONObject(day).getJSONArray("weather");
        return weatherArray.getJSONObject(0).getString("description");
    }
}
