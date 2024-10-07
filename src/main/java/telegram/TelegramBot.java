package telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TelegramBot extends TelegramLongPollingBot {

    private final String BOT_USER_NAME = "MyWeather_31bot";
    private final String TOKEN;
    private final String TOKEN_PATH = "src/main/resources/config/telegram_API_key.txt";

    public TelegramBot() {
        try {
            TOKEN = Files.readString(Path.of(TOKEN_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            sendMessage(chatId, CommandHandler.getResponse(messageText));
        }
    }

    private void sendMessage(long chatId, String text) {

        SendMessage message = new SendMessage(String.valueOf(chatId), text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_USER_NAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }
}
