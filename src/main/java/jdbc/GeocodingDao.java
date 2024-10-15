package jdbc;

import api.geocoding.GeocodingResponse;
import lombok.Cleanup;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class GeocodingDao {
    @SneakyThrows
    public static Optional<GeocodingResponse> getGeocoding(String query) {
        @Cleanup Connection connection = ConnectionManager.getConnection();
        String geocodingRequestSql = "SELECT query, geocoding_data_id FROM geocoding_request WHERE query = ?";
        @Cleanup PreparedStatement stmt = connection.prepareStatement(geocodingRequestSql);
        stmt.setString(1, query);
        ResultSet resultSet = stmt.executeQuery();
        if (resultSet.next()) {
            String geocodingSql = "SELECT * FROM geocoding_data WHERE id = ?";
            @Cleanup PreparedStatement stmt2 = connection.prepareStatement(geocodingSql);
            stmt2.setInt(1, resultSet.getInt("geocoding_data_id"));
            resultSet = stmt2.executeQuery();
            if (resultSet.next()) {
                return Optional.of(GeocodingResponse.builder()
                        .city(resultSet.getString("city"))
                        .cityId(resultSet.getInt("city_id"))
                        .country(resultSet.getString("country"))
                        .latitude(resultSet.getDouble("latitude"))
                        .longitude(resultSet.getDouble("longitude"))
                        .build());
            } else {
                return Optional.empty();
            }

        }
        return Optional.empty();
    }

    @SneakyThrows
    public static void insertGeocoding(GeocodingResponse geocoding, String query) {
        @Cleanup Connection connection = ConnectionManager.getConnection();
        long geocodingDataId = getGeocodingDataId(geocoding.getCityId(), geocoding.getCity());
        //Если не нашли то вставляем
        if (geocodingDataId == -1){
            String insertGeocodingDataSql = "INSERT INTO geocoding_data (longitude, latitude, city, city_id, country) VALUES (?, ?, ?,?,?);";
            @Cleanup PreparedStatement stmt = connection.prepareStatement(insertGeocodingDataSql);
            stmt.setDouble(1, geocoding.getLongitude());
            stmt.setDouble(2, geocoding.getLatitude());
            stmt.setString(3, geocoding.getCity());
            stmt.setLong(4, geocoding.getCityId());
            stmt.setString(5, geocoding.getCountry());
            stmt.executeUpdate();
            geocodingDataId = getGeocodingDataId(geocoding.getCityId(), geocoding.getCity());
        }

        String insertGeocodingRequestSql = "INSERT INTO geocoding_request (query, geocoding_data_id) VALUES (?, ?)";
        @Cleanup PreparedStatement stmt2 = connection.prepareStatement(insertGeocodingRequestSql);
        stmt2.setString(1, query);
        stmt2.setLong(2, geocodingDataId);
        stmt2.executeUpdate();

    }

    @SneakyThrows
    private static long getGeocodingDataId(long cityId, String city){
        @Cleanup Connection connection = ConnectionManager.getConnection();
        String geocodingDataQuerySql = "SELECT id FROM geocoding_data where city_id = ? AND city = ?";
        @Cleanup PreparedStatement stmt = connection.prepareStatement(geocodingDataQuerySql);
        stmt.setLong(1, cityId);
        stmt.setString(2, city);
        ResultSet resultSet = stmt.executeQuery();
        if (resultSet.next()) {
            return resultSet.getLong("id");
        }
        return -1;
    }
}
