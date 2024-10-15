package jdbc;

import config.Config;
import lombok.Cleanup;
import lombok.SneakyThrows;

import java.sql.*;

public class ConnectionManager {
    @SneakyThrows
    public static Connection getConnection() {
        String jdbcUrl = Config.getProperty("sql.jdbc_url");
        String username = Config.getProperty("sql.username");
        String password = Config.getProperty("sql.password");
        return DriverManager.getConnection(jdbcUrl, username, password);
    }
}
