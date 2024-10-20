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

    private static Response handle(String commandString, User user) {
        if (commandString == null || commandString.isEmpty()) {
            return new Response("Команда не найдена");
        }

        String command = commandString.toLowerCase().split(" ")[0];
        String arguments = getArguments(commandString);

        WeatherAPI weatherAPI = new OpenMeteoAPI();

        switch (command) {
            case "/weather":
                WeatherResponse weather = weatherAPI.getWeather(arguments);
                String response = getFormattedWeatherString(weather);
                return Response.builder()
                        .text(response)
                        .weatherResponse(weather)
                        .build();
            default:
                return new Response("Команда не найдена");
        }
    }

    private static String getArguments(String commandString) {
        return Arrays.stream(commandString.split(" "))
                .skip(1)
                .collect(Collectors.joining(" "));
    }

    private static Response handle(ButtonCommand command, User user, long callbackMessageId) {

        WeatherAPI weatherAPI = new OpenMeteoAPI();

        switch (command) {
            case WEATHER_NOW -> {

                Optional<Integer> lastRequestGeocoding = RequestHistoryDao.getRequestGeocoding(callbackMessageId);
                if (lastRequestGeocoding.isPresent()) {
                    var weather = weatherAPI.getWeather(lastRequestGeocoding.get());
                    String responseText = getFormattedWeatherString(weather);
                    Response response = Response.builder()
                            .weatherResponse(weather)
                            .text(responseText)
                            .build();


                    return response;


                } else new Response("Команда не найдена");


            }
            case WEATHER_TODAY -> {

            }
            case REPEAT_LAST_REQUEST -> {

            }
            case WEATHER_7_DAYS -> {

            }

            case null, default -> {
                return new Response("Команда не найдена");
            }
        }
        return new Response("Команда не найдена");
    }

    private static String getFormattedWeatherString(WeatherResponse weather) {
        if (weather.getError() != null) {
            return weather.getError();
        }
        String str = "%s\n%s %2.1f C\nВлажность %2.0f%%\nВетер %s м/с\nПорывы %s м/с";
        TemperatureData temperature = weather.getTemperature();
        return str.formatted(weather.getGeocoding().getCity(), temperature.getDescription(), temperature.getTemperature(), temperature.getHumidity(), temperature.getWindSpeed(), temperature.getWindGusts());
    }

    public static Response getResponse(String messageText, User user) {
        return handle(messageText, user);
    }

    public static void writeHistory(String request, Response response, User user, long telegramMessageId) {
        int geocodingId = -1;
        WeatherResponse weatherResponse = response.getWeatherResponse();
        if (weatherResponse != null && weatherResponse.getError() == null) {
            geocodingId = weatherResponse.getGeocoding().getId();
        }
        RequestHistoryDao.insert(request, response.getText(), user, geocodingId, telegramMessageId);
    }

    public static Response getResponse(ButtonCommand buttonCommand, User user, long callbackMessageId) {
        return handle(buttonCommand, user, callbackMessageId);
    }
}
