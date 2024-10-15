package api.geocoding;

import jdbc.GeocodingDao;

import java.util.Optional;

public class GeocodingService {
    public static Optional<GeocodingResponse> getGeocoding(String cityQuery) {
        var geocodingFromDB = GeocodingDao.getGeocoding(cityQuery);
        if (geocodingFromDB.isEmpty()) {
            Optional<GeocodingResponse> geocoding = OpenMeteoGeocodingAPI.getGeocoding(cityQuery);
            GeocodingDao.insertGeocoding(geocoding.get(), cityQuery);
            return geocoding;
        }
        return geocodingFromDB;
    }
}
