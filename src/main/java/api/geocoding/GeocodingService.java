package api.geocoding;

import jdbc.GeocodingDao;

import java.util.Optional;

public class GeocodingService {
    public static Optional<GeocodingResponse> getGeocoding(String cityQuery) {
        var geocodingFromDB = GeocodingDao.getGeocoding(cityQuery);
        if (geocodingFromDB.isEmpty()) {
            Optional<GeocodingResponse> geocoding = OpenMeteoGeocodingAPI.getGeocoding(cityQuery);
            if (geocoding.isPresent()) {
                int id = GeocodingDao.insertGeocoding(geocoding.get(), cityQuery);
                geocoding.get().setId(id);
                return geocoding;
            } else {
                return Optional.empty();
            }
        }
        return geocodingFromDB;
    }
    public static Optional<GeocodingResponse> getGeocoding(int id) {
        return GeocodingDao.getGeocoding(id);
    }

    }
