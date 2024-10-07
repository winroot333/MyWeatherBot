package api.geocoding;

import api.APIRequestHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class OpenMeteoGeocodingAPI {
    private static final String API_URL = "https://geocoding-api.open-meteo.com/v1/search";

    public static Optional<GeocodingResponse> getGeocoding(String city) {
        try {
            String lang = (Character.UnicodeScript.of(city.charAt(0)) == Character.UnicodeScript.CYRILLIC) ? "ru" : "en";
            URI uri = new URIBuilder(API_URL)
                    .addParameter("name", city)
                    .addParameter("count", "1")
                    .addParameter("language", lang)
                    .build();
            String response = APIRequestHandler.request(uri);
            return parseApiResponse(response);

        } catch (HttpResponseException e) {
            return Optional.empty();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static Optional<GeocodingResponse> parseApiResponse(String response) {
        JSONObject json = new JSONObject(response);
        if (json.has("results")) {

            json = json.getJSONArray("results").getJSONObject(0);
            return Optional.of(new GeocodingResponse.Builder()
                    .setCity(json.getString("name"))
                    .setLongitude(json.getDouble("longitude"))
                    .setLatitude(json.getDouble("latitude"))
                    .build());
        } else
            return Optional.empty();
    }

}
