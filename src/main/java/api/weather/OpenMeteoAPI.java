package api.weather;

import api.APIRequestHandler;
import api.geocoding.GeocodingResponse;
import api.geocoding.OpenMeteoGeocodingAPI;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class OpenMeteoAPI implements WeatherAPI {
    private final String API_URL = "https://api.open-meteo.com/v1/forecast";
    String cityName = "";

    @Override
    public String getWeather(String city) {
        try {
            Optional<GeocodingResponse> geocoding = OpenMeteoGeocodingAPI.getGeocoding(city);
            if (geocoding.isEmpty())
                return "Город не найден";
            cityName = geocoding.get().getCity();

            String hourly = "temperature_2m,precipitation_probability,precipitation,rain,wind_speed_10m,wind_gusts_10m,relative_humidity_2m";
            URI uri = new URIBuilder(API_URL)
                    .addParameter("latitude", geocoding.get().getLatitude().toString())
                    .addParameter("longitude", geocoding.get().getLongitude().toString())
                    .addParameter("daily", "weather_code")
                    .addParameter("wind_speed_unit", "ms")
                    .addParameter("hourly", hourly)
                    .addParameter("timezone", "Europe/Moscow")
                    .build();

            String response = APIRequestHandler.request(uri);
            return parseApiResponse(response);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (HttpResponseException e) {
            return e.getMessage();
        }
    }

    private String parseApiResponse(String response) {
        JSONObject jsonObject = new JSONObject(response);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00");
        String currentTimeString = now.format(formatter);
        int timeIndex = 0;

        JSONArray jsonArray = jsonObject.getJSONObject("hourly").getJSONArray("time");
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.getString(i).equals(currentTimeString)) {
                timeIndex = i;
                break;
            }
        }

        double temperature = jsonObject.getJSONObject("hourly").getJSONArray("temperature_2m").getDouble(timeIndex);
        double humidity = jsonObject.getJSONObject("hourly").getJSONArray("relative_humidity_2m").getDouble(timeIndex);
        double windSpeed = jsonObject.getJSONObject("hourly").getJSONArray("wind_speed_10m").getDouble(timeIndex);

        return "%s\n%2.1f C\nВлажность %2.0f%%\nВетер %s м/с".formatted(cityName, temperature, humidity, windSpeed);
    }
}
