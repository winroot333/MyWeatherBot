package jdbc;

import lombok.Cleanup;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class RequestHistoryDao {
    @SneakyThrows
    public static void insert(String request, String response, User user, int geocodingId, long telegramMessageId) {
        @Cleanup Connection connection = ConnectionManager.getConnection();
        String sql = """
                        INSERT INTO request_history(request, response, user_id, geocoding_id,telegram_message_id, timestamp)
                        VALUES (?, ?, ?, ?, ?,LOCALTIMESTAMP)
                """;
        @Cleanup PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, request);
        stmt.setString(2, response);
        stmt.setInt(3, user.getId());
        stmt.setInt(4, geocodingId);
        stmt.setLong(5, telegramMessageId);
        stmt.executeUpdate();
    }


    @SneakyThrows
    public static Optional<Integer> getLastRequestGeocoding(int userId, boolean correct) {
        @Cleanup Connection connection = ConnectionManager.getConnection();
        String sql = """
                    SELECT request, geocoding_id FROM request_history
                    WHERE user_id = ?
                    AND geocoding_id > ?
                    ORDER BY timestamp DESC
                    LIMIT 1;
                """;
        @Cleanup PreparedStatement stmt = connection.prepareStatement(sql);

        stmt.setInt(1, userId);
        stmt.setInt(2, correct ? 0 : -2);
        ResultSet resultSet = stmt.executeQuery();
        if (resultSet.next()) {
            return Optional.of(resultSet.getInt("geocoding_id"));
        }
        return Optional.empty();
    }

    @SneakyThrows
    public static Optional<Integer> getRequestGeocoding(long telegramMessageId) {
        @Cleanup Connection connection = ConnectionManager.getConnection();
        String sql = """
                    SELECT request, geocoding_id, telegram_message_id FROM request_history
                    WHERE telegram_message_id = ?
                    ORDER BY timestamp DESC
                    LIMIT 1;
                """;
        @Cleanup PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, telegramMessageId);
        ResultSet resultSet = stmt.executeQuery();
        if (resultSet.next()) {
            return Optional.of(resultSet.getInt("geocoding_id"));
        }
        return Optional.empty();
    }


}

