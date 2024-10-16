package jdbc;

import lombok.Cleanup;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class RequestHistoryDao {
    @SneakyThrows
    public static void insert(String request, String response, User user) {
        @Cleanup Connection connection = ConnectionManager.getConnection();
        String sql = """
                        INSERT INTO request_history(request, response, user_id, timestamp)
                        VALUES (?, ?, ?, LOCALTIMESTAMP)
                """;
        @Cleanup PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, request);
        stmt.setString(2, response);
        stmt.setInt(3, user.getId());
        stmt.executeUpdate();
    }
}

