package api.weather;

import api.APIRequestHandler;
import api.weather.response.TemperatureData;
import api.weather.response.WeatherResponse;
import config.Config;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class OpenWeatherMapAPI  {
    private final String API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private final String API_KEY;

    public OpenWeatherMapAPI() {
        API_KEY = getApiKey();
    }

//    @Override
    public WeatherResponse getWeather(String city) {
        try {
            URI uri = new URIBuilder(API_URL)
                    .addParameter("q", city)
                    .addParameter("appid", API_KEY)
                    .addParameter("units", "metric")
                    .addParameter("lang", "ru")
                    .build();
            String response = APIRequestHandler.request(uri);
            return parseApiResponse(response);

        } catch (HttpResponseException e) {
            return new WeatherResponse(e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private WeatherResponse parseApiResponse(String response) {
        JSONObject jsonObject = new JSONObject(response);
        String city = jsonObject.getString("name");
        double temperature = jsonObject.getJSONObject("main").getDouble("temp");
        double humidity = jsonObject.getJSONObject("main").getDouble("humidity");
        double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
        double windDegrees = jsonObject.getJSONObject("wind").getDouble("deg");

        return WeatherResponse.builder()
                .temperature(TemperatureData.builder()
                        .temperature(temperature)
                        .windSpeed(windSpeed)
                        .build())
                .build();
    }

    private String getApiKey() {
        return Config.getProperty("api.openweathermap");
    }

}
