package api.weather;

import api.APIRequestHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class OpenWeatherMapAPI implements WeatherAPI {
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast";
    private final String API_KEY;

    public OpenWeatherMapAPI() {
        API_KEY = getApiKey();
    }

    //    @Override
    public String getWeather(String city) {
        try {
            URI uri = new URIBuilder("https://api.openweathermap.org/data/2.5/weather")
                    .addParameter("q", city)
                    .addParameter("appid", API_KEY)
                    .addParameter("units", "metric")
                    .addParameter("lang", "ru")
                    .build();
            String response = APIRequestHandler.request(uri);
            return parseApiResponse(response);

        } catch (HttpResponseException e) {
            return e.getMessage();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String parseApiResponse(String response) {
        JSONObject jsonObject = new JSONObject(response);
        String city = jsonObject.getString("name");
        double temperature = jsonObject.getJSONObject("main").getDouble("temp");
        double humidity = jsonObject.getJSONObject("main").getDouble("humidity");
        double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
        double windDegrees = jsonObject.getJSONObject("wind").getDouble("deg");

        return "%s\n%2.1f C\n Влажность %2.0f\nВетер %s м/с".formatted(city, temperature, humidity, windSpeed);
    }

    private String getApiKey() {
        String keyPath = "src/main/resources/config/openweathermap_API_key.txt";
        String key = "";
        try {
            key = Files.readString(Path.of(keyPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return key;
    }

}
