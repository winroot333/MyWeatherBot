package api.weather.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Builder
@Getter
@Value
public class TemperatureData {
    String time;
    double temperature;
    int weatherCode;
    String description;
    double apparentTemperature;
    double humidity;
    double windSpeed;
    double windDirection;
    double windGusts;
    double precipitationProbability;
    double precipitation;
}
