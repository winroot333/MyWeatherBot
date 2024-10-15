package telegram;

import api.weather.OpenMeteoAPI;
import api.weather.OpenWeatherMapAPI;
import api.weather.WeatherAPI;
import api.weather.response.TemperatureData;
import api.weather.response.WeatherResponse;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandHandler {

    private static String handle(String commandString) {
        if (commandString == null || commandString.isEmpty()) {
            return "Команда не найдена";
        }

        String command = commandString.toLowerCase().split(" ")[0];
        String arguments = Arrays.stream(commandString.split(" "))
                .skip(1)
                .collect(Collectors.joining(" "));

        WeatherAPI weatherAPI = new OpenMeteoAPI();

        switch (command) {
            case "/weather":
                WeatherResponse weather = weatherAPI.getWeather(arguments);
                return getFormattedWeatherString(weather);
            case "/subscribe":
                return "";
            default:
                return "Команда не найдена";
        }
    }

    private static String getFormattedWeatherString(WeatherResponse weather) {
        if (weather.getError() != null) {
            return weather.getError();
        }
        String str = "%s\n%s %2.1f C\nВлажность %2.0f%%\nВетер %s м/с\nПорывы %s м/с";
        TemperatureData temperature = weather.getTemperature();
        return str.formatted(weather.getGeocoding().getCity(), temperature.getDescription(), temperature.getTemperature(), temperature.getHumidity(), temperature.getWindSpeed(), temperature.getWindGusts());
    }

    public static String getResponse(String messageText) {
        return handle(messageText);
    }
}
