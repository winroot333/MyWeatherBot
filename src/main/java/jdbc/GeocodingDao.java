package jdbc;

import api.geocoding.GeocodingResponse;
import lombok.Cleanup;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class GeocodingDao {
    @SneakyThrows
    public static Optional<GeocodingResponse> getGeocoding(String query) {
        @Cleanup Connection connection = ConnectionManager.getConnection();
        String geocodingRequestSql = "SELECT id, query, geocoding_data_id FROM geocoding_request WHERE query = ?";
        @Cleanup PreparedStatement stmt = connection.prepareStatement(geocodingRequestSql);
        stmt.setString(1, query);
        ResultSet resultSet = stmt.executeQuery();
        if (resultSet.next()) {
            int geocodingDataId = resultSet.getInt("geocoding_data_id");
            return getGeocodingById(geocodingDataId);

        }
        return Optional.empty();
    }

    @SneakyThrows
    public static Optional<GeocodingResponse> getGeocoding(int geocodingId) {
        return getGeocodingById(geocodingId);
    }

    @SneakyThrows
    private static Optional<GeocodingResponse> getGeocodingById(int geocodingDataId) {
        @Cleanup Connection connection = ConnectionManager.getConnection();
        String geocodingSql = "SELECT * FROM geocoding_data WHERE id = ?";
        @Cleanup PreparedStatement stmt2 = connection.prepareStatement(geocodingSql);
        stmt2.setInt(1, geocodingDataId);
        ResultSet resultSet = stmt2.executeQuery();
        if (resultSet.next()) {
            return getResponseOptional(resultSet);
        } else {
            return Optional.empty();
        }
    }

    private static Optional<GeocodingResponse> getResponseOptional(ResultSet resultSet) throws SQLException {
        return Optional.of(GeocodingResponse.builder()
                .city(resultSet.getString("city"))
                .cityId(resultSet.getInt("city_id"))
                .country(resultSet.getString("country"))
                .latitude(resultSet.getDouble("latitude"))
                .longitude(resultSet.getDouble("longitude"))
                .id(resultSet.getInt("id"))
                .build());
    }

    @SneakyThrows
    public static int insertGeocoding(GeocodingResponse geocoding, String query) {
        @Cleanup Connection connection = ConnectionManager.getConnection();
        int geocodingDataId = getGeocodingDataId(geocoding.getCityId(), geocoding.getCity());
        //Если не нашли то вставляем
        if (geocodingDataId == -1) {
            String sql = "INSERT INTO geocoding_data (longitude, latitude, city, city_id, country) VALUES (?, ?, ?,?,?);";
            @Cleanup PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDouble(1, geocoding.getLongitude());
            stmt.setDouble(2, geocoding.getLatitude());
            stmt.setString(3, geocoding.getCity());
            stmt.setLong(4, geocoding.getCityId());
            stmt.setString(5, geocoding.getCountry());
            stmt.executeUpdate(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            //Добавляем добавленный id из таблицы
            if (stmt.getGeneratedKeys().next()) {
                geocodingDataId = stmt.getGeneratedKeys().getInt("id");
            }
        }

        String insertGeocodingRequestSql = "INSERT INTO geocoding_request (query, geocoding_data_id) VALUES (?, ?)";
        @Cleanup PreparedStatement stmt2 = connection.prepareStatement(insertGeocodingRequestSql);
        stmt2.setString(1, query);
        stmt2.setLong(2, geocodingDataId);
        stmt2.executeUpdate();

        return geocodingDataId;

    }

    @SneakyThrows
    private static int getGeocodingDataId(long cityId, String city) {
        @Cleanup Connection connection = ConnectionManager.getConnection();
        String geocodingDataQuerySql = "SELECT id FROM geocoding_data where city_id = ? AND city = ?";
        @Cleanup PreparedStatement stmt = connection.prepareStatement(geocodingDataQuerySql);
        stmt.setLong(1, cityId);
        stmt.setString(2, city);
        ResultSet resultSet = stmt.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("id");
        }
        return -1;
    }
}
