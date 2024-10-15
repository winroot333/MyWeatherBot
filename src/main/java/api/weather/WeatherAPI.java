package api.weather;

import api.weather.response.WeatherResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class WeatherAPI {
    public abstract WeatherResponse getWeather(String city);

    protected String getDescriptionFromWmoCode(int code, boolean day) {
        String wmoJsonPath = "src/main/resources/wmo_weather_codes.json";
        try {
            String wmoCodeJson = Files.readString(Path.of(wmoJsonPath));
            JSONObject jsonObject = new JSONObject(wmoCodeJson);
            return jsonObject.getJSONObject(String.valueOf(code))
                    .getJSONObject(day ? "day" : "night")
                    .getJSONObject("description")
                    .getString("ru");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
