package telegram;

import lombok.Getter;

@Getter
public enum ButtonCommand {
    WEATHER_NOW("weatherNow"),
    REPEAT_LAST_REQUEST("repeatLastRequest"),
    WEATHER_TODAY("weatherToday"),
    WEATHER_7_DAYS("weather7Days");

    private final String command;

    ButtonCommand(String command) {
        this.command = command;
    }

    public static ButtonCommand fromString(String command) {
        for (ButtonCommand buttonCommand : ButtonCommand.values()) {
            if (buttonCommand.getCommand().equalsIgnoreCase(command)) {
                return buttonCommand;
            }
        }
        throw new IllegalArgumentException("No enum constant for command: " + command);


    }
}
