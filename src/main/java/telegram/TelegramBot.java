package telegram;

import config.Config;
import jdbc.RequestHistoryDao;
import jdbc.User;
import jdbc.UserDao;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
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
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();
                User user = getUser(update.getMessage().getFrom(), chatId);
                Response response = CommandHandler.getResponse(messageText, user);
                var inlineKeyboardMarkup = getInlineKeyboard();

                //отправляем ответ
                int messageId = sendMessage(chatId, response.getText(), inlineKeyboardMarkup);
                CommandHandler.writeHistory(messageText, response, user, messageId);


            } else if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                String callbackQueryData = callbackQuery.getData();

                var buttonCommand = ButtonCommand.fromString(callbackQueryData);
                long chatId = callbackQuery.getMessage().getChatId();
                long callbackMessageId = callbackQuery.getMessage().getMessageId();
                User user = getUser(callbackQuery.getFrom(), chatId);
                Response response = CommandHandler.getResponse(buttonCommand, user, callbackMessageId );
                var inlineKeyboardMarkup = getInlineKeyboard();

                //отправляем ответ
                int messageId = sendMessage(chatId, response.getText(), inlineKeyboardMarkup);
                execute(new AnswerCallbackQuery(callbackQuery.getId()));
                CommandHandler.writeHistory("button " + callbackQueryData, response, user, messageId);

            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    private User getUser(org.telegram.telegrambots.meta.api.objects.User telegramUser, long chatId) {
        long userId = telegramUser.getId();
        User user = UserDao.getUserByTelegramId(userId);
        if (user == null) {
            String firstName = telegramUser.getFirstName();
            String userName = telegramUser.getUserName();
            String Name = firstName.isEmpty() ? userName : firstName;
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
    private int sendMessage(long chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        message.setReplyMarkup(inlineKeyboardMarkup);
        Message execute = execute(message);
        return execute.getMessageId();
    }

    //TODO сделать кнопки прогноза погоды по часам, по 15мин, на 5 дней
    //TODO автообновление прогноза погоды в чате
    private InlineKeyboardMarkup getInlineKeyboard() {
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("Погода сейчас").callbackData("weatherNow").build());
//        row1.add(InlineKeyboardButton.builder().text("Повторить последний запрос").callbackData("repeatLastRequest").build());
//        row2.add(InlineKeyboardButton.builder().text("Прогноз на сегодня").callbackData("weatherToday").build());
//        row2.add(InlineKeyboardButton.builder().text("Прогноз на 7 дней").callbackData("weather7Days").build());

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
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
