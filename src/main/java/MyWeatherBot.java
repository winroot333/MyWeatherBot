import api.weather.OpenMeteoAPI;
import config.Config;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegram.TelegramBot;

public class MyWeatherBot {
    public static void main(String[] args) {
//        System.out.println(Config.getProperty("db.user"));
//        System.out.println(new OpenMeteoAPI().getWeather("санкт-петербург"));
//        Запуск бота
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TelegramBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
