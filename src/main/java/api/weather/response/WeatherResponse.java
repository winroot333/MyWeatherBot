package api.weather.response;

import api.geocoding.GeocodingResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class WeatherResponse {
    private String error;
    private GeocodingResponse geocoding;
    private String timeStringFormat = "yyyy-MM-dd'T'HH:mm";
    private TemperatureData temperature;
    private TemperatureData[] temperatureForecast;

    public WeatherResponse(String error) {
        this.error = error;
    }
}
