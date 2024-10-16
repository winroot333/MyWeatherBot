package jdbc;

import lombok.Cleanup;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    @SneakyThrows
    public static User getUser(int id) {
        @Cleanup Connection connection = ConnectionManager.getConnection();
        String sql = "SELECT * FROM users WHERE id = ?";
        @Cleanup PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet resultSet = stmt.executeQuery();
        return CreateUserFromResultSet(resultSet);
    }

    //TODO проверкаа обновление chat_id у пользователя
    //TODO разобраться почему chat_id и telegram_user_id одинаковый в базе
    @SneakyThrows
    public static User getUserByTelegramId(long id) {
        @Cleanup Connection connection = ConnectionManager.getConnection();
        String sql = "SELECT * FROM users WHERE telegram_user_id = ?";
        @Cleanup PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, id);
        ResultSet resultSet = stmt.executeQuery();
        return CreateUserFromResultSet(resultSet);
    }


    @SneakyThrows
    private static User CreateUserFromResultSet(ResultSet resultSet) {
        if (resultSet.next()) {
            return User.builder()
                    .id(resultSet.getInt("id"))
                    .name(resultSet.getString("name"))
                    .chatId(resultSet.getLong("chat_id"))
                    .telegramUserId(resultSet.getLong("telegram_user_id"))
                    .status(resultSet.getInt("status"))
                    .build();
        }
        return null;
    }


    @SneakyThrows
    public static User addUser(User user) {
        @Cleanup Connection connection = ConnectionManager.getConnection();
        String sql = """
                    INSERT INTO users(name,status,chat_id,telegram_user_id)
                    VALUES (?, ?, ?, ?)
                """;
        @Cleanup PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, user.getName());
        stmt.setInt(2, user.getStatus());
        stmt.setLong(3, user.getChatId());
        stmt.setLong(4, user.getTelegramUserId());
        stmt.executeUpdate(sql, PreparedStatement.RETURN_GENERATED_KEYS);

        //Добавляем добавленный id из таблицы user
        if (stmt.getGeneratedKeys().next()) {
            user.setId(stmt.getGeneratedKeys().getInt("id"));
        }
        return user;
    }

}
