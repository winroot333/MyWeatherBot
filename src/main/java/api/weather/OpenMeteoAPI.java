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

public class OpenMeteoAPI extends WeatherAPI {
    private final String API_URL = "https://api.open-meteo.com/v1/forecast";

    String cityName = "";

    @Override
    public String getWeather(String city) {
        try {
            Optional<GeocodingResponse> geocoding = OpenMeteoGeocodingAPI.getGeocoding(city);
            if (geocoding.isEmpty())
                return "Город не найден";
            cityName = geocoding.get().getCity();
            String current = "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,wind_speed_10m,wind_gusts_10m";
            String hourly = "temperature_2m,precipitation_probability,precipitation,rain,wind_speed_10m,wind_gusts_10m,relative_humidity_2m";
            URI uri = new URIBuilder(API_URL)
                    .addParameter("latitude", geocoding.get().getLatitude().toString())
                    .addParameter("longitude", geocoding.get().getLongitude().toString())
                    .addParameter("daily", "weather_code")
                    .addParameter("current", current)
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
//        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00");
//        String currentTimeString = now.format(formatter);
//        int timeIndex = 0;

//        JSONArray jsonArray = jsonObject.getJSONObject("hourly").getJSONArray("time");
//        for (int i = 0; i < jsonArray.length(); i++) {
//            if (jsonArray.getString(i).equals(currentTimeString)) {
//                timeIndex = i;
//                break;
//            }
//        }

        JSONObject current = jsonObject.getJSONObject("current");
        double temperature = current.getDouble("temperature_2m");
        double windSpeed = current.getDouble("wind_speed_10m");
        double windGusts = current.getDouble("wind_gusts_10m");
        double relativeHumidity = current.getDouble("relative_humidity_2m");
        int weatherCode = current.getInt("weather_code");
        String weatherDescription = getDescriptionFromWmoCode(weatherCode, true);


        return "%s\n%s %2.1f C\nВлажность %2.0f%%\nВетер %s м/с\nПорывы %s м/с".formatted(cityName,weatherDescription, temperature, relativeHumidity, windSpeed, windGusts);
    }
}
