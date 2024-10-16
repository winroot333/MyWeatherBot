package telegram;

import config.Config;
import jdbc.RequestHistoryDao;
import jdbc.User;
import jdbc.UserDao;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TelegramBot extends TelegramLongPollingBot {

    private final String BOT_USER_NAME;
    private final String TOKEN;

    public TelegramBot() {
        TOKEN = Config.getProperty("telegram.apikey");
        BOT_USER_NAME = Config.getProperty("telegram.username");
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String response = CommandHandler.getResponse(messageText);
            // Записываем историю
            User user = getUser(update);
            RequestHistoryDao.insert(messageText, response, user);
            //отправляем ответ
//            sendInlineKeyboard(chatId);
            sendMessage(chatId, response);
        } else if (update.hasCallbackQuery()) {
            String callbackQuery = update.getCallbackQuery().getData();
            System.out.println("Нажата кнопка " + callbackQuery);

        }
    }

    private User getUser(Update update) {
        long userId = update.getMessage().getFrom().getId();
        User user = UserDao.getUserByTelegramId(userId);
        if (user == null) {
            String firstName = update.getMessage().getFrom().getFirstName();
            String userName = update.getMessage().getFrom().getUserName();
            String Name = firstName.isEmpty() ? userName : firstName;
            long chatId = update.getMessage().getChatId();
            user = User.builder()
                    .telegramUserId(userId)
                    .name(Name)
                    .chatId(chatId)
                    .build();
            user = UserDao.addUser(user);

        }

        return user;
    }

    @SneakyThrows
    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        execute(message);
    }

    //TODO сделать кнопки прогноза погоды по часам, по 15мин, на 5 дней
    //TODO автообновление прогноза погоды в чате
    @SneakyThrows
    private void sendInlineKeyboard(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("Погода сейчас").callbackData("button1").build());
        row1.add(InlineKeyboardButton.builder().text("Прогноз на сегодня").callbackData("button1").build());
        row1.add(InlineKeyboardButton.builder().text("Прогноз на 7 дней").callbackData("button2").build());

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row1);

        inlineKeyboardMarkup.setKeyboard(rows);

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
//                .text("Выберите кнопку:")
                .replyMarkup(inlineKeyboardMarkup)
                .build();

        execute(message);

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
