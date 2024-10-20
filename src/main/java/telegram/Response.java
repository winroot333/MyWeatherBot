package telegram;

import api.weather.response.WeatherResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class Response {
    private String text;
    private WeatherResponse weatherResponse;

    Response(String text){
        this.text = text;
    }


}
