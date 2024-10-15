package api.weather;

import api.APIRequestHandler;
import api.geocoding.GeocodingResponse;
import api.geocoding.OpenMeteoGeocodingAPI;
import api.weather.response.TemperatureData;
import api.weather.response.WeatherResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class OpenMeteoAPI extends WeatherAPI {
    private final String API_URL = "https://api.open-meteo.com/v1/forecast";

    String cityName = "";

    @Override
    public WeatherResponse getWeather(String city) {
        try {
            Optional<GeocodingResponse> geocoding = OpenMeteoGeocodingAPI.getGeocoding(city);
            if (geocoding.isEmpty())
                return new WeatherResponse("Город не найден");
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
            return new WeatherResponse(e.getMessage());
        }
    }

    private WeatherResponse parseApiResponse(String response) {
        JSONObject jsonObject = new JSONObject(response);

        var current = jsonObject.getJSONObject("current");
        var temperature = current.getDouble("temperature_2m");
        var windSpeed = current.getDouble("wind_speed_10m");
        var windGusts = current.getDouble("wind_gusts_10m");
        var relativeHumidity = current.getDouble("relative_humidity_2m");
        var weatherCode = current.getInt("weather_code");
        var weatherDescription = getDescriptionFromWmoCode(weatherCode, true);

        return WeatherResponse.builder()
                .city(cityName)
                .temperature(TemperatureData.builder()
                        .temperature(temperature)
                        .windSpeed(windSpeed)
                        .windGusts(windGusts)
                        .humidity(relativeHumidity)
                        .description(weatherDescription)
                        .build())
                .build();
    }
}
