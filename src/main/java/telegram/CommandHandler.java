package telegram;

import api.weather.OpenWeatherMapAPI;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandHandler {

    static String[] parse(String message) {
        if (message.startsWith("/")) {
            return message.split(" ");
        }
        return null;
    }


    static String handle(String[] args) {
        if (args == null || args.length == 0) {
            return "Комманда не найдена";
        }

        String command = args[0].toLowerCase();

        switch (command) {
            case "/weather" -> {
                return new api.weather.OpenMeteoAPI().getWeather(
                        Arrays.stream(args)
                                .skip(1)
                                .collect(Collectors.joining(" ")));
            }
            case "/subscribe" -> {
                return "";
            }
            default -> {
                return "Комманда не найдена";
            }
        }
    }

    public static String getResponse(String messageText) {
        String[] args = parse(messageText);

        return handle(args);
    }
}
