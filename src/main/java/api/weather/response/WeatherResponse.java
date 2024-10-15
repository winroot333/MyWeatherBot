package api.weather.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class WeatherResponse {
    private String error;
    private String city;
    private String country;
    private String timeStringFormat = "yyyy-MM-dd'T'HH:mm";
    private TemperatureData temperature;
    private TemperatureData[] temperatureForecast;

    public WeatherResponse(String error) {
        this.error = error;
    }
}
