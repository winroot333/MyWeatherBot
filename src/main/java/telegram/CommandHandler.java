package telegram;

import api.weather.OpenMeteoAPI;
import api.weather.WeatherAPI;
import api.weather.response.TemperatureData;
import api.weather.response.WeatherResponse;
import jdbc.GeocodingDao;
import jdbc.RequestHistoryDao;
import jdbc.User;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandHandler {

    private static String handle(String commandString, User user) {
        if (commandString == null || commandString.isEmpty()) {
            return "Команда не найдена";
        }

        String command = commandString.toLowerCase().split(" ")[0];
        String arguments = getArguments(commandString);

        WeatherAPI weatherAPI = new OpenMeteoAPI();

        switch (command) {
            case "/weather":
                WeatherResponse weather = weatherAPI.getWeather(arguments);
                String response = getFormattedWeatherString(weather);
                    writeHistory(commandString, response, user, weather);

                return response;
            default:
                return "Команда не найдена";
        }
    }

    private static String getArguments(String commandString) {
        return Arrays.stream(commandString.split(" "))
                .skip(1)
                .collect(Collectors.joining(" "));
    }

    private static String handle(ButtonCommand command, User user) {

        WeatherAPI weatherAPI = new OpenMeteoAPI();

        switch (command) {
            case WEATHER_NOW -> {

                Optional<Integer> lastRequestGeocoding = RequestHistoryDao.getLastRequestGeocoding(user.getId(), true);
                if (lastRequestGeocoding.isPresent()) {
                    var weather = weatherAPI.getWeather(lastRequestGeocoding.get());
                    String response = getFormattedWeatherString(weather);

                        writeHistory(command.getCommand(), response, user, weather);


                    return response;


                }
                else return "Город не найден";


            }
            case WEATHER_TODAY -> {

            }
            case REPEAT_LAST_REQUEST -> {

            }
            case WEATHER_7_DAYS -> {

            }

            case null, default -> {
                return "Команда не найдена";
            }
        }
        return "Комманда не найдена";
    }

    private static String getFormattedWeatherString(WeatherResponse weather) {
        if (weather.getError() != null) {
            return weather.getError();
        }
        String str = "%s\n%s %2.1f C\nВлажность %2.0f%%\nВетер %s м/с\nПорывы %s м/с";
        TemperatureData temperature = weather.getTemperature();
        return str.formatted(weather.getGeocoding().getCity(), temperature.getDescription(), temperature.getTemperature(), temperature.getHumidity(), temperature.getWindSpeed(), temperature.getWindGusts());
    }

    public static String getResponse(String messageText, User user) {
        return handle(messageText, user);
    }

    private static void writeHistory(String request, String response, User user, WeatherResponse weatherResponse) {
        int geocodingId = -1;
        if (weatherResponse.getError() == null){
            geocodingId = weatherResponse.getGeocoding().getId();
        }
        RequestHistoryDao.insert(request, response, user, geocodingId);
    }

    public static String getResponse(ButtonCommand buttonCommand, User user) {
        return handle(buttonCommand, user);
    }
}
